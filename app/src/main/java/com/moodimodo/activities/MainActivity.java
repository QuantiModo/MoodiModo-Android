package com.moodimodo.activities;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.moodimodo.BuildConfig;
import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.Utils;
import com.moodimodo.events.ShowEndTimeEvent;
import com.moodimodo.events.ShowStartTimeEvent;
import com.moodimodo.events.UpdateDates;
import com.moodimodo.events.WelcomeFinishedEvent;
import com.moodimodo.fragments.AllMeasurementsFragment;
import com.moodimodo.fragments.InboxWebFragment;
import com.moodimodo.fragments.VariableFragment;
import com.moodimodo.fragments.WelcomeFragment;
import com.moodimodo.sync.MigrationService;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.activities.CustomReminderVarsList;
import com.quantimodo.tools.activities.CustomRemindersActivity;
import com.quantimodo.tools.activities.QuantimodoLoginActivity;
import com.quantimodo.tools.activities.TourActivity;
import com.quantimodo.tools.adapters.CorrelationAdapter;
import com.quantimodo.tools.adapters.DrawerAdapter;
import com.quantimodo.tools.events.NoAuthEvent;
import com.quantimodo.tools.events.SyncFinished;
import com.quantimodo.tools.fragments.DrawerFragment;
import com.quantimodo.tools.fragments.FactorsFragment;
import com.quantimodo.tools.fragments.ImportWebFragment;
import com.quantimodo.tools.fragments.QuantimodoWebFragment;
import com.quantimodo.tools.fragments.TrackingFragment;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.receivers.CustomRemindersReceiver;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;
import com.quantimodo.tools.utils.CustomRemindersHelper;
import com.uservoice.uservoicesdk.UserVoice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import javax.inject.Inject;


public class MainActivity extends MActivity implements DrawerFragment.MenuListener{
    public static final String EXTRA_TRACK_MOOD = "extra_track_mood" ;
    public static final String EXTRA_TRACK_DIET = "extra_track_diet" ;
    public static final String EXTRA_TRACK_TREATMENT = "extra_track_treatment" ;
    public static final String EXTRA_TRACK_SYMPTOM = "extra_track_symptom" ;
    public static final String EXTRA_TRACK_CUSTOM = "extra_track_custom" ;
    public static final String EXTRA_TRACK_PHYSICAL = "extra_track_physical";

    private Button btTimeRangeStart;
    private Button btTimeRangeEnd;

    private Calendar startCalendar;
    private Calendar endCalendar;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    private Fragment mCurrentFragment;


    @Inject
    AuthHelper mAuthHelper;

    @Inject
    DaoSession mDaoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        final SharedPreferences sharedPref = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("entered_first_time", true)){
            Intent intent = new Intent(this, TourActivity.class);
            startActivityForResult(intent, 1);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("entered_first_time", false);
            //when showing the tour don't show the informative popup
            editor.putBoolean("show_info_popup", false);
            editor.apply();
        }
        else{
            //if app updated, shows informative popup
            /*if(sharedPref.getBoolean("show_info_popup", true)){
                new AlertDialog.Builder(this)
                        .setMessage(R.string.popup_info_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setCancelable(false)
                        .create().show();
                sharedPref.edit().putBoolean("show_info_popup", false).apply();
            }*/
        }
        setContentView(R.layout.activity_main);
        QTools.getInstance().inject(this);

        Global.init(this, mDaoSession);

        if (!Global.welcomeCompleted || mDaoSession.getMeasurementDao().count() == 0) {
            mCurrentFragment = WelcomeFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.mainFragment, mCurrentFragment).commit();
        } else {
            mCurrentFragment = VariableFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.mainFragment, mCurrentFragment).commit();
        }

