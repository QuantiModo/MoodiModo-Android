package com.quantimodo.tools.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.octo.android.robospice.SpiceManager;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.R;
import com.quantimodo.tools.utils.CustomRemindersHelper;
import com.quantimodo.tools.views.VariableSearchView;

/**
 * Activity that list the variables result, given a filter category
 */
public class CustomReminderVarsList extends Activity implements AdapterView.OnItemClickListener{
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";

    private SpiceManager mSpiceManager = new SpiceManager(QTools.getInstance().getServiceClass());
    private String mCategoryName = null;
    VariableSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_reminder_var_list);
        mCategoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);

        searchView = (VariableSearchView) findViewById(R.id.custom_reminder_list);
        searchView.setOnItemClick(this);
        searchView.setSearchBoxHint("");
        searchView.setSpiceManager(mSpiceManager);
        searchView.setFilter(mCategoryName);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSpiceManager.start(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSpiceManager.shouldStop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Variable variable = searchView.getData().get(position);
        CustomRemindersHelper.Reminder reminder = CustomRemindersHelper.getReminder(this,
                Long.toString(variable.getId()));
        Intent intent = new Intent(this, CustomRemindersCreateActivity.class);
        if(reminder != null)
            intent.putExtra(CustomRemindersCreateActivity.EXTRA_REMINDER_ID, reminder.id);
        else {
            intent.putExtra(CustomRemindersCreateActivity.EXTRA_FLAG_CREATING, true);
            intent.putExtra(CustomRemindersCreateActivity.EXTRA_VARIABLE, variable);
        }
        startActivity(intent);
    }
}
