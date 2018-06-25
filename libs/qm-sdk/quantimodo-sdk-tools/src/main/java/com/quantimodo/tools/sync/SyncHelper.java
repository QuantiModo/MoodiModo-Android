package com.quantimodo.tools.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.quantimodo.tools.ToolsPrefs;

/**
 * Helper class to control sync
 * Create/updates/deletes pending intents for AlarmManager
 * By default sync invoked once per 12 hours
 * {@link SyncReceiver SyncReceiver} should be added to AndroidManifest.xml file, in order to use this helper
 */
public class SyncHelper {

    private static final int ID = 14754;
    public static final long RUN_SYNC_EVERY_TIME = 12 * 60 * 60 * 1000; //12 Hours

    /**
     * Checks if sync flag set in Shared prefs
     * @param context Context
     * @return true if flag is set
     */
    public static boolean isSync(Context context){
        SharedPreferences preferences = context.getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_PRIVATE);
        return preferences.getBoolean(ToolsPrefs.PREF_SYNC_ENABLED,false);
    }

    /**
     * Set flag of sync, creates repeating pending intent, and invoke sync in 5 seconds
     * @param ctx Context
     */
    public static void scheduleSync(Context ctx){
        scheduleSync(ctx, 5000);
    }

    /**
     * Set flag of sync, creates repeating pending intent, and invoke sync in <b>firstStart</b> milliseconds
     * @param ctx Context
     * @param firstStart delay in milliseconds before first start
     */
    public static void scheduleSync(Context ctx, long firstStart){
        Intent intent = new Intent(ctx,SyncReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,firstStart,RUN_SYNC_EVERY_TIME,alarmIntent);
        SharedPreferences preferences = ctx.getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(ToolsPrefs.PREF_SYNC_ENABLED,true).apply();
    }

    /**
     * Remove flag,cancel pending intent of sync
     * @param ctx Context
     */
    public static void unscheduleSync(Context ctx){
        Intent intent = new Intent(ctx,SyncReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, ID, intent, PendingIntent.FLAG_NO_CREATE);

        SharedPreferences preferences = ctx.getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(ToolsPrefs.PREF_SYNC_ENABLED, false).apply();
        if (alarmIntent != null){
            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(alarmIntent);
        }
    }

    /**
     * Removes data related to sync, like last sync time
     * @param ctx Context
     */
    public static void cleanSyncSettings(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_MULTI_PROCESS);
        sp.edit().putLong(ToolsPrefs.PREF_SYNC_FROM, 1).commit();
    }

    /**
     * Start sync process, ignoring any data that already synced,
     * so sync done from start
     * @param ctx Context
     * @param schedule true if sync should be scheduled
     */
    public static void invokeFullSync(Context ctx,boolean schedule){
        Intent intent = new Intent(ctx,SyncReceiver.class);
        intent.putExtra(SyncService.SYNC_FROM_SCRATCH_KEY,true);
        if (schedule){
            scheduleSync(ctx, RUN_SYNC_EVERY_TIME);
        }
        ctx.sendBroadcast(intent);
    }

    /**
     * Start sync process
     * @param context Context
     */
    public static void invokeSync(Context context){
        Intent intent = new Intent(context,SyncReceiver.class);
        context.sendBroadcast(intent);
    }

}
