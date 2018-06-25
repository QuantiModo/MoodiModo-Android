package com.quantimodo.tools.views;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.R;
import com.quantimodo.tools.adapters.AutoCompleteListAdapter;
import com.quantimodo.tools.sdk.DefaultSdkResponseListener;
import com.quantimodo.tools.sdk.request.GetSuggestedVariablesRequest;
import com.quantimodo.tools.utils.QtoolsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * View that encapsulates the Variables search logic, provides an EditText to input the search
 * and shows a list view with the results
 */
public class VariableSearchView extends RelativeLayout {
    private SpiceManager mSpiceManager;
    private Context mContext;
    private String mFilter = null;
    private AutoCompleteListAdapter autoCompleteListAdapter;
    private ArrayList<Variable> suggestedVariables = new ArrayList<>();

    private EditText mInputEditText;
    private ProgressBar mLoadingProgressBar;
    private ListView mResultsListView;

    public VariableSearchView(Context context) {
        super(context);
        init();
    }

    public VariableSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VariableSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mContext = getContext();
        inflate(getContext(), R.layout.variable_search, this);
        mInputEditText = (EditText) findViewById(R.id.variable_search_edit_text);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.variable_search_progress);
        mResultsListView = (ListView) findViewById(R.id.variable_search_result_list);

        // Add a textwatcher so that we can update our custom autocomplete when the user types
        mInputEditText.addTextChangedListener(onVariableNameChanged);
        autoCompleteListAdapter = new AutoCompleteListAdapter(mContext, suggestedVariables);
        mResultsListView.setAdapter(autoCompleteListAdapter);
//        refreshAutoComplete("");
    }

    /**
     * Sets the spice manager to make the remote request. The parent Activity is in charge of
     * the start and stop the spice manager
     * @param manager the manager to use
     */
    public void setSpiceManager(SpiceManager manager){
        this.mSpiceManager = manager;
    }

    /**
     * Sets the listener when the user clicks over an item on the list
     */
    public void setOnItemClick(AdapterView.OnItemClickListener listener){
        mResultsListView.setOnItemClickListener(listener);
    }

    public List<Variable> getData(){
        return this.suggestedVariables;
    }

    public void setFilter(String filter){
        this.mFilter = filter;
        refreshAutoComplete("");
    }

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
                    if (search.equals(mInputEditText.getText().toString())) {
                        refreshAutoComplete(search);
                    }
                }
            }, 500);
        }
    };
    /*
    **  Refreshes the list of suggested variable names
    */
    private int refreshesRunning = 0;

    private void refreshAutoComplete(final String search) {
        if(!QtoolsUtils.hasInternetConnection(mContext)){
            Toast.makeText(mContext.getApplicationContext(),
                    R.string.network_connection_error_message, Toast.LENGTH_LONG).show();
            return;
        }
        Ion.getDefault(mContext).cancelAll(mContext);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        refreshesRunning++;
        getSpiceManager().execute(new GetSuggestedVariablesRequest(search,mFilter), new DefaultSdkResponseListener<GetSuggestedVariablesRequest.GetSuggestedVariablesResponse>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                super.onRequestFailure(spiceException);
                spiceException.printStackTrace();
            }

            @Override
            public void onRequestSuccess(GetSuggestedVariablesRequest.GetSuggestedVariablesResponse response) {
                if(response.variables.size() == 0){
                    mLoadingProgressBar.setVisibility(View.GONE);
                    //show no results message
                }
                else {
                    suggestedVariables = response.variables;
                    autoCompleteListAdapter.clear();
                    autoCompleteListAdapter.addAll(response.variables);
                    refreshesRunning--;
                    if (refreshesRunning <= 0) {
                        mLoadingProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void setSearchBoxHint(String hint){
        mInputEditText.setHint(hint);
    }

    private SpiceManager getSpiceManager(){
        return mSpiceManager;
    }
}
