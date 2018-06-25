package com.quantimodo.tools.fragments;


import android.app.Activity;
import android.app.Fragment;
import com.octo.android.robospice.SpiceManager;
import com.quantimodo.tools.QTools;

/**
 * Base class for fragments
 * Creates SpiceManager to use RoboSpice
 */
public abstract class QFragment extends Fragment {

    protected SpiceManager mSpiceManager = new SpiceManager(QTools.getInstance().getServiceClass());

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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected SpiceManager getSpiceManager(){
        return mSpiceManager;
    }
}
