package com.quantimodo.tools.testhelpers;

import android.app.Activity;
import android.os.Bundle;
import com.quantimodo.tools.adapters.CorrelationAdapter;
import com.quantimodo.tools.fragments.FactorsFragment;
import com.quantimodo.tools.fragments.TrackingFragment;
import com.quantimodo.tools.test.R;


public class TestMainActivity extends Activity {

    private FactorsFragment factorsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_test_activity);

        TrackingFragment trackingFragment = new TrackingFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.container, trackingFragment)
                .commit();
    }

    public FactorsFragment getFactorsFragment(){
        return factorsFragment;
    }

    public void openFactors(@CorrelationAdapter.CorrelationType final int type, final String variableName){
        factorsFragment = null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                factorsFragment = FactorsFragment.newInstance(type, variableName);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, factorsFragment)
                        .commit();
            }
        });
    }
}
