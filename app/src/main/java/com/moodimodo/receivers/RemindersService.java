package com.moodimodo.receivers;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.moodimodo.R;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class RemindersService extends IntentService {
    private static final String TAG = RemindersService.class.getSimpleName();

    public RemindersService() {
        super("RemindersService");
    }

    public static final int NOTIFICATION_ID_MOOD = 23109614;
    public static final int NOTIFICATION_ID_DIET = 43810239;
    public static final int NOTIFICATION_ID_TREATMENT = 58628804;
    public static final int NOTIFICATION_ID_SYMPTOM = 39856306;
    public static final int NOTIFICATION_ID_PHYSICAL = 656437521;
    private NotificationManager mNotificationManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.hasExtra(RemindersReceiver.EXTRA_TYPE)){
            int typeIndex = intent.getExtras().getInt(
                    RemindersReceiver.EXTRA_TYPE, RemindersReceiver.RemindersType.DIET.ordinal());
            Log.d(TAG, "onHandleIntent, typeIndex: " + typeIndex);
            RemindersReceiver.RemindersType type = RemindersReceiver.RemindersType.values()[typeIndex];
            switch (type){
                case MOOD:
                    sendNotification(
                            NOTIFICATION_ID_MOOD, getString(R.string.reminders_notif_title_mood));
                    break;
                case DIET:
                    sendNotification(
                            NOTIFICATION_ID_DIET, getString(R.string.reminders_notif_title_diet));
                    break;
                case TREATMENT:
                    sendNotification(
                            NOTIFICATION_ID_TREATMENT, getString(R.string.reminders_notif_title_treatments));
                    break;
                case SYMPTOM:
                    sendNotification(
                            NOTIFICATION_ID_SYMPTOM, getString(R.string.reminders_notif_title_symptoms));
                    break;
                case PHYSICAL:
                    sendNotification(
                            NOTIFICATION_ID_PHYSICAL, getString(R.string.reminders_notif_title_physical));
                    break;
            }
        }
        else
            Log.d(TAG, "onHandleIntent has no extras");
        // Release the wake lock provided by the BroadcastReceiver.
        RemindersReceiver.completeWakefulIntent(intent);
    }

    /**
     * Popup the notification and prepares de action over notifications
     * @param notificationId the notification id to setup
     * @param title the title to put on the notification
     */
    private void sendNotification(int notificationId, String title) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent snoozeIntent = new Intent(this, RemindersReceiver.class);
        Intent trackIntent = new Intent(this, RemindersReceiver.class);
        Intent skipIntent = new Intent(this, RemindersReceiver.class);

        RemindersReceiver.RemindersType type = RemindersReceiver.RemindersType.MOOD;
        if(notificationId == NOTIFICATION_ID_MOOD){
            type = RemindersReceiver.RemindersType.MOOD;
        } else if(notificationId == NOTIFICATION_ID_DIET){
            type = RemindersReceiver.RemindersType.DIET;
        } else if(notificationId == NOTIFICATION_ID_TREATMENT){
            type = RemindersReceiver.RemindersType.TREATMENT;
        } else if(notificationId == NOTIFICATION_ID_SYMPTOM){
            type = RemindersReceiver.RemindersType.SYMPTOM;
        } else if(notificationId == NOTIFICATION_ID_PHYSICAL){
            type = RemindersReceiver.RemindersType.PHYSICAL;
        }

        snoozeIntent.putExtra(RemindersReceiver.EXTRA_NOTIFICATION_ID, notificationId);
        snoozeIntent.putExtra(RemindersReceiver.EXTRA_REQUEST_SNOOZE, true);
        snoozeIntent.putExtra(RemindersReceiver.EXTRA_TYPE, type.ordinal());
        trackIntent.putExtra(RemindersReceiver.EXTRA_NOTIFICATION_ID, notificationId);
        trackIntent.putExtra(RemindersReceiver.EXTRA_REQUEST_REMINDER, true);
        trackIntent.putExtra(RemindersReceiver.EXTRA_TYPE, type.ordinal());
        skipIntent.putExtra(RemindersReceiver.EXTRA_NOTIFICATION_ID, notificationId);
        skipIntent.putExtra(RemindersReceiver.EXTRA_REQUEST_SKIP, true);

        PendingIntent trackPendingIntent = PendingIntent.getBroadcast(this,
                RemindersReceiver.NotificationType.TRACK.ordinal() + notificationId,
                trackIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this,
                RemindersReceiver.NotificationType.SNOOZE.ordinal() + notificationId,
                snoozeIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent skipPendingIntent = PendingIntent.getBroadcast(this,
                RemindersReceiver.NotificationType.SKIP.ordinal() + notificationId,
                skipIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(getString(R.string.reminders_notif_subtitle))
                .setSmallIcon(R.drawable.ic_action_appicon)
                .setAutoCancel(true)
                .setContentIntent(trackPendingIntent)
                .addAction(new NotificationCompat.Action(0,
                        getString(R.string.reminders_notif_button_track), trackPendingIntent))
                .addAction(new NotificationCompat.Action(0,
                        getString(R.string.reminders_notif_button_snooze), snoozePendingIntent))
                .addAction(new NotificationCompat.Action(0,
                        getString(R.string.reminders_notif_button_skip), skipPendingIntent))
                .setColor(getResources().getColor(R.color.card_button))
                .build();

        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(notificationId, notification);
    }
}
