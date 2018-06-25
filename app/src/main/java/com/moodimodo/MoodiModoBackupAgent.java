package com.moodimodo;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import com.moodimodo.databases.DatabaseBackupHelper;

public class MoodiModoBackupAgent extends BackupAgentHelper
{
	static final String DATABASES_KEY = "MoodDatabase";
	static final String PREFS_KEY = "Preferences";

	@Override
	public void onCreate()
	{
		DatabaseBackupHelper dbHelper = new DatabaseBackupHelper(this, Global.DATABASE_NAME);
		addHelper(DATABASES_KEY, dbHelper);

		SharedPreferencesBackupHelper spHelper = new SharedPreferencesBackupHelper(this, Global.QUANTIMODO_PREF_KEY);
		addHelper(PREFS_KEY, spHelper);
	}
}