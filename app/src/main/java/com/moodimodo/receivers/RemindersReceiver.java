package com.moodimodo.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.moodimodo.R;
import com.moodimodo.activities.MainActivity;
import com.moodimodo.dialogs.ReminderDialog;

import java.util.Date;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code RemindersService} to do some work.
 */
public class RemindersReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = RemindersReceiver.class.getSimpleName();
    public static final String EXTRA_TYPE = "extra_reminder_type";
    public static final String EXTRA_REQUEST_ALARM = "extra_request_alarm";
    public static final String EXTRA_REQUEST_REMINDER = "extra_request_reminder";
    public static final String EXTRA_REQUEST_SNOOZE = "extra_request_snooze";
    public static final String EXTRA_REQUEST_SKIP = "extra_request_skip";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    public static final long INTERVAL_THREE_HOURS = 3 * AlarmManager.INTERVAL_HOUR;

    public enum RemindersType{
        MOOD,
        DIET,
        TREATMENT,
        SYMPTOM,
        PHYSICAL
    }

    public enum FrecuencyType{
        HOURLY,
        EVERY_THREE_HOURS,
        TWICE_A_DAY,
        DAILY,
        SNOOZE
    }

    public enum NotificationType{
        TRACK,
        SNOOZE,
        SKIP
    }

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Intent service = new Intent(context, RemindersService.class);
        if(intent.hasExtra(EXTRA_REQUEST_SKIP)){
            cancelNotification(context, extras.getInt(EXTRA_NOTIFICATION_ID));
        }
        if(!intent.hasExtra(EXTRA_TYPE)){
            return;
        }
        final int type = extras.getInt(EXTRA_TYPE, RemindersType.MOOD.ordinal());
        Log.d(TAG, "onReceive, extra type value: " + type);

        if(intent.hasExtra(EXTRA_REQUEST_ALARM)) {
            ReminderDialog.getInstance().show(context, type);
            service.putExtra(EXTRA_TYPE, type);
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, service);
        }
        else if(intent.hasExtra(EXTRA_REQUEST_REMINDER)){
            cancelNotification(context, extras.getInt(EXTRA_NOTIFICATION_ID));
            startTracking(context, RemindersType.values()[type]);
        }
        else if(intent.hasExtra(EXTRA_REQUEST_SNOOZE)){
            cancelNotification(context, extras.getInt(EXTRA_NOTIFICATION_ID));
            setAlarm(context, RemindersType.values()[type], FrecuencyType.SNOOZE);
        }
    }

    private void cancelNotification(Context context, int notifId){
        NotificationManager notifManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notifManager.cancel(notifId);
        //the same time when closing the notification we close the popup if any.
        if(ReminderDialog.getInstance().isShowing())
            ReminderDialog.getInstance().dismiss(context);
    }

    private void startTracking(Context context, RemindersType type){
        Intent trackIntent = new Intent(context, MainActivity.class);
        trackIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switch (type){
            case MOOD: trackIntent.putExtra(MainActivity.EXTRA_TRACK_MOOD, true);
                break;
            case DIET: trackIntent.putExtra(MainActivity.EXTRA_TRACK_DIET, true);
                break;
            case TREATMENT: trackIntent.putExtra(MainActivity.EXTRA_TRACK_TREATMENT, true);
                break;
            case SYMPTOM: trackIntent.putExtra(MainActivity.EXTRA_TRACK_SYMPTOM, true);
                break;
            case PHYSICAL: trackIntent.putExtra(MainActivity.EXTRA_TRACK_PHYSICAL, true);
                break;
        }
        context.startActivity(trackIntent);
    }

    /**
     * Sets the alarm for the selected reminder type
     * @param type The type of the reminder to track
     * @param frecuencyType The frecuency that the reminder has to be shown
     * @param context The current context
     */
    public void setAlarm(Context context, RemindersType type, FrecuencyType frecuencyType) {
        Log.d(TAG, "setting Alarm, type ordinal: " + type.ordinal());
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RemindersReceiver.class);
        intent.putExtra(EXTRA_TYPE, type.ordinal());
        intent.putExtra(EXTRA_REQUEST_ALARM, true);

        alarmIntent = PendingIntent.getBroadcast(context, type.ordinal(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch(frecuencyType){
            case HOURLY:
                alarmMgr.setRepeating(AlarmManager.RTC,
                        //Testing line:
//                        System.currentTimeMillis() + (10 * 1000), 120 * 1000, alarmIntent);
                        System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
                        AlarmManager.INTERVAL_HOUR, alarmIntent);
                break;
            case EVERY_THREE_HOURS:
                alarmMgr.setRepeating(AlarmManager.RTC,
                        System.currentTimeMillis() + INTERVAL_THREE_HOURS,
                        INTERVAL_THREE_HOURS,
                        alarmIntent);
                break;
            case TWICE_A_DAY:
                alarmMgr.setRepeating(AlarmManager.RTC,
                        System.currentTimeMillis() + AlarmManager.INTERVAL_HALF_DAY,
                        AlarmManager.INTERVAL_HALF_DAY,
                        alarmIntent);
                break;
            case DAILY:
                alarmMgr.setRepeating(AlarmManager.RTC,
                        System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
                        AlarmManager.INTERVAL_DAY,
                        alarmIntent);
                break;
            case SNOOZE:
                //Create a one time reminder to run in one hour as a snooze of a previous reminder
                //We crete a new intent to not replace the running ones
                alarmIntent = PendingIntent.getBroadcast(context, (int) new Date().getTime(),
                        intent, PendingIntent.FLAG_ONE_SHOT);
                alarmMgr.set(AlarmManager.RTC,
                        //Testing line:
//                        SystemClock.elapsedRealtime() + (10*1000), alarmIntent);
                        System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR, alarmIntent);
                Toast.makeText(context, R.string.reminders_snooze_message, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Cancels the alarm.
     * @param context current context
     */
    public void cancelAlarm(Context context, RemindersType type) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RemindersReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, type.ordinal(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);

    }
}
