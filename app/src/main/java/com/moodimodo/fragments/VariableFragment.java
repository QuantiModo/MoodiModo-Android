package com.moodimodo.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.moodimodo.BuildConfig;
import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.Utils;
import com.moodimodo.events.MeasurementsUpdatedEvent;
import com.moodimodo.events.UpdateDates;
import com.moodimodo.receivers.MoodResultReceiver;
import com.moodimodo.things.Ratings;
import com.moodimodo.widgets.BarGraph;
import com.moodimodo.widgets.LineGraph;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.fragments.QFragment;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.models.Measurement;
import com.quantimodo.tools.models.MeasurementDao;
import com.quantimodo.tools.models.Variable;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VariableFragment extends QFragment {

    private final static int LIMIT = 500;
    public final static long TIME_BETWEEN_MEASUREMENTS = 60 * 1000; // 60 seconds

    private View mView;

    @InjectView(R.id.rlOverTime)
    ViewGroup mLineGraphContainer;
    private LineGraph mLineGraph;

    @InjectView(R.id.lnBarChart)
     ViewGroup mBarGraphContainer;
    private BarGraph mBarGraph;

    @InjectView(R.id.imAverageMood)
    ImageView imAverageMood;

    @InjectView(R.id.tvAverageMood)
    TextView tvAverageMood;

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
    @InjectView(R.id.vfMoodInput)
    ViewFlipper vfMoodInput;
    @InjectView(R.id.tvMoodTimeRemaining)
    TextView tvMoodTimeRemaining;


    boolean mShowNotes;
    final Object mLock = new Object();
    boolean mDeleteEnabled = false;

    @Inject
    DaoSession mDaoSession;

    private long mLastMoodSubmited;

    @Inject @Named("outcome")
    Variable variable;

    int mAverageValue;
    private Handler mTextViewUpdater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QTools.getInstance().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mood, container, false);
        ButterKnife.inject(this,mView);
        mLineGraph = new LineGraph(mLineGraphContainer);
        mBarGraph = new BarGraph(mBarGraphContainer);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportMood(v.getContext(), (Ratings) v.getTag());
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

        initCard();

        return mView;
    }

    @OnClick(R.id.tvUndoLastMood)
    public void onClick(View v){
        synchronized (mLock){
            if (mDeleteEnabled) {
                mDeleteEnabled = false;
            } else {
                return;
            }
        }
        Measurement m = mDaoSession.getMeasurementDao().queryBuilder().orderDesc(MeasurementDao.Properties.Timestamp).limit(1).unique();
        if (m != null){
            m.delete();
        }
        refreshSettings();
        initCard();
        QTools.getInstance().postEvent(new MeasurementsUpdatedEvent());
    }

    private void initCard(){
        long timeDiff =  (System.currentTimeMillis() - mLastMoodSubmited);

        if (timeDiff < TIME_BETWEEN_MEASUREMENTS) {
            mDeleteEnabled = true;
            final String format = getString(R.string.fragment_variables_remaing_time_format);
            tvMoodTimeRemaining.setText(String.format(format,(TIME_BETWEEN_MEASUREMENTS - timeDiff) / 1000));

            if (mTextViewUpdater == null) {
                mTextViewUpdater = new Handler();
            } else {
                mTextViewUpdater.removeCallbacksAndMessages(null);
            }
            mTextViewUpdater.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        int timeDiff = (int) (System.currentTimeMillis() - mLastMoodSubmited);
                        if (timeDiff < 60000) {
                            tvMoodTimeRemaining.setText(String.format(format,(TIME_BETWEEN_MEASUREMENTS - timeDiff) / 1000));
                            mTextViewUpdater.postDelayed(this, 1000);
                        } else {
                            mDeleteEnabled = false;
                            initCard();
                        }
                    } catch (Exception ignored) {
                        mTextViewUpdater = null;
                    }
                }
            });

            if (vfMoodInput.getDisplayedChild() != 1) {
                vfMoodInput.setInAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_in));
                vfMoodInput.setOutAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_out));
            } else {
                vfMoodInput.setInAnimation(null);
                vfMoodInput.setOutAnimation(null);
            }
            vfMoodInput.setDisplayedChild(1);
        } else {
            vfMoodInput.setDisplayedChild(0);
        }
    }

    protected void refreshSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        mShowNotes = prefs.getBoolean(Global.PREF_NOTES_ENABLED, false);

        Measurement lastMeasurement = mDaoSession.getMeasurementDao().queryBuilder().orderDesc(MeasurementDao.Properties.Timestamp).limit(1).unique();
        if (lastMeasurement != null){
            mLastMoodSubmited = lastMeasurement.getTimestamp().getTime();
        } else {
            mLastMoodSubmited = 0;
        }
    }

    private void reportMood(final Context context, Ratings tag) {
        final Intent intent = Utils.createIntent(context, BuildConfig.OUTCOME_VARIABLE,tag.intValue);

        if (!mShowNotes) {
            sendMeasurement(context,intent);
        } else {
            final View view = View.inflate(getActivity(), R.layout.mood_fragment_note, null);
            new AlertDialog.Builder(getActivity()).setTitle("Measurement note")
                    .setCancelable(false)
                    .setView(view)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intent.putExtra(
                                    MoodResultReceiver.EXTRA_NOTE,
                                    ((EditText) view.findViewById(R.id.etNote)).getText().toString());
                            sendMeasurement(context,intent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendMeasurement(context,intent);
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    private void sendMeasurement(Context context,Intent intent){
        context.sendBroadcast(intent);
        mLastMoodSubmited = System.currentTimeMillis();
        initCard();
    }

    @Override
    public void onResume() {
        super.onResume();
        QTools.getInstance().register(this);
        refreshSettings();
        /*
            This hack causes problems
         */
        //getting the date one month back
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        Global.moodChartEnd = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Global.moodChartStart = calendar.getTime();
        mView.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        QTools.getInstance().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mView = null;
        ButterKnife.reset(this);
    }

    void setupAverage(){
        Resources res = mView.getResources();
        String baseText = res.getString(R.string.mood_average);

        switch (mAverageValue) {
            case 1:
                imAverageMood.setImageResource(R.drawable.ic_mood_depressed);
                baseText = baseText.replace("_MOOD_", res.getString(R.string.mood_depressed).toLowerCase());
                break;
            case 2:
                imAverageMood.setImageResource(R.drawable.ic_mood_sad);
                baseText = baseText.replace("_MOOD_", res.getString(R.string.mood_sad).toLowerCase());
                break;
            case 3:
                imAverageMood.setImageResource(R.drawable.ic_mood_ok);
                baseText = baseText.replace("_MOOD_", res.getString(R.string.mood_ok).toLowerCase());
                break;
            case 4:
                imAverageMood.setImageResource(R.drawable.ic_mood_happy);
                baseText = baseText.replace("_MOOD_", res.getString(R.string.mood_happy).toLowerCase());
                break;
            case 5:
                imAverageMood.setImageResource(R.drawable.ic_mood_ecstatic);
                baseText = baseText.replace("_MOOD_", res.getString(R.string.mood_ecstatic).toLowerCase());
                break;

            default:
                imAverageMood.setVisibility(View.GONE);
                tvAverageMood.setVisibility(View.GONE);
                break;
        }

        Spanned spanned = Html.fromHtml(baseText);
        tvAverageMood.setText(spanned);
    }

    void initData(){
        MeasurementDao measurementDao =  mDaoSession.getMeasurementDao();

        List<Measurement> measurements = measurementDao.queryBuilder()
                .orderAsc(MeasurementDao.Properties.Timestamp)
                .where(
                        MeasurementDao.Properties.VariableId.eq(variable.getId()),
                        MeasurementDao.Properties.Timestamp.between(Global.moodChartStart, Global.moodChartEnd)
                ).list();

        double sum = 0;
        for (Measurement m : measurements){
            sum += m.getValue();
        }

        mAverageValue = (int) Math.round(sum / measurements.size());

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupAverage();
            }
        });

        mLineGraph.setData(measurements);
        mBarGraph.updateBarChart(measurements);
    }

    public void onEventBackgroundThread(UpdateDates event){
        initData();
    }

    public void onEventBackgroundThread(MeasurementsUpdatedEvent e){
        Global.init(getActivity(), mDaoSession);
        initData();
    }

    public static Fragment newInstance() {
        VariableFragment variableFragment = new VariableFragment();
        return variableFragment;
    }
}
