package com.quantimodo.tools.receivers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.android.sdk.login.ToolsPrefs;
import com.quantimodo.android.sdk.model.Measurement;
import com.quantimodo.android.sdk.model.MeasurementSet;
import com.quantimodo.tools.QApplication;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.R;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;
import com.quantimodo.tools.utils.CustomRemindersHelper;
import com.quantimodo.tools.utils.QtoolsUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.quantimodo.tools.receivers.CustomRemindersReceiver.EXTRA_ACTION;
import static com.quantimodo.tools.utils.CustomRemindersHelper.EXTRA_REMINDER_ID;
import static com.quantimodo.tools.utils.CustomRemindersHelper.EXTRA_SPECIFIC_ID;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class RemindersService extends IntentService {
    private static final String TAG = RemindersService.class.getSimpleName();

    public static final String ACTION_NOTIFICATION = "notification";
    public static final String ACTION_SEND_VALUE = "send";

    @Inject
    AuthHelper mAuthHelper;

    @Inject
    protected QuantimodoApiV2 mClient;

    public RemindersService() {
        super("RemindersService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        QTools.getInstance().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null || !extras.containsKey(EXTRA_REMINDER_ID)){
            CustomRemindersReceiver.completeWakefulIntent(intent);
            Log.d(TAG,"Check intent");
            return;
        }


        try {
            String reminderId = intent.getExtras().getString(EXTRA_REMINDER_ID);
            int specificId = intent.getExtras().getInt(EXTRA_SPECIFIC_ID);
            String action = extras.getString(EXTRA_ACTION, ACTION_NOTIFICATION);

            CustomRemindersHelper.Reminder reminder = CustomRemindersHelper.getReminder(this, reminderId);

            switch (action) {
                case ACTION_NOTIFICATION:
                    sendNotification(reminder, specificId);
                    break;

                case ACTION_SEND_VALUE:
                    sendData(reminder,specificId);
                    break;
            }
        } finally {
            CustomRemindersReceiver.completeWakefulIntent(intent);
        }
    }

    private void sendData(CustomRemindersHelper.Reminder reminder, int specificId){
        try {
            String token = mAuthHelper.getAuthTokenWithRefresh();

            MeasurementSet set = new MeasurementSet(
                    reminder.name,
                    null,
                    reminder.variableCategory,
                    reminder.unitName,
                    reminder.combinationOperation,"MoodiModo");

            set.getMeasurements().add(new Measurement(System.currentTimeMillis() / 1000,Double.parseDouble(reminder.value)));

            ArrayList<MeasurementSet> sets = new ArrayList<>();
            sets.add(set);

            mClient.putMeasurements(this,token,sets);
            Log.d(TAG,"Measurement sent");
        } catch (NoNetworkConnection noNetworkConnection) {
            noNetworkConnection.printStackTrace();
        }
    }

    /**
     * Popup the notification and prepares de action over notifications
     * @param reminder the reminder object to display
     */
    private void sendNotification(CustomRemindersHelper.Reminder reminder, int specificId) {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        String reminderId = reminder.id;

        Intent snoozeIntent = new Intent(this, CustomRemindersReceiver.class);
        Intent trackIntent = new Intent(this, CustomRemindersReceiver.class);
        Intent editIntent = new Intent(this, CustomRemindersReceiver.class);
        Intent popupIntent = new Intent(this, CustomRemindersReceiver.class);


        snoozeIntent.putExtra(CustomRemindersReceiver.EXTRA_NOTIFICATION_ID, specificId);
        snoozeIntent.putExtra(EXTRA_REMINDER_ID, reminderId);
        snoozeIntent.putExtra(CustomRemindersReceiver.EXTRA_REQUEST_SNOOZE, true);
        trackIntent.putExtra(CustomRemindersReceiver.EXTRA_NOTIFICATION_ID, specificId);
        trackIntent.putExtra(EXTRA_REMINDER_ID, reminderId);
        trackIntent.putExtra(CustomRemindersReceiver.EXTRA_REQUEST_REMINDER, true);
        editIntent.putExtra(CustomRemindersReceiver.EXTRA_NOTIFICATION_ID, specificId);
        editIntent.putExtra(EXTRA_REMINDER_ID, reminderId);
        editIntent.putExtra(CustomRemindersReceiver.EXTRA_REQUEST_EDIT, true);
        popupIntent.putExtra(CustomRemindersReceiver.EXTRA_NOTIFICATION_ID, specificId);
        popupIntent.putExtra(EXTRA_REMINDER_ID, reminderId);
        popupIntent.putExtra(CustomRemindersReceiver.EXTRA_REQUEST_POPUP, true);

        Random rnd = new Random();
        PendingIntent trackPendingIntent = PendingIntent.getBroadcast(this,
                rnd.nextInt(1000),
                trackIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this,
                rnd.nextInt(10000)-1000,//the subtraction is to avoid repeated numbers
                snoozeIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent editPendingIntent = PendingIntent.getBroadcast(this,
                rnd.nextInt(100000)-10000,
                editIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent popupPendingIntent= PendingIntent.getBroadcast(this,
                rnd.nextInt(1000000)-100000,
                popupIntent, PendingIntent.FLAG_ONE_SHOT);

        String subtitle = String.format(getString(R.string.reminders_notif_track_subtitle),
                CustomRemindersHelper.removeTrailingZeros(reminder.value), reminder.unitName);
        int icon = QTools.getAppIconResId() == 0 ? R.drawable.ic_alarm_on_white_24dp : QTools.getAppIconResId();
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(reminder.name)
                .setContentText(subtitle)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setContentIntent(popupPendingIntent)
                .addAction(new NotificationCompat.Action(0,
                        getString(R.string.reminders_notif_button_track), trackPendingIntent))
                .addAction(new NotificationCompat.Action(0,
                        getString(R.string.reminders_notif_button_snooze), snoozePendingIntent))
                .addAction(new NotificationCompat.Action(0,
                        getString(R.string.reminders_notif_button_edit), editPendingIntent))
                .setColor(getResources().getColor(R.color.card_button))
                .build();

        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(specificId, notification);
    }
}
