package com.moodimodo.widgets;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.Utils;
import com.moodimodo.events.ShowEndTimeEvent;
import com.moodimodo.events.ShowStartTimeEvent;
import com.moodimodo.graph.MatrixFunctions;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.models.Measurement;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LineGraph {

    private static final String KEY_SHOW_REALDATA = "showDetailedGraph";
    private static final String KEY_SHOW_SMOOTHDATA = "showSmoothGraph";

    private View mRoot;
    private GraphicalView mView;
    private ViewGroup mContainer;
    private View mProgress;

    private XYMultipleSeriesRenderer mSeriesRenderer;
    private XYMultipleSeriesDataset mSeriesDataset;

    private XYSeriesRenderer mRealDataRender;
    private TimeSeries mRealData;
    private XYSeriesRenderer mSmoothDataRender;
    private TimeSeries mSmoothData;
    private TextView tvLineLabelDateLeft;
    private TextView tvLineLabelDateRight;

    private boolean mSmoothEnabled = true;
    private boolean mRealEnabled = true;

    private boolean mShown = false;
    private ImageButton mBtOverTimeOptions;

    private SharedPreferences mPrefs;

    private final int mTransparentColor;
    private final int mRealColor;
    private final int mSmoothColor;


    private final SimpleDateFormat mDateFormatter = new SimpleDateFormat(Global.DEFAULT_DATE_FORMAT);
    private String mLeftDate;
    private String mRightDate;

    private boolean mHidden = false;


    public LineGraph(ViewGroup target){
        Resources res = target.getContext().getResources();

        mRoot = target;

        tvLineLabelDateLeft = (TextView) target.findViewById(R.id.tvLineLabelDateLeft);
        tvLineLabelDateRight = (TextView) target.findViewById(R.id.tvLineLabelDateRight);
        mContainer = (ViewGroup) target.findViewById(R.id.lnLineChart);
        mProgress = target.findViewById(R.id.loader);

        mTransparentColor = res.getColor(android.R.color.transparent);
        mSeriesRenderer = new XYMultipleSeriesRenderer();
        mSeriesRenderer.setMarginsColor(res.getColor(R.color.card_background));
        mSeriesRenderer.setShowAxes(true);
        mSeriesRenderer.setShowLegend(false);
        mSeriesRenderer.setZoomEnabled(false);
        mSeriesRenderer.setPanEnabled(false);
        mSeriesRenderer.setShowCustomTextGrid(false);
        mSeriesRenderer.setYAxisMin(-5);
        mSeriesRenderer.setYAxisMax(105);
        mSeriesRenderer.setAntialiasing(true);
        mSeriesRenderer.setShowLabels(false);
        mSeriesRenderer.setMargins(new int[]{0, 0, 0, Utils.convertDpToPixel(14, res)});
        mSeriesRenderer.setXLabels(0);
        mSeriesRenderer.setYLabels(0);
        mSeriesDataset = new XYMultipleSeriesDataset();

        //Real data graph
        mRealData = new TimeSeries("Outcome Variable");

        mRealColor = res.getColor(R.color.graph_detail);
        mRealDataRender = new XYSeriesRenderer();
        mRealDataRender.setDisplayChartValues(false);
        mRealDataRender.setColor(mRealColor);
        mRealDataRender.setLineWidth(Utils.convertDpToPixel(1, res));

        mSeriesDataset.addSeries(mRealData);
        mSeriesRenderer.addSeriesRenderer(mRealDataRender);

        //Smooth Graph
        mSmoothData = new TimeSeries("Outcome Variable");

        mSmoothColor = res.getColor(R.color.graph_poly);
        mSmoothDataRender = new XYSeriesRenderer();
        mSmoothDataRender.setDisplayChartValues(false);
        mSmoothDataRender.setColor(mSmoothColor);
        mSmoothDataRender.setLineWidth(Utils.convertDpToPixel(1, res));

        mSeriesDataset.addSeries(mSmoothData);
        mSeriesRenderer.addSeriesRenderer(mSmoothDataRender);

        mView = ChartFactory.getTimeChartView(target.getContext(),mSeriesDataset,mSeriesRenderer,"");
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mRoot.getContext());
        mRealEnabled = mPrefs.getBoolean(KEY_SHOW_REALDATA, true);
        mSmoothEnabled = mPrefs.getBoolean(KEY_SHOW_SMOOTHDATA, false);

        mBtOverTimeOptions = (ImageButton) target.findViewById(R.id.btOverTimeOptions);
        mBtOverTimeOptions.setOnClickListener(onOverTimeOptionsButtonClicked);
    }

    private void updateLineChart(List<Measurement> measurements){
        mRealData.clear();
        mSmoothData.clear();

        if (measurements.size() < 2){
            mHidden = true;
            return;
        } else {
            mHidden = false;
        }

        mLeftDate = mDateFormatter.format(measurements.get(0).getTimestamp());
        mRightDate = mDateFormatter.format(measurements.get(measurements.size() - 1).getTimestamp());

        for (Measurement m : measurements) {
            mRealData.add(m.getTimestamp(), Utils.convertToOneToHundred(m.getValue()));
        }

        ArrayList<Map.Entry<Date,Double>> entries = new ArrayList<>();
        for (Measurement m : measurements){
            entries.add(new MatrixFunctions.GraphEntry(m.getTimestamp(),Utils.convertToOneToHundred(m.getValue())));
        }

        ArrayList<Map.Entry<Date,Double>> data;
        if (measurements.size() > 0){
            data = MatrixFunctions.smoothData(entries,measurements.get(0).getTimestamp().getTime() / 1000,measurements.get(measurements.size() - 1).getTimestamp().getTime() / 1000);
        } else {
            data = new ArrayList<>();
        }

        for(Map.Entry<Date,Double> d : data){
            mSmoothData.add(d.getKey(),d.getValue());
        }
    }

    private void updateRenderes(){
        if (mRealEnabled){
            mRealDataRender.setColor(mRealColor);
        } else {
            mRealDataRender.setColor(mTransparentColor);
        }

        if (mSmoothEnabled){
            mSmoothDataRender.setColor(mSmoothColor);
        } else {
            mSmoothDataRender.setColor(mTransparentColor);
        }

        mView.zoomReset();
        mView.repaint();

        tvLineLabelDateLeft.setText(mLeftDate);
        tvLineLabelDateRight.setText(mRightDate);
    }

    View.OnClickListener onOverTimeOptionsButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu menuOverTimeOptions = new PopupMenu(view.getContext(), view);
            menuOverTimeOptions.inflate(R.menu.mood_overtime);
            menuOverTimeOptions.setOnMenuItemClickListener(onOverTimeOptionsMenuClicked);
            Menu overTimeOptions = menuOverTimeOptions.getMenu();
            overTimeOptions.getItem(0).setChecked(mRealEnabled);
            overTimeOptions.getItem(1).setChecked(mSmoothEnabled);
            menuOverTimeOptions.show();
        }
    };

    PopupMenu.OnMenuItemClickListener onOverTimeOptionsMenuClicked = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_mood_detailedgraph:
                    mRealEnabled = !item.isChecked();
                    item.setChecked(mRealEnabled);
                    mPrefs.edit().putBoolean(KEY_SHOW_REALDATA,mRealEnabled).apply();
                    updateRenderes();
                    return true;
                case R.id.action_mood_smoothgraph:
                    mSmoothEnabled = !item.isChecked();
                    item.setChecked(mSmoothEnabled);
                    mPrefs.edit().putBoolean(KEY_SHOW_SMOOTHDATA,mSmoothEnabled).apply();
                    updateRenderes();
                    return true;
                case R.id.action_start_time:
                    QTools.getInstance().postEvent(new ShowStartTimeEvent());
                    return true;
                case R.id.action_end_time:
                    QTools.getInstance().postEvent(new ShowEndTimeEvent());
                    return true;
                case R.id.action_share_overtime:
                    share();
                    return true;
                default:
                    return false;
            }
        }
    };

    private void share() {
        mBtOverTimeOptions.setVisibility(View.GONE);
        Utils.shareGraph(mRoot.getContext(), mRoot, new Runnable() {
            @Override
            public void run() {
                mBtOverTimeOptions.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressWarnings(value = "unchecked")
    public void setData(List<Measurement> measurements) {

        new AsyncTask<List<Measurement>,Void,Void>(){

            @Override
            protected Void doInBackground(List<Measurement>... params) {
                updateLineChart(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                showGraph();
                //setting labels
            }
        }.execute(measurements);
    }

    private void showGraph(){
        if (!mShown && !mHidden) {
            mRoot.setVisibility(View.VISIBLE);
            mContainer.addView(mView);
            mProgress.setVisibility(View.GONE);
            mShown = true;
        } else if (mHidden){
            hideGraph();
        }

        updateRenderes();
    }

    private void hideGraph(){
        if (mShown){
            mContainer.removeView(mView);
        }
        mProgress.setVisibility(View.VISIBLE);
        mShown = false;
        mRoot.setVisibility(View.GONE);
    }



}
