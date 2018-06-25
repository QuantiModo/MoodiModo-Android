package com.quantimodo.tools.sync;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.*;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.R;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.events.RequestSyncStopEvent;
import com.quantimodo.tools.events.SyncFinished;
import com.quantimodo.tools.events.SyncStarted;
import com.quantimodo.tools.models.*;
import com.quantimodo.tools.models.Unit;
import com.quantimodo.tools.receivers.SyncStopReceiver;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.utils.QtoolsUtils;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static com.quantimodo.tools.ToolsPrefs.DEBUG_TAG;


/**
 * Helps user to sync data with QuantimodoService
 * This service should be invoked by {@link SyncHelper SyncHelper}
 */
public abstract class SyncService extends IntentService {

    public static final String TYPE_EXTRA = "type";
    public static final String TYPE_REQUEST_CLOSE = "rClose";
    public static final String SYNC_FROM_SCRATCH_KEY = "syncFromScratch";
    public static final String LAST_SUCCESFULL_SYNC_KEY = "lastSuccessfulMoodSync";
    public static final String LAST_SUCCESFULL_SEND_KEY = "lastSuccessfulMoodSend";
    public static final String LAST_META_SYNC = "lastMetaSync";
    public static final long DEFAULT_META_SYNC_TIMEOUT = 3600 * 24; //1 day
    private static final int FOREGROUND_ID = 1345;

    protected long lastSuccessfulMoodSync;
    protected long lastSuccessfulMoodSent;
    protected SharedPreferences mSharePrefs;

    private Notification.Builder mBuilder;
    private NotificationManager mNotificationManager;

    @Inject
    protected QuantimodoApiV2 mClient;

    protected String mToken;

    @Inject
    protected AuthHelper mAuthHelper;

    @Inject
    protected ToolsPrefs mToolPrefs;

    @Inject
    protected DaoSession mDaoSession;

