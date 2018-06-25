package com.moodimodo.activities;

import android.app.Activity;
import android.os.Bundle;

import com.moodimodo.fragments.RemindersFragment;

/**
 * Activity to store some reminders to track measurements
 */
public class RemindersActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new RemindersFragment())
                .commit();
    }

}
