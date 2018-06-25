package com.moodimodo.fragments;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.moodimodo.BuildConfig;
import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.Utils;
import com.moodimodo.adapters.IntervalSpinnerAdapter;
import com.moodimodo.events.WelcomeFinishedEvent;
import com.moodimodo.receivers.MoodResultReceiver;
import com.moodimodo.receivers.MoodTimeReceiver;
import com.moodimodo.things.Ratings;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.activities.QuantimodoLoginActivity;
import com.quantimodo.tools.sdk.AuthHelper;

import javax.inject.Inject;

import static com.moodimodo.activities.SettingsActivity.ADD_NOTE_AFTER_MOOD_KEY;
import static com.moodimodo.activities.SettingsActivity.MOOD_NOTIFICATION_ENABLED_KEY;
import static com.moodimodo.activities.SettingsActivity.MOOD_POPUP_ENABLED_KEY;

public class WelcomeFragment extends Fragment {

    private ScrollView svWelcome;

    private static Ratings reportedMood;
    private static boolean intervalCardVisible;
    private static boolean syncSkipped = false;

    private static final String PREF_INTERVAL = "moodInterval";

    @Inject
    AuthHelper authHelper;

    @InjectView(R.id.tvWelcome)
    TextView tvWelcome;
    @InjectView(R.id.btDepressed)
    ImageButton btDepressed;
    @InjectView(R.id.btSad)
    ImageButton btSad;
    @InjectView(R.id.btOk)
    ImageButton btOk;
    @InjectView(R.id.btHappy)
    ImageButton btHappy;
    @InjectView(R.id.btEcstatic)
    ImageButton btEcstatic;
    @InjectView(R.id.imReportedMood)
    ImageView imReportedMood;
    @InjectView(R.id.vfMoodInput)
    ViewFlipper vfMoodInput;
    @InjectView(R.id.cbNotificationEnabled)
    CheckBox cbNotificationEnabled;
    @InjectView(R.id.cbPopupEnabled)
    CheckBox cbPopupEnabled;
    @InjectView(R.id.btReportIntervalDone)
    Button btReportIntervalDone;
    @InjectView(R.id.tvSyncTitle)
    TextView tvSyncTitle;
    @InjectView(R.id.btSyncEnable)
    Button btSyncEnable;
    @InjectView(R.id.rlSync)
    RelativeLayout rlSync;
    @InjectView(R.id.spInterval)
    Spinner spInterval;
    @InjectView(R.id.btSyncSkip)
    Button btSyncSkip;
    @InjectView(R.id.cbNoteEnabled)
    CheckBox cbNoteEnabled;


    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        QTools.getInstance().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (container == null) {
            return null;
        }

        svWelcome = (ScrollView) inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.inject(this,svWelcome);

        Spanned spanned = Html.fromHtml(getString(R.string.welcome_moodimodo));
        tvWelcome.setText(spanned);

        initReportMoodCard();
        initSyncCard(false);

        if (intervalCardVisible) {
            initReportIntervalCard(false);
        }