    private Intent mCurrentIntent;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SyncService(String name) {
        super(name);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!mAuthHelper.isLoggedIn()){
            SyncReceiver.completeWakefulIntent(intent);
            return;
        }
        mCurrentIntent = intent;
        startSync(intent.getExtras());
        SyncReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initCrashReporting();
        QTools.getInstance().inject(this);
        QTools.getInstance().register(this);
        mSharePrefs = getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_PRIVATE);
        lastSuccessfulMoodSync = mSharePrefs.getLong(LAST_SUCCESFULL_SYNC_KEY, 0);
        lastSuccessfulMoodSent = mSharePrefs.getLong(LAST_SUCCESFULL_SEND_KEY,0);
        setIntentRedelivery(false);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        QTools.getInstance().unregister(this);
        super.onDestroy();
    }

    /**
     * This service could run in separate process
     * So any crash reporting tool should be initialized again
     */
    protected abstract void initCrashReporting();

    /**
     * Default behaviour for handling exceptions is to print stacktrace
     * In this method you should use your favorite crash reporting tool
     * @param ex Exception that should be handled ( logged for example )
     */
    protected void handleException(Exception ex){
        ex.printStackTrace();
    }

    /**
     * Defines if we need to show notification with progress bar,
     * and if service should run in foreground mode
     * @param bundle Params of intent
     * @return true if we need to show progress bar
     */
    protected boolean isLongRunning(Bundle bundle){
        return false;
    }

    /**
     * Create notification builder based on params of intent that invoked service
     * @param bundle Params of intent
     * @return Notification.Build with notification
     */
    protected Notification.Builder createNotificationBuilder(Bundle bundle){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Syncing data")
                .setProgress(100, 0, false)
                .setSmallIcon(getIconId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent intent = new Intent(this, SyncStopReceiver.class);
            intent.putExtra(TYPE_EXTRA, TYPE_REQUEST_CLOSE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_stat_av_pause, "Stop sync", pendingIntent);
        }

        return builder;
    }

    public void onEvent(RequestSyncStopEvent requestSyncStop){
        requestStop();
    }

    /**
     * Request service stop, for example from notification
     */
    protected void requestStop() {
        stopForeground(true);
        SyncReceiver.completeWakefulIntent(mCurrentIntent);
        stopSelf();
    }

    /**
     * Starts service in foreground service
     * @param bundle Params of intent
     */
    protected void runForeground(Bundle bundle){
        mBuilder = createNotificationBuilder(bundle);
        Notification notification = mBuilder.getNotification();
        startForeground(FOREGROUND_ID, notification);
    }

    /**
     * Icon for foreground service, should be defined if {@link SyncService#isLongRunning(Bundle) SyncService.isLongRunning()} can return true
     * @return
     */
    protected abstract int getIconId();

    /**
     * Inform notification about service progress
     * @param progress int value between 0 and 100
     * @param text message for notification , for example "Syncing measurements" , that represent current operation
     */
    protected void commitProcess(int progress,String text){
        mBuilder.setProgress(100, progress, false);
        if (text != null){
            mBuilder.setContentText(text);
        }
        mNotificationManager.notify(FOREGROUND_ID, mBuilder.getNotification());
    }

    private void startSync(Bundle bundle) {
        Log.i(DEBUG_TAG, "Sync invoked!");

        boolean successful = true;
        try
        {
            if (!beforeSync(bundle)){
                Log.i(DEBUG_TAG,"Error during sync!");
                return;
            }

            QTools.getInstance().postEvent(new SyncStarted());

            if (isLongRunning(bundle)){
                runForeground(bundle);
            }

            sync(bundle);

            afterSync();
        }
        catch (Exception e)
        {
            handleException(e);
            successful = false;
        } finally {
            stopForeground(true);
            QTools.getInstance().postEvent(new SyncFinished(successful));
        }

        Log.i(DEBUG_TAG, "Sync finished.");
    }

    /**
     * Actual sync done here
     * @param bundle
     * @throws Exception
     */
    protected abstract void sync(Bundle bundle) throws Exception;

    /**
     * Some initiation of service, get auth token
     * Database connection, other pre-checks should be done here
     * @param bundle params of intent
     * @return true if init of sync process was done successful
     * @throws Exception
     */
    protected boolean beforeSync(Bundle bundle) throws Exception{

        SharedPreferences sp = getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_MULTI_PROCESS);
        if(bundle.getBoolean(SYNC_FROM_SCRATCH_KEY,false)){
            Log.i(DEBUG_TAG,"Syncing from scratch");
            sp.edit().remove(ToolsPrefs.PREF_SYNC_FROM)
                    .remove(LAST_SUCCESFULL_SYNC_KEY)
                    .apply();
            lastSuccessfulMoodSync = 0;
        }

        if(!QtoolsUtils.hasInternetConnection(this)){
            Log.i(DEBUG_TAG, "No active network connection");
            return false;
        }

        mToken = mAuthHelper.getAuthTokenWithRefresh();

        if(mToken == null)
        {
            Log.i(DEBUG_TAG,"Authentication failed");
            return false;
        }

        return true;
    }

    protected Category getCategoryForName(ArrayList<Category> cats, String name){
        for (Category c : cats){
            if (c.getName().equals(name)){
                return c;
            }
        }

        return null;
    }

    protected Unit getUnitForAbbreviatedName(ArrayList<Unit> units, String abbreviatedName){
        for (Unit u : units){
            if (u.getAbbreviatedName().equals(abbreviatedName)){
                return u;
            }
        }

        return null;
    }

    /**
     * Invoke sync of measurments for variables, that are in local DB
     */
    protected void syncMeasurements(){
        VariableDao variableDao = mDaoSession.getVariableDao();
        List<com.quantimodo.tools.models.Variable> vars = variableDao.loadAll();
        //Check if we can get measurments without specify variable
    }

    /**
     * Invoke META sync, which would sync data like:
     * <ul>
     *     <li>Variable Categories</li>
     *     <li>Units</li>
     *     <li>User variables</li>
     * </ul>
     */
    protected void defaultSync(){
        long lastMetaSync = mSharePrefs.getLong(LAST_META_SYNC, 0);
        if (System.currentTimeMillis() / 1000 - lastMetaSync < DEFAULT_META_SYNC_TIMEOUT){
            return;
        }

        SdkResponse<ArrayList<VariableCategory>> response = mClient.getCategories(this,mToken);
        ArrayList<Category> categories = new ArrayList<>();
        if (response.isSuccessful()){
            CategoryDao categoryDao = mDaoSession.getCategoryDao();
            for (VariableCategory cat : response.getData()){
                categories.add(Category.fromVariableCategory(cat));
            }
            categoryDao.insertOrReplaceInTx(categories);
        }

        SdkResponse<ArrayList<com.quantimodo.android.sdk.model.Unit>> unitsResponse =  mClient.getUnits(this, mToken);
        ArrayList<Unit> units = new ArrayList<>();
        if (unitsResponse.isSuccessful()){
            UnitDao unitDao = mDaoSession.getUnitDao();
            for (com.quantimodo.android.sdk.model.Unit unit : unitsResponse.getData()){
                Unit u = Unit.fromWsUnit(unit);
                units.add(u);
            }
            unitDao.insertOrReplaceInTx(units);
        }

        //What with unit conversations after sync?
        SdkResponse<ArrayList<Variable>> variablesResponse = mClient.searchVariables(this,mToken,"",100,0,null,null);
        if(variablesResponse.isSuccessful()){
            VariableDao variableDao = mDaoSession.getVariableDao();
            ArrayList<com.quantimodo.tools.models.Variable> vars = new ArrayList<>();
            for (Variable v : variablesResponse.getData()){
                com.quantimodo.tools.models.Variable tv = com.quantimodo.tools.models.Variable.fromVariable(v);
                tv.setUnit(getUnitForAbbreviatedName(units, v.getDefaultAbbreviatedUnitName()));
                tv.setCategory(getCategoryForName(categories,v.getCategory()));
                vars.add(tv);
            }
            variableDao.insertOrReplaceInTx(vars);
        }

        mSharePrefs.edit().putLong(LAST_META_SYNC,System.currentTimeMillis() / 1000).apply();
    }

    /**
     * Post-sync process, re-usable resources should be freed here
     */
    protected void afterSync(){

    }
}
