package com.quantimodo.tools.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.quantimodo.android.sdk.model.Unit;
import com.quantimodo.tools.R;
import com.quantimodo.tools.utils.CustomRemindersHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Activity that display the list of created reminders and a button to add a new one
 */
public class CustomRemindersActivity extends ListActivity {
    List<CustomRemindersHelper.Reminder> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_reminder_main);
        if (getActionBar() != null)
            getActionBar().setTitle(R.string.custom_reminders_title);
        Button addButton = (Button) findViewById(R.id.custom_reminder_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomRemindersActivity.this, CustomRemindersCreateActivity.class);
                intent.putExtra(CustomRemindersCreateActivity.EXTRA_FLAG_CREATING, true);
                startActivity(intent);
            }
        });
        setProgressBarIndeterminate(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ListLoaderTask().execute();
    }

    private class ListLoaderTask extends AsyncTask<Void, Void, SimpleAdapter> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected SimpleAdapter doInBackground(Void... params) {
            // create the mapping list
            String[] from = new String[]{"title", "subtitle"};
            int[] to = new int[]{R.id.custom_reminder_title_text, R.id.custom_reminder_freq_text};

            // prepare the list of all records
            List<HashMap<String, String>> fillMaps = new ArrayList<>();
            reminderList = CustomRemindersHelper.getRemindersList(CustomRemindersActivity.this);

            //sorting by reminder name
            Collections.sort(reminderList, new Comparator<CustomRemindersHelper.Reminder>() {
                @Override
                public int compare(CustomRemindersHelper.Reminder reminder1,
                                   CustomRemindersHelper.Reminder reminder2) {
                    return reminder1.name.compareToIgnoreCase(reminder2.name);
                }
            });

            for (CustomRemindersHelper.Reminder reminder : reminderList) {
                HashMap<String, String> map = new HashMap<>();
                map.put(from[0], reminder.name);
                map.put(from[1], CustomRemindersHelper.removeTrailingZeros(reminder.value) + " " +
                        reminder.unitName + " " +
                        reminder.getFrequencyString());
                fillMaps.add(map);
            }

            // fill in the layout with the map
            return new SimpleAdapter(CustomRemindersActivity.this,
                    fillMaps, R.layout.custom_reminder_item, from, to);
        }

        @Override
        protected void onPostExecute(SimpleAdapter adapter) {
            super.onPostExecute(adapter);
            setListAdapter(adapter);
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CustomRemindersHelper.Reminder reminder = reminderList.get(position);
                    Intent intent = new Intent(CustomRemindersActivity.this, CustomRemindersCreateActivity.class);
                    intent.putExtra(CustomRemindersCreateActivity.EXTRA_REMINDER_ID, reminder.id);
                    startActivity(intent);
                }
            });
            getListView().setDividerHeight(0);
            getListView().setDivider(null);
            ScaleAnimation anim = new ScaleAnimation(1, 1, 0, 1);
            anim.setDuration(350);
            getListView().setAnimation(anim);
            getListView().animate();

            setProgressBarIndeterminateVisibility(false);
        }
    }
}
