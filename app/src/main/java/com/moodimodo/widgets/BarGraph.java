package com.moodimodo.widgets;

import android.content.res.Resources;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import com.moodimodo.R;
import com.moodimodo.Utils;
import com.quantimodo.tools.models.Measurement;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.List;

public class BarGraph {

    private final ViewGroup mRoot;
    private final GraphicalView mGraph;
    private final XYMultipleSeriesRenderer mBarMultipleSeriesRenderer;
    private final XYMultipleSeriesDataset mBarMultipleSeriesDataSet;
    private final XYSeries[] mBarSeries;
    private final ImageButton mBtDistributionOptions;

    private View mLoader;
    private boolean mShown = false;

    public BarGraph(ViewGroup root) {
        mRoot = root;

        Resources res = root.getResources();
        int[] chartColours = res.getIntArray(R.array.chart_colours);
        int margin = Utils.convertDpToPixel(24, res);

        mBarMultipleSeriesRenderer = new XYMultipleSeriesRenderer();
        // Config looks
        mBarMultipleSeriesRenderer.setLabelsTextSize(Utils.convertSpToPixels(11, res));
        mBarMultipleSeriesRenderer.setBackgroundColor(res.getColor(R.color.card_background));
        mBarMultipleSeriesRenderer.setMarginsColor(res.getColor(R.color.card_background));
        mBarMultipleSeriesRenderer.setMargins(new int[]{0, margin, 0, margin});               // Set margins so the labels don't render outside the screen
        mBarMultipleSeriesRenderer.setYLabelsPadding(Utils.convertDpToPixel(8, res));   // label padding so they render next to the Y axis
        mBarMultipleSeriesRenderer.setBarWidth(Utils.convertDpToPixel(40, res));
        mBarMultipleSeriesRenderer.setXLabels(0);
        // Set chart range
        mBarMultipleSeriesRenderer.setYAxisMin(0);
        mBarMultipleSeriesRenderer.setXAxisMin(-0.4);
        mBarMultipleSeriesRenderer.setXAxisMax(4.4);
        // Enable/disable features
        mBarMultipleSeriesRenderer.setShowLegend(false);
        mBarMultipleSeriesRenderer.setShowAxes(true);                                   // Axis to fill the card a bit
        mBarMultipleSeriesRenderer.setZoomEnabled(false);                               // Zoom and pan disabled so it doesn't interfere with the scrollview
        mBarMultipleSeriesRenderer.setPanEnabled(false);
        mBarMultipleSeriesRenderer.setClickEnabled(false);                              // We don't want this to be clickable

        for (int color : chartColours) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            mBarMultipleSeriesRenderer.addSeriesRenderer(r);
        }


        mBarMultipleSeriesDataSet = new XYMultipleSeriesDataset();
        mBarSeries = new XYSeries[5];
        String[] titles = res.getStringArray(R.array.moods);

        for (int i = 0; i < mBarSeries.length; i++) {
            mBarSeries[i] = new XYSeries(titles[i]);
            mBarSeries[i].setTitle(titles[i]);
            mBarMultipleSeriesDataSet.addSeries(i, mBarSeries[i]);
            mBarMultipleSeriesRenderer.addXTextLabel(i, titles[i]);
        }

        mGraph = ChartFactory.getBarChartView(mRoot.getContext(), mBarMultipleSeriesDataSet, mBarMultipleSeriesRenderer, BarChart.Type.STACKED);
        mGraph.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.convertDpToPixel(230, res)));

        mBtDistributionOptions = (ImageButton) mRoot.findViewById(R.id.btDistributionOptions);
        mBtDistributionOptions.setOnClickListener(onDistributionOptionsButtonClicked);

        mLoader = mRoot.findViewById(R.id.loaderBar);

        LinearLayout lnBarChart = (LinearLayout) mRoot.findViewById(R.id.lnBarChart);
        lnBarChart.setVisibility(View.VISIBLE);
        lnBarChart.addView(mGraph);
    }

    View.OnClickListener onDistributionOptionsButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu menuDistributionOptions = new PopupMenu(view.getContext(), view);
            menuDistributionOptions.inflate(R.menu.mood_distribution);
            menuDistributionOptions.setOnMenuItemClickListener(onDistributionOptionsMenuClicked);
            menuDistributionOptions.show();
        }
    };

    PopupMenu.OnMenuItemClickListener onDistributionOptionsMenuClicked = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_share_distribution:
                    share();
                    return true;
                default:
                    return false;
            }
        }
    };

    private void share() {
        mBtDistributionOptions.setVisibility(View.GONE);
        Utils.shareGraph(mRoot.getContext(), mRoot, new Runnable() {
            @Override
            public void run() {
                mBtDistributionOptions.setVisibility(View.VISIBLE);
            }
        });
    }


    public void updateBarChart(List<Measurement> measurement) {
        double[] numReportsPerMood = new double[5];

        for (Measurement currentEntry : measurement) {
            int value =(int) Math.round(currentEntry.getValue());
            numReportsPerMood[value - 1]++;
        }

        for (int i = 0; i < numReportsPerMood.length; i++) {
            mBarSeries[i].clear();
            mBarSeries[i].add(i, numReportsPerMood[i]);
        }

        mLoader.setVisibility(View.GONE);
        if (measurement.size() > 0) {
            mRoot.setVisibility(View.VISIBLE);
        } else {
            mRoot.setVisibility(View.GONE);
        }
        mGraph.repaint();
    }
}
