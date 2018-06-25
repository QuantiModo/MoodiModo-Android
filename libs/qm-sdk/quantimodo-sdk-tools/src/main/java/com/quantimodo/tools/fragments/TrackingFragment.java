package com.quantimodo.tools.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import com.koushikdutta.ion.Ion;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.quantimodo.android.sdk.model.*;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.R;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.adapters.AutoCompleteListAdapter;
import com.quantimodo.tools.adapters.UnitSpinnerAdapter;
import com.quantimodo.tools.adapters.VariableCategorySelectSpinnerAdapter;
import com.quantimodo.tools.sdk.DefaultSdkResponseListener;
import com.quantimodo.tools.sdk.request.GetCategoriesRequest;
import com.quantimodo.tools.sdk.request.GetPublicSuggestedVariablesRequest;
import com.quantimodo.tools.sdk.request.GetSuggestedVariablesRequest;
import com.quantimodo.tools.sdk.request.GetUnitsRequest;
import com.quantimodo.tools.sdk.request.SendMeasurementsRequest;
import com.quantimodo.tools.utils.ConvertUtils;
import com.quantimodo.tools.utils.CustomRemindersHelper;
import com.quantimodo.tools.utils.QtoolsUtils;
import com.quantimodo.tools.utils.ViewUtils;
import com.quantimodo.tools.utils.tracking.MeasurementCardHolder;
import com.quantimodo.tools.views.ScrollViewExt;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Used to show/search/submit measurements and variables
 * Require network access and user auth in Quantimodo
 *
 * Search can be narrowed to one category, check {@link #newInstance(int) newInstance(int)} method for more details
 */
public class TrackingFragment extends QFragment {

    EditText etVariableName;
    ListView lvVariableSuggestions;
    ProgressBar pbAutoCompleteLoading;
    LinearLayout lnCardsContainer;
    ScrollViewExt svCardsContainer;
    View vwVariableNameShadow;

    View lnAddVariableContainer;
    EditText etVariableNameNew;
    Spinner spVariableCategory;
    Spinner spVariableUnits;
    RadioGroup rgVariableCombinationOperation;

    UnitSpinnerAdapter unitAdapter;
    /**
     * Linear layout that contains the bottom buttons, when editing or creating a new measurement
     */
    LinearLayout lnButtons;

    @Inject
    ToolsPrefs mPrefs;

    private static final String KEY_TYPE = "type";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_SEARCH = "search";

    @IntDef({TYPE_ALL,TYPE_DIET,TYPE_MOOD,TYPE_SYMPTOMS,TYPE_TREATMENTS, TYPE_EMOTIONS, TYPE_PHYSICAL, TYPE_SLEEP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TrackingType{}

    /**
     * Category definition, that defines some params of tracking fragment like
     * <ul>
     *     <li>Action bar title</li>
     *     <li>Category name, that would used in search variables request</li>
     *     <li>Default value</li>
     *     <li>Default unit</li>
     *     <li>Hint for search variable edit input</li>
     *     <li>Default combine type of new variable</li>
     * </ul>
     *
     * There are several predefined CategoryDefs
     * <ul>
     *     <li>Diet &ndash; {@link #TYPE_DIET}</li>
     *     <li>Treatments &ndash; {@link #TYPE_TREATMENTS}</li>
     *     <li>Symptoms &ndash; {@link #TYPE_SYMPTOMS}</li>
     *     <li>Mood &ndash; {@link #TYPE_MOOD}</li>
     * </ul>
     */
    public static class CategoryDef implements Serializable{
        final String filter;
        final Double defaultValue;
        final String defaultAbbreviatedUnitName;
        final int hintId;
        final int titleId;
        final String combineType;

        /**
         * @param filter Category name
         * @param defaultValue Default value
         * @param defaultAbbreviatedUnitName Default unit
         * @param hintId hint resource
         * @param combineType combineType should be {@link com.quantimodo.android.sdk.SdkDefs#COMBINE_SUM} or {@link com.quantimodo.android.sdk.SdkDefs#COMBINE_MEAN}
         * @param titleId title for action bar
         */
        public CategoryDef(String filter, Double defaultValue, String defaultAbbreviatedUnitName, int hintId, String combineType,int titleId) {
            this.filter = filter;
            this.defaultValue = defaultValue;
            this.defaultAbbreviatedUnitName = defaultAbbreviatedUnitName;
            this.hintId = hintId;
            this.combineType = combineType;
            this.titleId = titleId;
        }


        public String getFilter() {
            return filter;
        }

        public Double getDefaultValue() {
            return defaultValue;
        }

        public String getDefaultAbbreviatedUnitName() {
            return defaultAbbreviatedUnitName;
        }

        public int getHintId() {
            return hintId;
        }
    }

    //this category has to be in the same orther of the TYPE_ constants
    private static final CategoryDef[] mCategoryFilter = {
            new CategoryDef(null,Double.NaN,"units", R.string.tracking_item_no_category,
                    null,R.string.tracking_fragment_no_category_title),
            new CategoryDef("Foods",1d,"serving", R.string.tracking_item_diet_question,
                    Variable.COMBINE_SUM,R.string.tracking_fragment_diet_title),
            new CategoryDef("Treatments",1d,"units", R.string.tracking_item_treatments_question,
                    Variable.COMBINE_SUM,R.string.tracking_fragment_treatments_title),
            new CategoryDef("Symptoms",1d,"%", R.string.tracking_item_symptoms_question,
                    Variable.COMBINE_MEAN,R.string.tracking_fragment_symptoms_title),
            new CategoryDef("Mood",0d,"serving", R.string.tracking_item_mood_question,
                    Variable.COMBINE_MEAN,R.string.tracking_fragment_mood_title),
            new CategoryDef("Emotions",1d,"%", R.string.tracking_item_emotions_question,
                    Variable.COMBINE_SUM,R.string.tracking_fragment_emotions_title),
            new CategoryDef("Physical Activity", 1d, "min", R.string.tracking_item_physical_question,
                    Variable.COMBINE_SUM, R.string.tracking_item_physical_title),
            new CategoryDef("Sleep",1d,"min",R.string.tracking_item_sleep_question, Variable.COMBINE_SUM,R.string.tracking_item_sleep_title)
    };

    public static final int TYPE_ALL = 0;
    public static final int TYPE_DIET = 1;
    public static final int TYPE_TREATMENTS = 2;
    public static final int TYPE_SYMPTOMS = 3;
    public static final int TYPE_MOOD = 4;
    public static final int TYPE_EMOTIONS = 5;
    public static final int TYPE_PHYSICAL = 6;
    public static final int TYPE_SLEEP = 7;

    private CategoryDef mCategoryDef;
    private int mType;

    // Adapter for the autocomplete listview
    private AutoCompleteListAdapter autoCompleteListAdapter;

    // QM API stuff
    private ArrayList<Variable> suggestedVariables = new ArrayList<>();              // Variables in autocomplete
    private ArrayList<Unit> mUnits;                            // All units from QM
    private ArrayList<VariableCategory> allCategories;           // All variable categories from QM

    // The variable the user selected
    Variable selectedVariable;
    int selectedDefaultUnitIndex;

    // All measurement cards currently visible
    ArrayList<MeasurementCardHolder> measurementCards = new ArrayList<>();

    private String mSearchText = null;

    /**
     * Creates new TrackingFragment
     * @param type category definition see {@link com.quantimodo.tools.fragments.TrackingFragment.CategoryDef CategoryDef} for more info
     * @return new instance of TrackingFragment
     */
    public static TrackingFragment newInstance(int type){
        return newInstance(type, "");
    }

    public static TrackingFragment newInstance(int type, String searchText){
        TrackingFragment fragment = new TrackingFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        args.putString(KEY_SEARCH, searchText);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QTools.getInstance().inject(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mType = TYPE_ALL;
        if (getArguments() != null){
            mType = getArguments().getInt(KEY_TYPE,TYPE_ALL);
            mSearchText = getArguments().getString(KEY_SEARCH, "");
        }
        mCategoryDef = mCategoryFilter[mType];


        ActionBar bar = getActivity().getActionBar();
        if(bar != null){
            bar.setTitle(mCategoryDef.titleId);
        }
    }

    // ButterKnife doesn't work on apklibs
    void initView(View view){
        etVariableName = (EditText) view.findViewById(R.id.etVariableName);
        lvVariableSuggestions = (ListView) view.findViewById(R.id.lvVariableSuggestions);
        pbAutoCompleteLoading = (ProgressBar) view.findViewById(R.id.pbAutoCompleteLoading);
        lnCardsContainer = (LinearLayout) view.findViewById(R.id.lnCardsContainer);
        svCardsContainer = (ScrollViewExt) view.findViewById(R.id.svCardsContainer);
        vwVariableNameShadow = view.findViewById(R.id.vwVariableNameShadow);

        lnAddVariableContainer = view.findViewById(R.id.lnAddVariableContainer);

        etVariableNameNew = (EditText) view.findViewById(R.id.etVariableNameNew);
        spVariableCategory = (Spinner) view.findViewById(R.id.spVariableCategory);
        spVariableUnits = (Spinner) view.findViewById(R.id.spVariableUnit);
        rgVariableCombinationOperation = (RadioGroup) view.findViewById(R.id.rgVariableCombinationOperation);

        lnButtons = (LinearLayout) view.findViewById(R.id.lnButtonContainer);

        etVariableName.setHint(mCategoryDef.hintId);

        view.findViewById(R.id.btSend).setOnClickListener(onBtSendClick);
//        View v = view.findViewById(R.id.btAddMeasurement);
//        v.setOnClickListener(onBtAddMeasurmentsClick);
//        v.setOnLongClickListener(onBtAddMeasurmentLongClick);

        lvVariableSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onVariableClick(parent, view, position, id);
            }
        });

        registerForContextMenu(lvVariableSuggestions);
    }

    void cleanViews(){
        unregisterForContextMenu(lvVariableSuggestions);
        etVariableName = null;
        lvVariableSuggestions = null;
        pbAutoCompleteLoading = null;
        lnCardsContainer = null;
        svCardsContainer = null;
        vwVariableNameShadow = null;

        lnAddVariableContainer = null;
        etVariableNameNew = null;
        spVariableCategory = null;
        rgVariableCombinationOperation = null;

        lnButtons = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.qmt_f_tracking, container,false);
        initView(view);

        // These listeners makes the autocomplete disappear/appear when you click outside/inside the variable name textbox
        view.setOnTouchListener(onRootViewFocusChanged);
        etVariableName.setOnFocusChangeListener(onVariableNameFocusChanged);

        // Add the "Add new variable" button to the bottom of the autocomplete suggestions
        View lvVariableSuggestionsFooter = inflater.inflate(R.layout.qmt_f_tracking_autocompletefooter, null);
        //Using Global.FOOTER for testing purposes, more intfo at
        // https://code.google.com/p/android-test-kit/wiki/EspressoSamples#Matching_a_view_that_is_a_footer/header_in_a_ListView
        lvVariableSuggestions.addFooterView(lvVariableSuggestionsFooter, ToolsPrefs.FOOTER, true);

        // Configure the autocomplete listview
        autoCompleteListAdapter = new AutoCompleteListAdapter(this.getActivity(),suggestedVariables);
        lvVariableSuggestions.setAdapter(autoCompleteListAdapter);

        // Add a textwatcher so that we can update our custom autocomplete when the user types
        etVariableName.addTextChangedListener(onVariableNameChanged);

        // Makes the shadow animate as you scroll the cards
        final float scrollDist = getResources().getDimensionPixelSize(R.dimen.scroll_dist_shadow);
        svCardsContainer.setScrollViewListener(new ScrollViewExt.ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
                if (y <= scrollDist) {
                    vwVariableNameShadow.setAlpha(y / scrollDist);
                } else {
                    vwVariableNameShadow.setAlpha(1);
                }
            }
        });

        // Creates the card that lets you add a new variable
        etVariableNameNew.setText(etVariableName.getText());


        // Load units/categories if they're not already loaded. Otherwise call xUpdated so that the spinners are filled
        loadAndInitData();

        return view;
    }

    @TrackingType
    public int getType() {
        return mType;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleanViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        etVariableName.setOnFocusChangeListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        etVariableName.setOnFocusChangeListener(onVariableNameFocusChanged);
    }

    private void loadAndInitData() {
        if(!QtoolsUtils.hasInternetConnection(getActivity())){
            Toast.makeText(getActivity(), R.string.network_connection_error_message, Toast.LENGTH_LONG).show();
            return;
        }

        getSpiceManager().execute(new GetUnitsRequest().getCachedSpiceRequest(), new DefaultSdkResponseListener<GetUnitsRequest.GetUnitsResponse>() {
            @Override
            public void onRequestSuccess(GetUnitsRequest.GetUnitsResponse getUnitsResponse) {
                mUnits = getUnitsResponse.units;
                unitsUpdated();
            }
        });

        getSpiceManager().execute(new GetCategoriesRequest().getCachedSpiceRequest(), new DefaultSdkResponseListener<GetCategoriesRequest.GetCategoriesResponse>() {
            @Override
            public void onRequestSuccess(GetCategoriesRequest.GetCategoriesResponse getCategoriesResponse) {
                allCategories = getCategoriesResponse.categories;
                categoriesUpdated();
            }
        });
        if(!TextUtils.isEmpty(mSearchText)) refreshAutoComplete(mSearchText);
        else refreshAutoComplete("");
    }

    private View.OnClickListener onBtSendClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (selectedVariable == null) {
                final Variable tempVariable = constructVariableFromInput();

                if (tempVariable != null) {
                    //showing confirmation dialog before creating the variable
                    String title = String.format(getString(R.string.tracking_create_var_question),
                            tempVariable.getName(), tempVariable.getCategory(), tempVariable.getDefaultAbbreviatedUnitName());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder
                            .setMessage(Html.fromHtml(title))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    selectedVariable = tempVariable;
                                    sendMeasurements();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null);
                    builder.create().show();
                }
            }
            else{
                sendMeasurements();
            }
        }
    };

    private View.OnClickListener onBtAddMeasurmentsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addMeasurementCard(true, true);
            if(!measurementCards.get(0).spMeasurementUnit.isEnabled()){
                measurementCards.get(measurementCards.size() - 1).spMeasurementUnit.setEnabled(false);
            }
        }
    };

    private View.OnLongClickListener onBtAddMeasurmentLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(v.getContext(), R.string.tracking_measurement_add, Toast.LENGTH_SHORT).show();
            return true;
        }
    };


    /*
        **  Clears the focus of the variable entry edittext when the user clicks anywhere else
        */
    View.OnTouchListener onRootViewFocusChanged = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