//        List<CustomRemindersHelper.Reminder> reminders = CustomRemindersHelper.getRemindersList(this);
//
//        CustomRemindersHelper.Reminder rem = reminders.get(0);
//
//        if (rem != null) {
//            Intent intent = new Intent(this, CustomRemindersReceiver.class);
//            intent.putExtra(CustomRemindersHelper.EXTRA_SPECIFIC_ID, rem.remoteId);
//            intent.putExtra(CustomRemindersHelper.EXTRA_REMINDER_ID, rem.id);
//            intent.putExtra(CustomRemindersReceiver.EXTRA_REQUEST_ALARM, true);
//            sendBroadcast(intent);
//        }

        initDrawerV2();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            loadFragment(bundle);
        }

        Intent migration = MigrationService.needUpdate(this);
        if (migration != null){
            startService(migration);
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            loadFragment(bundle);
        }
    }

    /**
     * Loads a certain fragment using intent extras, see:
     * <ul>
     *     <li>MainActivity.EXTRA_TRACK_MOOD</li>
     *     <li>MainActivity.EXTRA_TRACK_DIET</li>
     *     <li>MainActivity.EXTRA_TRACK_TREATMENT</li>
     *     <li>MainActivity.EXTRA_TRACK_SYMPTOM</li>
     * </ul>
     * @param extras
     */
    private void loadFragment(@NonNull Bundle extras){
        if (extras.getBoolean(EXTRA_TRACK_MOOD, false)) {
            clickTracking();
        } else if (extras.getBoolean(EXTRA_TRACK_DIET, false)) {
            clickTrackingFactors(TrackingFragment.TYPE_DIET);
        } else if (extras.getBoolean(EXTRA_TRACK_TREATMENT, false)) {
            clickTrackingFactors(TrackingFragment.TYPE_TREATMENTS);
        } else if (extras.getBoolean(EXTRA_TRACK_SYMPTOM, false)) {
            clickTrackingFactors(TrackingFragment.TYPE_SYMPTOMS);
        } else if (extras.getBoolean(EXTRA_TRACK_PHYSICAL, false)) {
            clickTrackingFactors(TrackingFragment.TYPE_PHYSICAL);
        } else if (!extras.getString(CustomRemindersHelper.EXTRA_VARIABLE_NAME, "").isEmpty()) {
            String reminderName = extras.getString(CustomRemindersHelper.EXTRA_VARIABLE_NAME, "");
            clickTrackingFactors(TrackingFragment.TYPE_ALL, reminderName);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
    private static final int TRACK_ITEM = 1;
    private static final int TRACK_MOOD_ITEM = 2;
    private static final int TRACK_DIET_ITEM = 3;
    private static final int TRACK_EMOTIONS_ITEM = 20;
    private static final int TRACK_TREATMENTS_ITEM = 4;
    private static final int TRACK_SYMPTOMS_ITEM = 5;
    private static final int TRACK_PHYSICAL_ITEM = 29;
    private static final int TRACK_MISC_ITEM = 6;

    private static final int IMPORT_ITEM = 7;
    private static final int POSITIVE_FACTORS_ITEM = 8;
    private static final int NEGATIVE_FACTORS_ITEM = 9;
    private static final int SYNC_DATA_ITEM = 10;
    private static final int SETTINGS_ITEM = 11;
    private static final int ITEM_HELP_ITEM = 12;

    private static final int APPS_AND_DEVICES_ITEM = 13;
    private static final int TRACK_EXERCISE_ITEM = 14;
    private static final int TRACK_SLEEP_ITEM = 15;
    private static final int TRACK_VITALS_ITEM = 16;
    private static final int TRACK_CORRELATE_ITEM = 17;

    private static final int HISTORY_ITEM = 18;
    private static final int YOUR_PREDICTORS_ITEM = 19;

    private static final int REMINDERS_ITEM = 21;
    private static final int REMINDERS_MANAGE_ITEM = 22;
    private static final int REMINDERS_SETTINGS_ITEM = 23;
    private static final int REMINDERS_EMOTIONS_ITEM = 24;
    private static final int REMINDERS_DIET_ITEM = 25;
    private static final int REMINDERS_TREATMENTS_ITEM = 26;
    private static final int REMINDERS_SYMPTOMS_ITEM = 27;
    private static final int REMINDERS_PHYSICAL_ITEM = 30;
    private static final int REMINDERS_MISC_ITEM = 28;
    private static final int REMINDERS_SLEEP_ITEM = 40;
    private static final int REMINDERS_INBOX_ITEM = 41;

    private static final int VARIABLES = 31;
    private static final int PREDICTOR_SEARCH = 32;
    private static final int MOOD_PREDICTORS = 33;
    private static final int SEARCH_YOUR_PREDICTORS = 34;
    private static final int SEARCH_COMMON_PREDICTORS = 35;

    private static final int ALL_MEASUREMENTS_ITEM = 42;
    private static final int HISTORY_GROUP_ITEM = 43;

    private ArrayList<DrawerAdapter.DrawerItem> createMenu(){
        ArrayList<DrawerAdapter.DrawerItem> items = new ArrayList<>();

        DrawerAdapter.DrawerItem item = new DrawerAdapter.DrawerItem(R.string.drawer_item_track,TRACK_ITEM,R.drawable.ic_tracking);
        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_outcome_variable, TRACK_MOOD_ITEM, R.drawable.ic_track));
        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_emotions, TRACK_EMOTIONS_ITEM,R.drawable.ic_emotions));
        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_diet, TRACK_DIET_ITEM, R.drawable.ic_diet));
        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_treatments, TRACK_TREATMENTS_ITEM, R.drawable.ic_medi));
        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_symptoms, TRACK_SYMPTOMS_ITEM, R.drawable.ic_symptom));
        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_physical, TRACK_PHYSICAL_ITEM, R.drawable.ic_directions_run_black_24dp));
        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_sleep,TRACK_SLEEP_ITEM,R.drawable.ic_airline_seat_individual_suite_black_24dp));
        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_misc, TRACK_MISC_ITEM, R.drawable.ic_tracking));
        items.add(item);

        items.add(new DrawerAdapter.DrawerItem(R.string.reminders_title, REMINDERS_ITEM, R.drawable.clock));
