package com.moodimodo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.fragments.RemindersFragment;
import com.quantimodo.tools.sync.SyncHelper;
import com.quantimodo.tools.utils.CustomRemindersHelper;

public class BootCompletedReceiver extends BroadcastReceiver
{
	@Override public void onReceive(Context context, Intent intent)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Global.moodInterval = Integer.valueOf(prefs.getString("moodInterval", "1"));

		MoodTimeReceiver.setAlarm(context, Global.moodInterval);

		if (SyncHelper.isSync(context)){
			SyncHelper.scheduleSync(context);
		}

		final String prefNameMood = context.getString(R.string.reminders_key_mood);
		final String prefNameDiet = context.getString(R.string.reminders_key_diet);
		final String prefNameTreatment = context.getString(R.string.reminders_key_treatment);
		final String prefNameSymptom = context.getString(R.string.reminders_key_symptom);
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);


		//Loads the saved preference for every type of reminder and set the corresponding alarm
		int indexMood = Integer.parseInt(sharedPref.getString(prefNameMood, "0"));
		int indexDiet = Integer.parseInt(sharedPref.getString(prefNameDiet, "0"));
		int indexTreatment = Integer.parseInt(sharedPref.getString(prefNameTreatment, "0"));
		int indexSymptom = Integer.parseInt(sharedPref.getString(prefNameSymptom, "0"));

		RemindersFragment.setAlarm(context, indexMood, RemindersReceiver.RemindersType.MOOD);
		RemindersFragment.setAlarm(context, indexDiet, RemindersReceiver.RemindersType.DIET);
		RemindersFragment.setAlarm(context, indexTreatment, RemindersReceiver.RemindersType.TREATMENT);
		RemindersFragment.setAlarm(context, indexSymptom, RemindersReceiver.RemindersType.SYMPTOM);

		//Loads custom reminders
		for(CustomRemindersHelper.Reminder reminder : CustomRemindersHelper.getRemindersList(context)) {
			CustomRemindersHelper.startAlarms(context, reminder.id);
		}
	}
}
