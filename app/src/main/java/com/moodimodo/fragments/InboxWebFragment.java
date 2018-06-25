package com.moodimodo.fragments;

import android.os.Bundle;

public class InboxWebFragment extends WebFragmentWithAuth{

    private final static String REMINDERS_URL_FORMAT = "https://app.quantimo.do/ionic/Modo/www/index.html#/app/reminders-inbox?accessToken=%s&hideMenu=true";


    public static InboxWebFragment newInstance(){
        InboxWebFragment inboxWebFragment = new InboxWebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL,REMINDERS_URL_FORMAT);
        inboxWebFragment.setArguments(args);
        return inboxWebFragment;
    }

}