//        DrawerAdapter.DrawerItem itemReminders = new DrawerAdapter.DrawerItem(
//                R.string.reminders_title, REMINDERS_ITEM, R.drawable.clock);
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_reminders_inbox,
//                REMINDERS_INBOX_ITEM,R.drawable.ic_move_to_inbox_black_24dp));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.custom_reminders_manage,
//                REMINDERS_MANAGE_ITEM, R.drawable.ic_alarm_add_black_24dp));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_emotions,
//                REMINDERS_EMOTIONS_ITEM,R.drawable.ic_emotions));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_diet,
//                REMINDERS_DIET_ITEM, R.drawable.ic_diet));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_treatments,
//                REMINDERS_TREATMENTS_ITEM, R.drawable.ic_medi));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_symptoms,
//                REMINDERS_SYMPTOMS_ITEM, R.drawable.ic_symptom));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_physical,
//                REMINDERS_PHYSICAL_ITEM, R.drawable.ic_directions_run_black_24dp));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_sleep,
//                REMINDERS_SLEEP_ITEM, R.drawable.ic_airline_seat_individual_suite_black_24dp));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_misc,
//                REMINDERS_MISC_ITEM, R.drawable.ic_tracking));
//        itemReminders.put(new DrawerAdapter.DrawerItem(R.string.custom_reminders_settings,
//                REMINDERS_SETTINGS_ITEM, R.drawable.ic_gears));
//        items.add(itemReminders);

