package com.moodimodo.sync;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.moodimodo.BuildConfig;
import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.events.MeasurementsUpdatedEvent;
import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.*;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.models.*;
import com.quantimodo.tools.models.Measurement;
import com.quantimodo.tools.models.Unit;
import com.quantimodo.tools.models.Variable;
import com.quantimodo.tools.sdk.SdkException;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;
import com.quantimodo.tools.utils.CustomRemindersHelper;

import io.fabric.sdk.android.Fabric;
import io.swagger.client.ApiException;
import io.swagger.client.api.RemindersApi;
import io.swagger.client.model.InlineResponse200;
import io.swagger.client.model.TrackingReminder;

import javax.inject.Inject;
import javax.inject.Named;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class SyncService extends com.quantimodo.tools.sync.SyncService {

    private final static String TAG = "SyncService";
    private final static int LIMIT = 200;
    private final static int RETRY_COUNT = 3;

    @Inject
    DaoSession mSession;

    @Inject @Named("outcome")
    Variable mVariable;

    @Inject @Named("outcome")
    Unit mUnit;

    private ThreadPoolExecutor mThreadPoolExecutor;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SyncService() {
        super("Sync measurements");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void initCrashReporting() {
        Fabric.with(this, new Crashlytics());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mThreadPoolExecutor = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
    }

    @Override
    protected void handleException(Exception ex) {
        super.handleException(ex);
        if (!BuildConfig.DEBUG) {
            Crashlytics.logException(ex);
        } else {
            Log.e(TAG, ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    protected boolean beforeSync(Bundle bundle) throws Exception {
        defaultSync();
        return super.beforeSync(bundle);
    }

    @Override
    protected void sync(Bundle bundle) throws Exception {
        boolean putOk = putData();
        boolean loadOk = loadDataFromWs(bundle);
        //boolean remindersOk = syncReminders();

        //if (putOk && loadOk && remindersOk) {
        if (putOk && loadOk) {
            mSharePrefs.edit().putLong(LAST_SUCCESFULL_SYNC_KEY, System.currentTimeMillis()).apply();
        }
    }

    private boolean putData() {
        MeasurementDao measurementDao = mSession.getMeasurementDao();
        List<Measurement> unsyncedMeasurements = measurementDao.queryBuilder()
                .whereOr(MeasurementDao.Properties.Timestamp.ge(new Date(lastSuccessfulMoodSent)),MeasurementDao.Properties.NeedUpdate.eq(true))
                .list();


        if (unsyncedMeasurements.size() > 0) {
            ArrayList<MeasurementSet> sets = measurementToSet(unsyncedMeasurements);

            SdkResponse<Integer> result = mClient.putMeasurements(this, mToken, sets);
            if (result.isSuccessful()) {
                mSharePrefs.edit().putLong(LAST_SUCCESFULL_SEND_KEY, System.currentTimeMillis()).apply();
                for(Measurement m : unsyncedMeasurements){
                    m.setNeedUpdate(false);
                }
                //Setting all synced
                measurementDao.updateInTx(unsyncedMeasurements);
                Log.i(TAG, "New successful sync: " + System.currentTimeMillis());
                return true;
            } else {
                if (result.getCause() != null){
                    Crashlytics.log(result.getStringBody());
                    handleException(result.getCause());
                }
                Log.i(TAG, "Error during sync!");
            }
        } else {
            Log.i(TAG, "Nothing to sync");
            return true;
        }
        return false;
    }

    @Override
    protected boolean isLongRunning(Bundle bundle) {
        return true;
    }

    private boolean loadDataFromWs(Bundle bundle) throws ExecutionException, InterruptedException, SdkException {

        SdkResponse<ArrayList<HistoryMeasurement>> sdkResponse = null;
        SdkResponse<ArrayList<HistoryMeasurement>> response = null;
        ArrayList<HistoryMeasurement> measurements = null;
        int offset = 0;
        int size = 100000;
        long lastSyncTime = 0;
        int percentComplete = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -15);
        final Date createdAt = cal.getTime();
        final Date updatedAt = new Date(mSharePrefs.getLong(Global.PREF_SYNC_FROM,1) * 1000);
        Log.d(TAG, "Loading data from WS: created at less than: " + createdAt +
                ", and updated at greater than: " + updatedAt);

        do {
            commitProcess(percentComplete, null);
            sdkResponse = mClient.getMeasurmentHistory(SyncService.this, mToken,
                    createdAt, updatedAt, BuildConfig.OUTCOME_VARIABLE ,
                    null, Global.DEFAULT_UNIT, LIMIT, offset);
            if (sdkResponse.isSuccessful()) {
                measurements = sdkResponse.getData();
                Log.d(TAG, "Got " + measurements.size() + " measurements");
                if(measurements.size() < LIMIT){
                    Log.d(TAG, "Did not get " + LIMIT + " measurements in last request so stopping sync.");
                    break;
                }
                writeInDb(measurements);
                offset += LIMIT;
                if(percentComplete < 100){
                    percentComplete++;
                }
            } else {
                return false;
            }

        } while (true);

        lastSyncTime = createdAt.getTime() / 1000;
        mSharePrefs.edit().putLong(Global.PREF_SYNC_FROM, lastSyncTime).apply();

        QTools.getInstance().postEvent(new MeasurementsUpdatedEvent(true));
        return true;
    }

    private void writeInDb(List<HistoryMeasurement> measurements) {
        MeasurementDao measurementDao = mSession.getMeasurementDao();
        try {
            ArrayList<Measurement> ms = new ArrayList<>();
            for (HistoryMeasurement measurement : measurements) {
                Measurement m = Measurement.fromHistoryMeasurement(measurement);
                m.setVariable(mVariable);
                m.setUnit(mUnit);
                ms.add(m);
            }
            measurementDao.insertOrReplaceInTx(ms);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    public ArrayList<MeasurementSet> measurementToSet(List<Measurement> measurement){
        HashMap<String,MeasurementSet> sets = new HashMap<>();
        for (Measurement m : measurement){
            MeasurementSet set = sets.get(m.getVariableName());

            if (set == null){
                set = new MeasurementSet(
                        m.getVariableName(),
                        null,
                        BuildConfig.OUTCOME_CATEGORY,
                        m.getUnitName(),
                        MeasurementSet.COMBINE_MEAN,
                        Global.QUANTIMODO_SOURCE_NAME);
                sets.put(m.getVariableName(),set);
            }

            com.quantimodo.android.sdk.model.Measurement meas = new com.quantimodo.android.sdk.model.Measurement(m.getTimestamp().getTime() / 1000,m.getValue());
            meas.setNote(m.getNote());
            set.getMeasurements().add(meas);
        }

        return new ArrayList<>(sets.values());
    }

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);

    static {
        // Use UTC as the default time zone.
        DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    private boolean syncReminders(){
        Log.d(TAG, "Syncing reminders");
        RemindersApi api = new RemindersApi();
        try {
            final String token = mAuthHelper.getAuthTokenWithRefresh();
            //try to upload the sync marked reminders
            for(CustomRemindersHelper.Reminder reminder : CustomRemindersHelper.getRemindersList(this)){
                if(reminder.needUpdate) {
                    Log.d(TAG, "Posting need update reminder variable id: " + reminder.id);
                    TrackingReminder bodyReminder = new TrackingReminder();
                    bodyReminder.setId(reminder.remoteId);
                    bodyReminder.setVariableId(Integer.parseInt(reminder.id));
                    bodyReminder.setDefaultValue(Float.parseFloat(reminder.value));
                    bodyReminder.setCombinationOperation(
                            TrackingReminder.CombinationOperationEnum.valueOf(reminder.combinationOperation)
                    );
                    bodyReminder.setAbbreviatedUnitName(reminder.unitName);
                    bodyReminder.setVariableCategoryName(reminder.variableCategory);
                    switch (CustomRemindersHelper.FrequencyType.values()[reminder.frequencyIndex]){
                        case NEVER: bodyReminder.setReminderFrequency(0);
                            break;
                        case HOURLY: bodyReminder.setReminderFrequency(60*60);
                            break;
                        case EVERY_THREE_HOURS: bodyReminder.setReminderFrequency(3*60*60);
                            break;
                        case TWICE_A_DAY: bodyReminder.setReminderFrequency(12*60*60);
                            bodyReminder.setFirstDailyReminderTime(DATE_TIME_FORMAT.format(reminder.time1));
                            bodyReminder.setSecondDailyReminderTime(DATE_TIME_FORMAT.format(reminder.time2));
                            break;
                        case THREE_TIMES_A_DAY: bodyReminder.setReminderFrequency(8*60*60);
                            bodyReminder.setFirstDailyReminderTime(DATE_TIME_FORMAT.format(reminder.time1));
                            bodyReminder.setSecondDailyReminderTime(DATE_TIME_FORMAT.format(reminder.time2));
                            bodyReminder.setThirdDailyReminderTime(DATE_TIME_FORMAT.format(reminder.time3));
                            break;
                        case DAILY: bodyReminder.setReminderFrequency(24*60*60);
                            bodyReminder.setFirstDailyReminderTime(DATE_TIME_FORMAT.format(reminder.time1));
                            break;
                        case EVERY_THIRTY_MINUTES: bodyReminder.setReminderFrequency(30*60);
                            break;
                    }
                    Log.d(TAG, bodyReminder.toString());
                    if(api.v1TrackingRemindersPost(token, bodyReminder).getSuccess()){
                        CustomRemindersHelper.putReminder(this, new CustomRemindersHelper.Reminder(
                                reminder.id,
                                reminder.remoteId,
                                reminder.name,
                                reminder.variableCategory,
                                reminder.combinationOperation,
                                reminder.value,
                                reminder.unitName,
                                CustomRemindersHelper.FrequencyType.values()[reminder.frequencyIndex],
                                false,
                                reminder.time1,
                                reminder.time2,
                                reminder.time3
                        ));
                    }
                    else{
                        Log.d(TAG, "Error when Posting reminder (v1TrackingRemindersPost)");
                        return false;
                    }
                }
            }
            //then get all of them
            InlineResponse200 response = api.v1TrackingRemindersGet(token, null, null, null, null, null);
            if(response.getSuccess()){
                ArrayList<CustomRemindersHelper.Reminder> locals = CustomRemindersHelper.getRemindersList(this);
                //then update the existing ones using the data from server
                for(TrackingReminder apiReminder : response.getData()){
                    final boolean exist = CustomRemindersHelper.existReminder(this, apiReminder.getVariableId().toString());
                    long time1 = 0, time2 = 0, time3 = 0;
                    if(apiReminder.getFirstDailyReminderTime() != null) {
                        time1 = DATE_TIME_FORMAT.parse(apiReminder.getFirstDailyReminderTime()).getTime();
                    }
                    if(apiReminder.getSecondDailyReminderTime() != null) {
                        time2 = DATE_TIME_FORMAT.parse(apiReminder.getSecondDailyReminderTime()).getTime();
                    }
                    if(apiReminder.getThirdDailyReminderTime() != null) {
                        time3 = DATE_TIME_FORMAT.parse(apiReminder.getThirdDailyReminderTime()).getTime();
                    }
                    CustomRemindersHelper.Reminder newReminder = new CustomRemindersHelper.Reminder(
                            apiReminder.getVariableId().toString(), //id
                            apiReminder.getId(), //remoteId
                            apiReminder.getVariableName(),//name
                            apiReminder.getVariableCategoryName(), //variable category
                            apiReminder.getCombinationOperation().name(), //combination operation
                            apiReminder.getDefaultValue().toString(),
                            apiReminder.getAbbreviatedUnitName(), //abbreviatedUnitName name
                            getFrequencyIndexFromSeconds(apiReminder.getReminderFrequency()),
                            false,
                            time1,
                            time2,
                            time3
                    );
                    CustomRemindersHelper.putReminder(this, newReminder);
                    //start the alarm for new ones
                    if(!exist){
                        CustomRemindersHelper.startAlarms(this, newReminder.id);
                    }
                    else{
                        //removing from locals list to keep track of what reminders where updated
                        removeFromList(locals, newReminder.id);
                    }
                }
                //if we still have reminders on locals list, means don't exist on server and
                //have to be removed, but only if don't need update
                for (CustomRemindersHelper.Reminder reminder : locals){
                    if(!reminder.needUpdate){
                        CustomRemindersHelper.removeReminder(this, reminder.id);
                    }
                }
            }
            else{
                Log.d(TAG, "Error when getting reminders (v1TrackingRemindersGet)");
                return false;
            }
        } catch (ApiException | NoNetworkConnection | ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void removeFromList(List<CustomRemindersHelper.Reminder> list, String reminderId){
        for(int i=0; i<list.size(); i++){
            if(list.get(i).id.equals(reminderId)){
                list.remove(i);
                i--;
            }
        }
    }

    private CustomRemindersHelper.FrequencyType getFrequencyIndexFromSeconds(int seconds){
        if(seconds == 60*60) return CustomRemindersHelper.FrequencyType.HOURLY;
        else if(seconds == 3*60*60) return CustomRemindersHelper.FrequencyType.EVERY_THREE_HOURS;
        else if(seconds == 12*60*60) return CustomRemindersHelper.FrequencyType.TWICE_A_DAY;
        else if(seconds == 24*60*60) return CustomRemindersHelper.FrequencyType.DAILY;
        else if(seconds == 8*60*60) return CustomRemindersHelper.FrequencyType.THREE_TIMES_A_DAY;
        else if(seconds == 30*60) return CustomRemindersHelper.FrequencyType.EVERY_THIRTY_MINUTES;
        return CustomRemindersHelper.FrequencyType.NEVER;
    }
    @Override
    protected int getIconId() {
        return R.drawable.ic_sync_notification;
    }
}
