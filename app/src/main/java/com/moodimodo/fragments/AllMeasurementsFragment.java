package com.moodimodo.fragments;

import android.os.Bundle;

public class AllMeasurementsFragment extends WebFragmentWithAuth {

    private static final String MEASUREMENTS_URL = "https://app.quantimo.do/ionic/Modo/www/index.html#/app/history-all?hideMenu=true&accessToken=%s";

    public static AllMeasurementsFragment newInstance(){
        AllMeasurementsFragment fg = new AllMeasurementsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL,MEASUREMENTS_URL);
        fg.setArguments(args);

        return fg;
    }

}