//        items.add(new DrawerAdapter.DrawerItem(R.string.custom_reminders_title,REMINDERS_ITEM,R.drawable.clock));

        DrawerAdapter.DrawerItem itemHistory = new DrawerAdapter.DrawerItem(R.string.drawer_item_history,HISTORY_GROUP_ITEM,R.drawable.ic_history);
        itemHistory.put(new DrawerAdapter.DrawerItem(BuildConfig.OUTCOME_VARIABLE, HISTORY_ITEM, R.drawable.ic_sentiment_satisfied_black_24dp));
        itemHistory.put(new DrawerAdapter.DrawerItem(getString(R.string.drawer_item_history_all_measurements), ALL_MEASUREMENTS_ITEM, R.drawable.ic_all_inclusive_black_24dp));
        items.add(itemHistory);
        items.add(new DrawerAdapter.DrawerItem(R.string.drawer_item_import_data, IMPORT_ITEM, R.drawable.ic_importdata));

        DrawerAdapter.DrawerItem itemMoodPredictors = new DrawerAdapter.DrawerItem(
                R.string.drawer_item_mood_predictors_title, MOOD_PREDICTORS, R.drawable.ic_record_voice_over_black_24dp);
        itemMoodPredictors.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_yours,
                YOUR_PREDICTORS_ITEM, R.drawable.ic_your_predictors));
        itemMoodPredictors.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_common,
                POSITIVE_FACTORS_ITEM, R.drawable.ic_common_predictors));
        items.add(itemMoodPredictors);

        DrawerAdapter.DrawerItem itemPredictorsSearch = new DrawerAdapter.DrawerItem(
                R.string.drawer_item_predictors_search_title, PREDICTOR_SEARCH, R.drawable.ic_search_black_24dp);
        itemPredictorsSearch.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_yours,
                SEARCH_YOUR_PREDICTORS, R.drawable.ic_your_predictors));
        itemPredictorsSearch.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_common,
                SEARCH_COMMON_PREDICTORS, R.drawable.ic_common_predictors));
        items.add(itemPredictorsSearch);

        items.add(new DrawerAdapter.DrawerItem(R.string.drawer_item_variables, VARIABLES, R.drawable.ic_variables));

        if (Utils.checkRoot()) {
            //items.add(new DrawerAdapter.DrawerItem(R.string.drawer_items_sync_data, SYNC_DATA_ITEM, R.drawable.ic_sync));
        }