        ButterKnife.inject(this, svWelcome);
        return svWelcome;
    }

    @Override
    public void onResume() {
        super.onResume();
        tryFinish();
        initSyncCard(false);
    }

    private void tryFinish() {
        boolean canBeFinished = authHelper.isLoggedIn() || syncSkipped;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        canBeFinished &= !prefs.getString(PREF_INTERVAL, "").equals("");
        if (canBeFinished) {
            finishWelcome();
        }
    }


    private void initReportMoodCard() {
        if (reportedMood != null) {
            updateImReported();
        } else {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportMood(v.getContext(), (Ratings) v.getTag());
                    new Handler().postDelayed(initReportIntervalCardRunnable, 500);
                    v.setOnClickListener(null);
                }
            };

            btDepressed.setTag(Ratings.RATING_1);
            btDepressed.setOnClickListener(onClickListener);

            btSad.setTag(Ratings.RATING_2);
            btSad.setOnClickListener(onClickListener);

            btOk.setTag(Ratings.RATING_3);
            btOk.setOnClickListener(onClickListener);

            btHappy.setTag(Ratings.RATING_4);
            btHappy.setOnClickListener(onClickListener);

            btEcstatic.setTag(Ratings.RATING_5);
            btEcstatic.setOnClickListener(onClickListener);
        }
    }

    private Runnable initReportIntervalCardRunnable = new Runnable() {
        @Override
        public void run() {
            initReportIntervalCard(true);
        }
    };

    private void initReportIntervalCard(boolean animate) {
        final View vwReportInterval = svWelcome.findViewById(R.id.rlCardReportInterval);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        spInterval.setAdapter(new IntervalSpinnerAdapter(this, this.getActivity()));
        spInterval.setSelection(Global.moodInterval);
        spInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Global.moodInterval = position;
                prefs.edit().putString(PREF_INTERVAL, String.valueOf(Global.moodInterval)).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btReportIntervalDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSyncCard(true);
                Global.moodInterval = spInterval.getSelectedItemPosition();
                prefs.edit().putString(PREF_INTERVAL, String.valueOf(Global.moodInterval)).apply();
                MoodTimeReceiver.setAlarm(view.getContext(), Global.moodInterval);
                tryFinish();
                svWelcome.fullScroll(View.FOCUS_DOWN);
            }
        });

        cbNotificationEnabled.setChecked(prefs.getBoolean(MOOD_NOTIFICATION_ENABLED_KEY, true));
        cbPopupEnabled.setChecked(prefs.getBoolean(MOOD_POPUP_ENABLED_KEY, true));
        cbNotificationEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(MOOD_NOTIFICATION_ENABLED_KEY, b).apply();
            }
        });
        cbPopupEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(MOOD_POPUP_ENABLED_KEY, b).apply();
            }
        });

        cbNoteEnabled.setChecked(prefs.getBoolean(ADD_NOTE_AFTER_MOOD_KEY, false));
        cbNoteEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(ADD_NOTE_AFTER_MOOD_KEY,isChecked).apply();
            }
        });

        vwReportInterval.setVisibility(View.VISIBLE);
        if (animate) {
            Animation anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.card_slide);
            vwReportInterval.startAnimation(anim);
            scrollToBottom();
        }

        intervalCardVisible = true;
    }

    private void initSyncCard(boolean animate) {
        Spanned spanned = Html.fromHtml(getString(R.string.welcome_sync_title));
        tvSyncTitle.setText(spanned);

        btSyncEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(getActivity(), QuantimodoLoginActivity.class);
                intent.putExtra(QuantimodoLoginActivity.KEY_APP_NAME, getString(R.string.app_name));
                startActivity(intent);
            }
        });

        btSyncSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncSkipped = true;
                rlSync.setVisibility(View.GONE);
                tryFinish();
            }
        });

        if (animate) {
            Animation anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.card_slide);
            rlSync.startAnimation(anim);

            scrollToBottom();
        }

        if (authHelper.isLoggedIn()) {
            rlSync.setVisibility(View.GONE);
        }
    }


    private void reportMood(Context context, Ratings rating) {
        Intent intent = Utils.createIntent(context, BuildConfig.OUTCOME_VARIABLE,rating.intValue);
        context.sendBroadcast(intent);

        reportedMood = rating;
        updateImReported();

        vfMoodInput.setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
        vfMoodInput.setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
    }

    private void updateImReported() {
        switch (reportedMood) {
            case RATING_1:
                imReportedMood.setImageResource(R.drawable.ic_mood_depressed);
                break;
            case RATING_2:
                imReportedMood.setImageResource(R.drawable.ic_mood_sad);
                break;
            case RATING_3:
                imReportedMood.setImageResource(R.drawable.ic_mood_ok);
                break;
            case RATING_4:
                imReportedMood.setImageResource(R.drawable.ic_mood_happy);
                break;
            case RATING_5:
                imReportedMood.setImageResource(R.drawable.ic_mood_ecstatic);
                break;
        }
        vfMoodInput.setDisplayedChild(1);
    }

    private void scrollToBottom() {
        ObjectAnimator animator = ObjectAnimator.ofInt(svWelcome, "scrollY", svWelcome.getBottom());
        animator.setDuration(1000);
        animator.start();
    }

    private void finishWelcome() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        prefs.edit().putBoolean("welcomeCompleted", true).apply();

        FragmentManager fm = getActivity().getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = VariableFragment.newInstance();
        ft.replace(R.id.mainFragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
        QTools.getInstance().postEvent(new WelcomeFinishedEvent(fragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}