//            int action = motionEvent.getAction();
//            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
//                etVariableName.clearFocus();
//            }
            return false;
        }
    };

    /*
    ** Toggles the autocomplete on/off based on whether the user selects the variable name edittext
    */
    View.OnFocusChangeListener onVariableNameFocusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, final boolean hasFocus) {
            setAutoCompleteVisibility(hasFocus);
        }
    };

    /*
    **  Called when the user modified the variable name in the EditText
    **  Requests an autocomplete refresh
    */
    private TextWatcher onVariableNameChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            final String search = editable.toString();
            // Only update if the text didn't change in the past 500ms
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (etVariableName != null && search.equals(etVariableName.getText().toString())) {
                        refreshAutoComplete(search);
                    }
                }
            }, 500);
        }
    };

    private int selectDefaultUnitIndex(Variable variable){

        int variableDefaultUnitIndex = -1;

        if(variable != null) {
            for (int i = 0; i < mUnits.size(); i++) {
                String abbreviatedUnitName = mUnits.get(i).getAbbreviatedName();
                if (abbreviatedUnitName.equals(variable.getDefaultAbbreviatedUnitName())) {
                     variableDefaultUnitIndex = i;
                    return variableDefaultUnitIndex;
                }
            }
        }

        int variableCategoryDefaultUnitIndex = -1;

        for (int i = 0; i< mUnits.size(); i++){
            String abbreviatedUnitName = mUnits.get(i).getAbbreviatedName();
            if (mCategoryDef.defaultAbbreviatedUnitName.equals(abbreviatedUnitName)){
                variableCategoryDefaultUnitIndex = i;
                return variableCategoryDefaultUnitIndex;
            }
        }

        return 0;
    }

    /**
     * Method to call when an item on the list was clicked
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked (this
     *            will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     */
    void onVariableClick(AdapterView<?> parent, View view, int position, long id) {
        if (mUnits == null) {
            Toast.makeText(getActivity(), R.string.tracking_fragment_wait_data_load, Toast.LENGTH_SHORT).show();
            return;
        }
        //when pressed Add Variable
        if (suggestedVariables == null || position >= suggestedVariables.size()) {
            selectedVariable = null;
            // Delay showing the cards for a bit so that the animations all run smoothly
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectedDefaultUnitIndex = selectDefaultUnitIndex(null);
                    showAddVariableCard();

                    etVariableNameNew.setText(etVariableName.getText().toString());
                    if (measurementCards.size() == 0) {
                        addMeasurementCard(false, true);
                    }
                    showButtonsCard();
                    measurementCards.get(0).spMeasurementUnit.setEnabled(true);
                    measurementCards.get(0).hideRemindersButton();
                }
            }, 400);
        }
        //When selected a variable so opens the cards to edit it
        else {
            selectedVariable = suggestedVariables.get(position);
            etVariableName.setText(selectedVariable.getName());

            selectedDefaultUnitIndex = selectDefaultUnitIndex(selectedVariable);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideAddVariableCard();
                    if (measurementCards.size() == 0) {
                        addMeasurementCard(false, true);
                    }
                    else{
                        measurementCards.get(0).init(false, mUnits, selectedDefaultUnitIndex, mCategoryDef,
                                selectedVariable.getDefaultValue(), selectedVariable);
                    }
                    showButtonsCard();
                    //when editing a variable the unit spinner is disabled by default
                    measurementCards.get(0).spMeasurementUnit.setEnabled(false);
                }
            }, 400);
        }

        // Clear the focus so that the autocomplete listview disappears
        etVariableName.clearFocus();
    }


    private void unitsUpdated() {
        Collections.sort(mUnits, new Comparator<Unit>() {
            @Override
            public int compare(Unit lhs, Unit rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        selectedDefaultUnitIndex = 0;
        unitAdapter = new UnitSpinnerAdapter(getActivity(), mUnits);
        spVariableUnits.setAdapter(unitAdapter);
        spVariableUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDefaultUnitIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void categoriesUpdated() {
        VariableCategorySelectSpinnerAdapter adapter = new VariableCategorySelectSpinnerAdapter(this.getActivity(), allCategories);
        spVariableCategory.setAdapter(adapter);
        if (!mCategoryDef.equals(mCategoryFilter[0])){
            int position = adapter.getPosition(mCategoryDef.getFilter());
            if (position != -1){
                spVariableCategory.setSelection(position);
            }
        }
    }

    /*
    **  Toggles the visibility of the autocomplete listview/card
    */
    private void setAutoCompleteVisibility(final boolean visible) {
        AnimatorSet animatorSet;
        if (visible) {
            ObjectAnimator backgroundAnimation = ObjectAnimator.ofFloat(lnCardsContainer, View.ALPHA, 1, 0.25f);
            ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(lvVariableSuggestions, View.TRANSLATION_Y, ConvertUtils.convertDpToPixel(-10, getResources()), 0);
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(lvVariableSuggestions, View.ALPHA, 0, 1);

            lvVariableSuggestions.setVisibility(View.VISIBLE);

            animatorSet = new AnimatorSet();
            animatorSet.setDuration(250);
            animatorSet.play(backgroundAnimation).with(translateAnimation).with(alphaAnimation);

        } else {
            ObjectAnimator backgroundAnimation = ObjectAnimator.ofFloat(lnCardsContainer, View.ALPHA, 0.25f, 1);
            ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(lvVariableSuggestions, View.TRANSLATION_Y, 0, ConvertUtils.convertDpToPixel(-10, getResources()));
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(lvVariableSuggestions, View.ALPHA, 1, 0);

            animatorSet = new AnimatorSet();
            animatorSet.setDuration(250);
            animatorSet.play(backgroundAnimation).with(translateAnimation).with(alphaAnimation);
            if (!isVisible() || getActivity().isFinishing()){
                return;
            }
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (isVisible()) {
                        lnCardsContainer.setAlpha(1.0f);
                        lvVariableSuggestions.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    lvVariableSuggestions.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }

        if (getActivity() != null && !getActivity().isFinishing()){
            animatorSet.start();
        }
    }

    /*
    **  Refreshes the list of suggested variable names
    */
    private int refreshesRunning = 0;

    private void refreshAutoComplete(final String search) {
        Ion.getDefault(this.getActivity()).cancelAll(this.getActivity());
        pbAutoCompleteLoading.setVisibility(View.VISIBLE);
        refreshesRunning++;
        getSpiceManager().execute(new GetSuggestedVariablesRequest(search,mCategoryDef.filter), new DefaultSdkResponseListener<GetSuggestedVariablesRequest.GetSuggestedVariablesResponse>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                super.onRequestFailure(spiceException);
                getPublicVariables(search);
            }

            @Override
            public void onRequestSuccess(GetSuggestedVariablesRequest.GetSuggestedVariablesResponse response) {
                if(response.variables.size() == 0){
                    getPublicVariables(search);
                }
                else {
                    suggestedVariables = response.variables;
                    autoCompleteListAdapter.clear();
                    autoCompleteListAdapter.addAll(response.variables);
                    refreshesRunning--;
                    if (refreshesRunning <= 0) {
                        if (isVisible()) {
                            pbAutoCompleteLoading.setVisibility(View.GONE);
                        }
                    }
                    openSearchVariable();
                }
            }
        });
    }

    private void getPublicVariables(final String search){
        getSpiceManager().execute(
                new GetPublicSuggestedVariablesRequest(search,mCategoryDef.filter),
                new DefaultSdkResponseListener<GetPublicSuggestedVariablesRequest.Response>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                super.onRequestFailure(spiceException);
                refreshesRunning--;
                if (refreshesRunning <= 0) {
                    pbAutoCompleteLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRequestSuccess(GetPublicSuggestedVariablesRequest.Response response) {
                suggestedVariables = response.variables;
                autoCompleteListAdapter.clear();
                autoCompleteListAdapter.addAll(response.variables);
                refreshesRunning--;
                if (refreshesRunning <= 0) {
                    if (isVisible()) {
                        pbAutoCompleteLoading.setVisibility(View.GONE);
                    }
                }
                openSearchVariable();
            }
        });
    }

    /**
     * Opens the variable that was previously setted, using {@link #mSearchText}
     */
    private void openSearchVariable(){
        if(mSearchText == null || mSearchText.isEmpty()) return;
        for(int i = 0; i < suggestedVariables.size(); i++){
            Variable variable = suggestedVariables.get(i);
            if(variable.getName().equals(mSearchText)) onVariableClick(null, null, i, 0);
        }
        mSearchText = null;
    }

    /**
     * Shows the button row at the bottom of the screen
     */
    private void showButtonsCard() {
        if (lnButtons.getVisibility() != View.VISIBLE) {
            ViewUtils.expandView(lnButtons, null);
        }
    }

    /**
     * Shows the button row at the bottom of the screen
     */
    private void hideButtonsCard() {
        if (lnButtons.getVisibility() == View.VISIBLE) {
            ViewUtils.collapseView(lnButtons, null);
        }
    }

    /**
     * Shows the card the user uses to input a new variable
     */
    private void showAddVariableCard() {
        if (lnAddVariableContainer.getVisibility() != View.VISIBLE) {
            ViewUtils.expandView(lnAddVariableContainer, null);
            if (!mCategoryDef.equals(mCategoryFilter[0])){

                if (Variable.COMBINE_SUM.equals(mCategoryDef.combineType)){
                    rgVariableCombinationOperation.check(R.id.rbVariableCombinationOperationSum);
                } else {
                    rgVariableCombinationOperation.check(R.id.rbVariableCombinationOperationAverage);
                }

                int position = -1;
                if (spVariableCategory.getAdapter() != null) {
                    position = ((VariableCategorySelectSpinnerAdapter) spVariableCategory.getAdapter()).getPosition(mCategoryDef.getFilter());
                }

                if (position > -1) {
                    spVariableCategory.setSelection(position);
                }
            }
            spVariableUnits.setSelection(selectedDefaultUnitIndex);
            etVariableNameNew.requestFocus();
        }
    }

    /*
    **  Hides the card the user uses to input a new variable
    */
    private void hideAddVariableCard() {
        if (lnAddVariableContainer.getVisibility() == View.VISIBLE) {
            ViewUtils.collapseView(lnAddVariableContainer, null);
        }
    }

    /**
     * Adds a new measurement card to create a new variable or edit it
     * @param removable if removable
     * @param animate if animate to open it
     * @return the created card, null when data is still loading
     */
    private MeasurementCardHolder addMeasurementCard(boolean removable, boolean animate) {
        if (mUnits == null) {
            Toast.makeText(getActivity(), R.string.tracking_fragment_wait_data_load, Toast.LENGTH_SHORT).show();
            return null;
        }

        final MeasurementCardHolder measurementCardHolder = new MeasurementCardHolder(this.getActivity());
        measurementCardHolder.setOnRemovedListener(new MeasurementCardHolder.OnMeasurementCardRemovedListener() {
            @Override
            public void onMeasurementCardRemoved(MeasurementCardHolder measurementCardHolder) {
                measurementCards.remove(measurementCardHolder);
                lnCardsContainer.removeView(measurementCardHolder.measurementCard);
            }
        });
        measurementCards.add(measurementCardHolder);

        // Set marginTop programatically, it's not picked up properly otherwise.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));
        measurementCardHolder.measurementCard.setLayoutParams(layoutParams);

        lnCardsContainer.addView(measurementCardHolder.measurementCard, lnCardsContainer.getChildCount() - 1);

        Double defaultValue = selectedVariable == null ? null : selectedVariable.getDefaultValue();
        measurementCardHolder.init(removable, mUnits, selectedDefaultUnitIndex, mCategoryDef,
                defaultValue, selectedVariable);

        if (animate) {
            ViewUtils.expandView(measurementCardHolder.measurementCard, null);
        }
        return measurementCardHolder;
    }

    /*
    **  Creates a variable object based on the user's input
    **  @return A valid variable object, or NULL if the input wasn't valid
    */
    private Variable constructVariableFromInput() {
        if (lnAddVariableContainer.getVisibility() != View.VISIBLE) {
            showAddVariableCard();
            return null;
        } else {
            // Make sure the variable name is set
            String name = etVariableNameNew.getText().toString();
            if (name.length() == 0) {
                etVariableNameNew.setTextColor(Color.RED);
                etVariableNameNew.setHintTextColor(Color.RED);
                return null;
            }

            // Read various variable settings
            if(spVariableCategory.getSelectedItem() == null) return null;
            String category = ((VariableCategory) spVariableCategory.getSelectedItem()).getName();
            int selectedCombinationOperationId = rgVariableCombinationOperation.getCheckedRadioButtonId();

            String combinationOperation;
            if (selectedCombinationOperationId == R.id.rbVariableCombinationOperationSum) {
                combinationOperation = Variable.COMBINE_SUM;
            } else if (selectedCombinationOperationId == R.id.rbVariableCombinationOperationAverage) {
                combinationOperation = Variable.COMBINE_MEAN;
            } else {
                combinationOperation = Variable.COMBINE_SUM;
            }

            // Use the unit of the first measurement card as the default unit for this variable
            return new Variable(-1, name, null, category, measurementCards.get(0).selectedUnit.getAbbreviatedName(), combinationOperation);
        }
    }

    /*
    **  Sends all inputted measurement for the selected variable
    */
    private boolean sendMeasurements() {
        final HashMap<String, MeasurementSet> measurementSets = new HashMap<>();

        for (MeasurementCardHolder currentHolder : measurementCards) {
            Unit unit = currentHolder.selectedUnit;
            double value = currentHolder.selectedValue;
            long timestampSeconds = currentHolder.selectedDate.getTimeInMillis() / 1000;

            if (Double.isNaN(value)) {
                currentHolder.etValue.setTextColor(Color.RED);
                currentHolder.etValue.setHintTextColor(Color.RED);
                return false;
            }

            Measurement measurement = new Measurement(timestampSeconds, value);
            String note = currentHolder.etNote.getText().toString();
            if (!TextUtils.isEmpty(note)){
                measurement.setNote(note);
            }

            if (!measurementSets.containsKey(unit.getAbbreviatedName())) {
                MeasurementSet newSet = new MeasurementSet(
                        selectedVariable.getOriginalName(), null, selectedVariable.getCategory(), unit.getAbbreviatedName(),
                        selectedVariable.getCombinationOperation(), mPrefs.getApplicationSource());
                newSet.getMeasurements().add(measurement);
                measurementSets.put(unit.getAbbreviatedName(), newSet);
            } else {
                measurementSets.get(unit.getAbbreviatedName()).getMeasurements().add(measurement);
            }
            //Saving the custom reminder
            if(currentHolder.spReminderTime.getSelectedItemPosition() != 0) {
                CustomRemindersHelper.Reminder reminder = new CustomRemindersHelper.Reminder(
                        Long.toString(selectedVariable.getId()),//id
                        selectedVariable.getName(),//name
                        selectedVariable.getCategory(), //variable category
                        selectedVariable.getCombinationOperation(), //combination operation
                        Double.toString(measurement.getValue()), //value
                        measurementSets.get(unit.getAbbreviatedName()).getUnit(), //unit name
                        getFrequencyFromItemPosition(currentHolder.spReminderTime.getSelectedItemPosition()) //frequency
                );
                CustomRemindersHelper.putReminder(getActivity(), reminder);
                CustomRemindersHelper.setAlarm(getActivity(), reminder.id);
            }
            else{
                CustomRemindersHelper.cancelAlarm(getActivity(), Long.toString(selectedVariable.getId()));
            }
        }

        getSpiceManager().execute(new SendMeasurementsRequest(selectedVariable, new ArrayList<>(measurementSets.values())),
                new DefaultSdkResponseListener<Boolean>() {
                    @Override
                    public void onRequestSuccess(Boolean aBoolean) {
                        clean();
                    }
                });

        return true;
    }

    private CustomRemindersHelper.FrequencyType getFrequencyFromItemPosition(int position){
        String string = getResources().getStringArray(R.array.mood_interval_entries)[position];
        if(string.equals(getString(R.string.interval_once_day)) ||
                string.equals(getString(R.string.interval_daily))){
            return CustomRemindersHelper.FrequencyType.DAILY;
        }
        else if(string.equals(getString(R.string.interval_twice_day)) ||
                string.equals(getString(R.string.interval_twicedaily))){
            return CustomRemindersHelper.FrequencyType.TWICE_A_DAY;
        }
        else if(string.equals(getString(R.string.interval_three_times_day))) {
            return CustomRemindersHelper.FrequencyType.THREE_TIMES_A_DAY;
        }
        else if(string.equals(getString(R.string.interval_hourly))){
            return CustomRemindersHelper.FrequencyType.HOURLY;
        }
        else if(string.equals(getString(R.string.interval_everythreehours))){
            return CustomRemindersHelper.FrequencyType.EVERY_THREE_HOURS;
        }
        else if(string.equals(getString(R.string.interval_every_30_minutes))){
            return CustomRemindersHelper.FrequencyType.EVERY_THIRTY_MINUTES;
        }
        return CustomRemindersHelper.FrequencyType.NEVER;
    }

    /*
    **  Clean all input fields, reset the fragment to its original state as much as possible
    */
    private void clean() {
        etVariableName.setText("");
        hideButtonsCard();
        removeAllMeasurementCards();
        hideAddVariableCard();
    }

    /*
    **  Removes all measurement cards currently visible
    */
    private void removeAllMeasurementCards() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
                         @Override
                         public void run() {
                             for (final MeasurementCardHolder measurementCard : measurementCards) {
                                 lnCardsContainer.removeView(measurementCard.measurementCard);
                             }
                             measurementCards.clear();
                         }
                     }
        );

    }


}