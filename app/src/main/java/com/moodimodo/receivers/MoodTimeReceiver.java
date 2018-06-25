package com.moodimodo.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.moodimodo.Global;
import com.moodimodo.Log;
import com.moodimodo.dialogs.MoodDialog;

public class MoodTimeReceiver extends BroadcastReceiver
{
	public static final String ACTION_SHOW_NOTIFICATION = "com.moodimodo.SHOW_MOOD_NOTIFICATION";
	public static final String ACTION_SHOW_POPUP = "com.moodimodo.SHOW_MOOD_POPUP";

	public static final int ALARM_REQUEST_CODE = 7331;
	public static final int ALARM_REMINDER_REQUEST_CODE = 3317;

	public static final int INTERVAL_MANUAL = 0;
	public static final int INTERVAL_HOURLY = 1;
	public static final int INTERVAL_EVERY_THREE_HOURS = 2;
	public static final int INTERVAL_TWICE_DAILY = 3;
	public static final int INTERVAL_DAILY = 4;
	public static final int INTERVAL_FIVE_MINUTES = 5;

	public static final float REMINDER_RATIO = 0.25f;           // An interval gets multiplied by this ratio to determine how long the user has to respond to the notification

	public static final long INTERVAL_HOURLY_MILLIS = 3600000;
	public static final long INTERVAL_EVERY_THREE_HOURS_MILLIS = 10800000;
	public static final long INTERVAL_TWICE_DAILY_MILLIS = 43200000;
	public static final long INTERVAL_DAILY_MILLIS = 86400000;
	public static final long INTERVAL_FIVE_MINUTES_MILLIS = 300000;


	@Override public void onReceive(Context context, Intent intent)
	{
		Log.i("TimeReceiver onReceive");

		if (intent.getAction().equals(ACTION_SHOW_NOTIFICATION))
		{
			Log.i("Start show notification");
			MoodDialog.showNotification(context);
		}
		else
		{
			Log.i("Start show dialog");

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(0);

			MoodDialog.getInstance().show(context.getApplicationContext());
		}
	}

	public static void setAlarm(Context context, int moodInterval)
	{
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent newIntent = new Intent(context, MoodTimeReceiver.class);
		newIntent.setAction(ACTION_SHOW_NOTIFICATION);
		PendingIntent operation = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		switch (moodInterval)
		{
		case INTERVAL_MANUAL:
			Log.i("Cancelling mood alarm, interval set to manual");
			manager.cancel(operation);
			break;
		case INTERVAL_HOURLY:
			Log.i("Set hourly mood alarm " + INTERVAL_HOURLY_MILLIS + " millis from now");
			manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + INTERVAL_HOURLY_MILLIS, INTERVAL_HOURLY_MILLIS, operation);
			break;
		case INTERVAL_EVERY_THREE_HOURS:
			Log.i("Set every three hours mood alarm " + INTERVAL_EVERY_THREE_HOURS_MILLIS + " millis from now");
			manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + INTERVAL_EVERY_THREE_HOURS_MILLIS, INTERVAL_EVERY_THREE_HOURS_MILLIS, operation);
			break;
		case INTERVAL_TWICE_DAILY:
			Log.i("Set twice daily mood alarm " + INTERVAL_TWICE_DAILY_MILLIS + " millis from now");
			manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + INTERVAL_TWICE_DAILY_MILLIS, INTERVAL_TWICE_DAILY_MILLIS, operation);
			break;
		case INTERVAL_DAILY:
			Log.i("Set daily mood alarm " + INTERVAL_DAILY_MILLIS + " millis from now");
			manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + INTERVAL_DAILY_MILLIS, INTERVAL_DAILY_MILLIS, operation);
			break;
		case INTERVAL_FIVE_MINUTES:
			Log.i("Set five minute mood alarm " + INTERVAL_FIVE_MINUTES_MILLIS + " millis from now");
			manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + INTERVAL_FIVE_MINUTES_MILLIS, INTERVAL_FIVE_MINUTES_MILLIS, operation);
			break;
		}
	}

	public static void setReminderAlarm(Context context)
	{
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent newIntent = new Intent(context, MoodTimeReceiver.class);
		newIntent.setAction(ACTION_SHOW_POPUP);
		PendingIntent operation = PendingIntent.getBroadcast(context, ALARM_REMINDER_REQUEST_CODE, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Global.moodInterval = Integer.valueOf(prefs.getString("moodInterval", "1"));

		switch (Global.moodInterval)
		{
		case INTERVAL_HOURLY:
			Log.i("Set reminder hourly mood alarm " + INTERVAL_HOURLY_MILLIS * REMINDER_RATIO + " millis from now");
			manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (long) (INTERVAL_HOURLY_MILLIS * REMINDER_RATIO), operation);
			break;
		case INTERVAL_EVERY_THREE_HOURS:
			Log.i("Set reminder every three hours  mood alarm " + INTERVAL_EVERY_THREE_HOURS_MILLIS * REMINDER_RATIO + " millis from now");
			manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (long) (INTERVAL_EVERY_THREE_HOURS_MILLIS * REMINDER_RATIO), operation);
			break;
		case INTERVAL_TWICE_DAILY:
			Log.i("Set remindertwice daily mood alarm " + INTERVAL_TWICE_DAILY_MILLIS * REMINDER_RATIO + " millis from now");
			manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (long) (INTERVAL_TWICE_DAILY_MILLIS * REMINDER_RATIO), operation);
			break;
		case INTERVAL_DAILY:
			Log.i("Set reminder daily mood alarm " + INTERVAL_DAILY_MILLIS * REMINDER_RATIO + " millis from now");
			manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (long) (INTERVAL_DAILY_MILLIS * REMINDER_RATIO), operation);
			break;
		case INTERVAL_FIVE_MINUTES:
			Log.i("Set reminder five minutes mood alarm " + INTERVAL_FIVE_MINUTES_MILLIS * REMINDER_RATIO + " millis from now");
			manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (long) (INTERVAL_FIVE_MINUTES_MILLIS * REMINDER_RATIO), operation);
			break;
		}
	}

	public static void cancelReminderAlarm(Context context)
	{
		Log.i("Cancel reminder");
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent newIntent = new Intent(context, MoodTimeReceiver.class);
		newIntent.setAction(ACTION_SHOW_POPUP);
		PendingIntent operation = PendingIntent.getBroadcast(context, ALARM_REMINDER_REQUEST_CODE, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		manager.cancel(operation);
	}
}