//        item = new DrawerAdapter.DrawerItem(R.string.drawer_item_apps_and_devices, APPS_AND_DEVICES_ITEM,R.drawable.ic_appsanddevices);
//        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_exercise, TRACK_EXERCISE_ITEM, R.drawable.ic_exercise));
//        item.put(new DrawerAdapter.DrawerItem(R.string.drawer_item_track_vitals, TRACK_VITALS_ITEM, R.drawable.ic_vitals));
//        items.add(item);

        items.add(new DrawerAdapter.DrawerItem(R.string.drawer_item_settings, SETTINGS_ITEM, R.drawable.ic_gears));
        items.add(new DrawerAdapter.DrawerItem(R.string.drawer_item_help, ITEM_HELP_ITEM, R.drawable.ic_megaphone));

        return items;
    }

    private void initDrawerV2(){
        ArrayList<DrawerAdapter.DrawerItem> items = createMenu();

        DrawerFragment fragment =  DrawerFragment.newInstance(items);
        getFragmentManager().beginTransaction().replace(R.id.drawerContent,fragment).commit();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_navigation_drawer,R.string.drawer_open,R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    private boolean checkForAuth(String helpMessage){
        if (!mAuthHelper.isLoggedIn()){
            Intent intent = new Intent(this, QuantimodoLoginActivity.class);
            intent.putExtra(QuantimodoLoginActivity.KEY_APP_NAME, getString(R.string.app_name));
            intent.putExtra(QuantimodoLoginActivity.HELP_MESSAGE_PREFERENCE, helpMessage);
            startActivity(intent);
            return false;
        }

        return true;
    }

    private void changeFragment(Fragment fragment,String title){
        if (getFragmentManager().findFragmentById(R.id.mainFragment) != fragment) {
            getFragmentManager().beginTransaction().replace(R.id.mainFragment, mCurrentFragment).commit();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (getActionBar() != null) {
            getActionBar().setTitle(title);
        }
        invalidateOptionsMenu();
    }

    public void clickInbox(){
        if (!(getFragmentManager().findFragmentById(R.id.mainFragment) instanceof InboxWebFragment)){
            mCurrentFragment = InboxWebFragment.newInstance();
        }
        changeFragment(mCurrentFragment, "");
    }

    public void clickTracking(){
        if (!(getFragmentManager().findFragmentById(R.id.mainFragment) instanceof VariableFragment)){
            mCurrentFragment = VariableFragment.newInstance();
        }
        changeFragment(mCurrentFragment, "");
    }

    public void clickImport(){
        if (!checkForAuth(getString(R.string.help_import))) return;
        if (!(getFragmentManager().findFragmentById(R.id.mainFragment) instanceof ImportWebFragment)){
            mCurrentFragment =  ImportWebFragment.newInstance();
        }
        changeFragment(mCurrentFragment, getString(R.string.tab_import_data));
    }

    private void runOrInstallApk(String pack){
        try {
            getPackageManager().getApplicationInfo(pack, 0);

            Intent intent = getPackageManager().getLaunchIntentForPackage(pack);
            startActivity(intent);

        } catch (PackageManager.NameNotFoundException exception) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("market://details?id=%s",pack))));
            } catch (ActivityNotFoundException ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s",pack))));
            }

        }
    }

    public void clickTrackingFactors(@TrackingFragment.TrackingType int type){
        if (!checkForAuth("")) return;
        mCurrentFragment = TrackingFragment.newInstance(type);
        changeFragment(mCurrentFragment, getString(R.string.tab_track_data));
    }

    public void clickTrackingFactors(@TrackingFragment.TrackingType int type, String searchText){
        if (!checkForAuth("")) return;
        mCurrentFragment = TrackingFragment.newInstance(type, searchText);
        changeFragment(mCurrentFragment, getString(R.string.tab_track_data));
    }

    public void clickMeasurements(){
        if (!checkForAuth("")) return;
        mCurrentFragment = AllMeasurementsFragment.newInstance();
        changeFragment(mCurrentFragment,"");
    }

    public void clickSync(){
        runOrInstallApk("com.quantimodo.sync");
    }

    public void onFactorsClick(@CorrelationAdapter.PredictorType int predictorType){
        if (!checkForAuth(getString(R.string.help_prediction))) return;
        mCurrentFragment = FactorsFragment.newInstance(CorrelationAdapter.TYPE_ANY, BuildConfig.OUTCOME_VARIABLE , predictorType);

        String title;
        switch (predictorType){
            case CorrelationAdapter.PREDICTOR_PRIVATE:
                title = getString(R.string.your_mood_predictor_title);
                break;
            default:
                title = getString(R.string.common_mood_predictor_title);
                break;
        }
        changeFragment(mCurrentFragment, title);
    }

    public void clickSettings(){
        Intent openPrefsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(openPrefsIntent);
    }

    public void clickReminders(){
        runOrInstallApk("com.quantimodo.quantimodo");
    }

    public void clickHelp(){
        UserVoice.launchUserVoice(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

//        mCurrentFragment = getFragmentManager().findFragmentById(R.id.mainFragment);
        if (mCurrentFragment != null && (mCurrentFragment instanceof VariableFragment) || mCurrentFragment == null) {
            MenuItem item = menu.add("");
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            View vwTimeRange = getLayoutInflater().inflate(R.layout.view_action_timerange, null);
            btTimeRangeStart = (Button) vwTimeRange.findViewById(R.id.tvTimeRangeStart);
            btTimeRangeEnd = (Button) vwTimeRange.findViewById(R.id.tvTimeRangeEnd);

            initTimeRangeView();
            item.setActionView(vwTimeRange);

            menu.findItem(R.id.action_start_time).setVisible(true);
            menu.findItem(R.id.action_end_time).setVisible(true);
        }
        else{
            menu.findItem(R.id.action_start_time).setVisible(false);
            menu.findItem(R.id.action_end_time).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent openPrefsIntent = new Intent(this, SettingsActivity.class);
                startActivity(openPrefsIntent);
                return true;
            case R.id.action_start_time:
                onTimeRangeStartClicked.onClick(item.getActionView());
                return true;
            case R.id.action_end_time:
                onTimeRangeEndClicked.onClick(item.getActionView());
                return true;
            default:
                return false;
        }
    }

    private void initTimeRangeView() {
        updateTimeRangeView();

        btTimeRangeStart.setOnClickListener(onTimeRangeStartClicked);
        btTimeRangeEnd.setOnClickListener(onTimeRangeEndClicked);
    }

    public void onEventMainThread(UpdateDates event){
        updateTimeRangeView();
    }

    public void onEventMainThread(NoAuthEvent event){
        mCurrentFragment = VariableFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.mainFragment, mCurrentFragment).commit();
    }

    public void onEventMainThread(SyncFinished syncFinished){
        Global.updateDates(this,mDaoSession);
        updateTimeRangeView();
    }

    public void onEventMainThread(WelcomeFinishedEvent event){
        mCurrentFragment = event.fragment;
        invalidateOptionsMenu();
    }


    public void updateTimeRangeView() {
        if (btTimeRangeStart == null) {
            invalidateOptionsMenu();
        }
        final Calendar nowCalendar = Calendar.getInstance();
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(Global.moodChartStart);
        endCalendar.setTime(Global.moodChartEnd);

        SimpleDateFormat startDateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM);

        if (startCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)) {
            startDateFormat.applyPattern(Global.DEFAULT_DATE_FORMAT_WO_YEAR);
        } else {
            startDateFormat.applyPattern(Global.DEFAULT_DATE_FORMAT);
        }

        btTimeRangeStart.setText(startDateFormat.format(Global.moodChartStart));

        if (endCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR) && endCalendar.get(Calendar.DAY_OF_YEAR) == nowCalendar.get(Calendar.DAY_OF_YEAR)) {
            btTimeRangeEnd.setText(R.string.action_timerange_today);
        } else {
            SimpleDateFormat endDateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM);

            if (endCalendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR)) {
                endDateFormat.applyPattern(Global.DEFAULT_DATE_FORMAT_WO_YEAR);
            } else {
                endDateFormat.applyPattern(Global.DEFAULT_DATE_FORMAT);
            }

            btTimeRangeEnd.setText(endDateFormat.format(Global.moodChartEnd));
        }
    }

    private void onRemindersClick(){


    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment instanceof ImportWebFragment){
            clickTracking();
        } else {
            super.onBackPressed();
        }
    }

    View.OnClickListener onTimeRangeStartClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Calendar nowCalendar = Calendar.getInstance();
                    nowCalendar.set(Calendar.YEAR, year);
                    nowCalendar.set(Calendar.MONTH, month);
                    nowCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    nowCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    nowCalendar.set(Calendar.MINUTE, 0);

                    Global.moodChartStart = nowCalendar.getTime();
                    updateTimeRangeView();
                    QTools.getInstance().postEvent(new UpdateDates());
                }
            }, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH));

            if (Build.VERSION.SDK_INT >= 11) {
                dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
            }

            dialog.show();
        }
    };

    View.OnClickListener onTimeRangeEndClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    Calendar nowCalendar = Calendar.getInstance();
                    nowCalendar.set(Calendar.YEAR, year);
                    nowCalendar.set(Calendar.MONTH, month);
                    nowCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    if (endCalendar.get(Calendar.YEAR) != nowCalendar.get(Calendar.YEAR) && endCalendar.get(Calendar.DAY_OF_YEAR) != nowCalendar.get(Calendar.DAY_OF_YEAR)) {
                        nowCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        nowCalendar.set(Calendar.MINUTE, 0);
                    } else {
                        nowCalendar.set(Calendar.HOUR_OF_DAY, 23);
                        nowCalendar.set(Calendar.MINUTE, 59);
                        nowCalendar.set(Calendar.SECOND, 59);
                    }

                    Global.moodChartEnd = Global.rangeEndRequested = nowCalendar.getTime();
                    updateTimeRangeView();
                    QTools.getInstance().postEvent(new UpdateDates());
                }
            }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));

            if (Build.VERSION.SDK_INT >= 11) {
                dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
            }

            dialog.show();
        }
    };

    private Fragment createWebFragmentIfNeeded(int type){

        String targetUrl = "";
        String authToken = "";
        try {
            authToken = mAuthHelper.getAuthTokenWithRefresh();
        } catch (NoNetworkConnection noNetworkConnection) {
            noNetworkConnection.printStackTrace();
        }
        switch (type){
            case TRACK_EXERCISE_ITEM:
                targetUrl = QuantimodoWebFragment.URL_TRACK_EXERCISE;
                break;
            case TRACK_SLEEP_ITEM:
                targetUrl = QuantimodoWebFragment.URL_TRACK_SLEEP;
                break;
            case TRACK_VITALS_ITEM:
                targetUrl = QuantimodoWebFragment.URL_TRACK_VITALS;
                break;
            case TRACK_CORRELATE_ITEM:
                targetUrl = QuantimodoWebFragment.URL_CORRELATE;
                break;
            case VARIABLES:
                targetUrl = QuantimodoWebFragment.URL_VARIABLES+authToken;
                break;
            case SEARCH_COMMON_PREDICTORS:
                targetUrl = QuantimodoWebFragment.URL_COMMON_PREDICTORS+authToken;
                break;
            case SEARCH_YOUR_PREDICTORS:
                targetUrl = QuantimodoWebFragment.URL_YOUR_PREDICTORS+authToken;
                break;
        }

        if (mCurrentFragment != null && mCurrentFragment instanceof QuantimodoWebFragment) {
            String url = ((QuantimodoWebFragment) mCurrentFragment).getUrl();
            if (targetUrl.equals(url)) {
                return mCurrentFragment;
            }
        }

        return QuantimodoWebFragment.newInstance(targetUrl);
    }

    private void onMenu(DrawerAdapter.DrawerItem item){
        int tag = (Integer) item.getTag();

        switch (tag){
            case TRACK_MOOD_ITEM:
                clickTracking();
                break;
            case IMPORT_ITEM:
                clickImport();
                break;
            case REMINDERS_ITEM:
                clickReminders();
                break;
            case SETTINGS_ITEM:
                clickSettings();
                break;
            case POSITIVE_FACTORS_ITEM:
                onFactorsClick(CorrelationAdapter.PREDICTOR_COMMON);
                break;
            case YOUR_PREDICTORS_ITEM:
                onFactorsClick(CorrelationAdapter.PREDICTOR_PRIVATE);
                break;
            case SEARCH_COMMON_PREDICTORS:
                if(!checkForAuth(getString(R.string.help_prediction))) return;
                mCurrentFragment = createWebFragmentIfNeeded(SEARCH_COMMON_PREDICTORS);
                changeFragment(mCurrentFragment, getString(R.string.common_predictors_title));
                break;
            case SEARCH_YOUR_PREDICTORS:
                if(!checkForAuth(getString(R.string.help_prediction))) return;
                mCurrentFragment = createWebFragmentIfNeeded(SEARCH_YOUR_PREDICTORS);
                changeFragment(mCurrentFragment, getString(R.string.your_predictors_title));
                break;
            case VARIABLES:
                if(!checkForAuth("")) return;
                mCurrentFragment = createWebFragmentIfNeeded(VARIABLES);
                changeFragment(mCurrentFragment, getString(R.string.drawer_item_variables));
                break;
            case SYNC_DATA_ITEM:
                clickSync();
                break;
            case TRACK_MISC_ITEM:
                clickTrackingFactors(TrackingFragment.TYPE_ALL);
                break;
            case TRACK_DIET_ITEM:
                clickTrackingFactors(TrackingFragment.TYPE_DIET);
                break;

            case TRACK_EMOTIONS_ITEM:
                clickTrackingFactors(TrackingFragment.TYPE_EMOTIONS);
                break;

            case TRACK_TREATMENTS_ITEM:
                clickTrackingFactors(TrackingFragment.TYPE_TREATMENTS);
                break;

            case TRACK_SYMPTOMS_ITEM:
                clickTrackingFactors(TrackingFragment.TYPE_SYMPTOMS);
                break;
            case TRACK_PHYSICAL_ITEM:
                clickTrackingFactors(TrackingFragment.TYPE_PHYSICAL);
                break;
            case TRACK_SLEEP_ITEM:
                clickTrackingFactors(TrackingFragment.TYPE_SLEEP);
                break;


            case HISTORY_ITEM:
                Intent intent = new Intent(this,HistoryActivity.class);
                startActivity(intent);
                break;

            case ALL_MEASUREMENTS_ITEM:
                clickMeasurements();
                break;

            case REMINDERS_INBOX_ITEM:
                if (!checkForAuth("")) return;
                clickInbox();
                break;

            case REMINDERS_EMOTIONS_ITEM:
                startRemindersActivity("Emotions");
                break;
            case REMINDERS_DIET_ITEM:
                startRemindersActivity("Foods");
                break;
            case REMINDERS_SYMPTOMS_ITEM:
                startRemindersActivity("Symptoms");
                break;
            case REMINDERS_TREATMENTS_ITEM:
                startRemindersActivity("Treatments");
                break;
            case REMINDERS_PHYSICAL_ITEM:
                startRemindersActivity("Physical Activity");
                break;
            case REMINDERS_SLEEP_ITEM:
                startRemindersActivity("Sleep");
                break;
            case REMINDERS_MISC_ITEM:
                startRemindersActivity(null);
                break;
            case REMINDERS_MANAGE_ITEM:
                Intent remindersMain = new Intent(this,CustomRemindersActivity.class);
                startActivity(remindersMain);
                break;
            case REMINDERS_SETTINGS_ITEM:
                onRemindersClick();
                break;

            case TRACK_CORRELATE_ITEM:
            case TRACK_EXERCISE_ITEM:
            case TRACK_VITALS_ITEM:
                Fragment newFragment = createWebFragmentIfNeeded(tag);
                mCurrentFragment = newFragment;
                changeFragment(mCurrentFragment, getString(R.string.app_name));
                break;

            case ITEM_HELP_ITEM:
                clickHelp();
                break;
        }
    }

    public void startRemindersActivity(String category){
        Intent intent = new Intent(this,CustomReminderVarsList.class);
        if (category != null) {
            intent.putExtra(CustomReminderVarsList.EXTRA_CATEGORY_NAME, category);
        }
        startActivity(intent);
    }

    @Override
    public void onChildSelected(DrawerAdapter.DrawerItem item) {
        onMenu(item);
    }

    @Override
    public void onCategorySelected(DrawerAdapter.DrawerItem item) {
        onMenu(item);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) { //request from the tour activity
            mCurrentFragment = WelcomeFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.mainFragment, mCurrentFragment).commit();
        }
    }

    public void onEventMainThread(ShowStartTimeEvent e){
        onTimeRangeStartClicked.onClick(null);
    }

    public void onEventMainThread(ShowEndTimeEvent e){
        onTimeRangeEndClicked.onClick(null);
    }
}