package com.quantimodo.tools.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import com.octo.android.robospice.SpiceManager;
import com.quantimodo.tools.QTools;

/**
 * Base class for list fragments
 * Creates SpiceManager to use RoboSpice
 */
public abstract class QListFragment extends ListFragment {

    private SpiceManager mSpiceManager = new SpiceManager(QTools.getInstance().getServiceClass());

    public SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSpiceManager.start(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        mSpiceManager.shouldStop();
    }
}
