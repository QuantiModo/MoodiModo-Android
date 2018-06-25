package com.moodimodo.events;

import android.app.Fragment;

/**
 * Event to inform the MainActivity that WelcomeFragment has finished and VariableFragment opened
 * so MainActivity should make updates on the view
 */
public class WelcomeFinishedEvent {
    public final Fragment fragment;

    public WelcomeFinishedEvent(Fragment fragment){
        this.fragment = fragment;
    }
}
