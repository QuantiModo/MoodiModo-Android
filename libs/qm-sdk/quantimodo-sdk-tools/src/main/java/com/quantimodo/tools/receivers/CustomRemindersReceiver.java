package com.quantimodo.tools.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.quantimodo.android.sdk.model.Measurement;
import com.quantimodo.android.sdk.model.MeasurementSet;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.dialogs.CustomReminderDialog;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.DefaultSdkResponseListener;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;
import com.quantimodo.tools.sdk.request.SendMeasurementsRequest;
import com.quantimodo.tools.utils.CustomRemindersHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * Broadcast receiver that gets the triggered alarms and display a popup to track custom measurements
 */
public class CustomRemindersReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = CustomRemindersReceiver.class.getSimpleName();
    public static final String EXTRA_REQUEST_ALARM = "extra_request_alarm";
    public static final String EXTRA_REQUEST_REMINDER = "extra_request_reminder";
    public static final String EXTRA_REQUEST_SNOOZE = "extra_request_snooze";
    public static final String EXTRA_REQUEST_EDIT = "extra_request_edit";
    public static final String EXTRA_REQUEST_POPUP = "extra_request_popup";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    public static final String EXTRA_ACTION = "extra_action";

    @Inject
    ToolsPrefs mPrefs;

    @Inject
    AuthHelper authHelper;

    @Override
    public void onReceive(final Context context, Intent intent) {
        QTools.getInstance().inject(this);
        Bundle extras = intent.getExtras();
        final int specificId = extras.getInt(CustomRemindersHelper.EXTRA_SPECIFIC_ID);
        final CustomRemindersHelper.Reminder reminder = CustomRemindersHelper.getReminder(
                context, extras.getString(CustomRemindersHelper.EXTRA_REMINDER_ID, ""));
        if(reminder == null){
            Log.d(TAG, "Reminder loaded null!, there is nothing to do");
            return;
        }
        if(intent.hasExtra(EXTRA_REQUEST_ALARM)) {
            Intent service = new Intent(context, RemindersService.class);
            //shows the popup dialog
//            CustomReminderDialog.getInstance().show(context, reminder.id);
            service.putExtra(CustomRemindersHelper.EXTRA_REMINDER_ID, reminder.id);
            service.putExtra(CustomRemindersHelper.EXTRA_SPECIFIC_ID, specificId);
            service.putExtra(EXTRA_ACTION,RemindersService.ACTION_NOTIFICATION);
            // Start the service, keeping the device awake while it is launching.
            //this service shows the notification and set the events for buttons
            startWakefulService(context, service);
        }
        else if(intent.hasExtra(EXTRA_REQUEST_REMINDER)){
            cancelNotification(context, extras.getInt(EXTRA_NOTIFICATION_ID, 0));

            Intent service = new Intent(context, RemindersService.class);
            service.putExtra(CustomRemindersHelper.EXTRA_REMINDER_ID, reminder.id);
            service.putExtra(CustomRemindersHelper.EXTRA_SPECIFIC_ID, specificId);
            service.putExtra(EXTRA_ACTION,RemindersService.ACTION_SEND_VALUE);
            startWakefulService(context,service);
        }
        else if(intent.hasExtra(EXTRA_REQUEST_SNOOZE)){
            cancelNotification(context, extras.getInt(EXTRA_NOTIFICATION_ID, 0));
            CustomRemindersHelper.setAlarm(context, reminder.id, CustomRemindersHelper.FrequencyType.SNOOZE);
            try {
                CustomRemindersHelper.postRemoteSnooze(reminder.remoteId, authHelper.getAuthTokenWithRefresh());
            } catch (NoNetworkConnection noNetworkConnection) {
                noNetworkConnection.printStackTrace();
            }
        }
        else if(intent.hasExtra(EXTRA_REQUEST_EDIT)){
            cancelNotification(context, extras.getInt(EXTRA_NOTIFICATION_ID, 0));
            openEditActivity(context, reminder.name);
        }
        else if(intent.hasExtra(EXTRA_REQUEST_POPUP)){
            cancelNotification(context, extras.getInt(EXTRA_NOTIFICATION_ID, 0));
            CustomReminderDialog.getInstance().show(context, reminder.id);
        }
    }

    private void openEditActivity(Context context, String varName){
        Intent trackIntent = new Intent(context,
                CustomRemindersHelper.getInstance().getRegisteredActivity());
        trackIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
        trackIntent.putExtra(CustomRemindersHelper.EXTRA_VARIABLE_NAME, varName);
        context.startActivity(trackIntent);
    }

    private void cancelNotification(Context context, int notifId){
        NotificationManager notifManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notifManager.cancel(notifId);
        //the same time when closing the notification we close the popup if any.
        if(CustomReminderDialog.getInstance().isShowing())
            CustomReminderDialog.getInstance().dismiss(context);
    }
}
