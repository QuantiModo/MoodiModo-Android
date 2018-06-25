package com.moodimodo.databases;

import android.app.backup.FileBackupHelper;
import android.content.Context;

public class DatabaseBackupHelper extends FileBackupHelper
{
	public DatabaseBackupHelper(Context context, String databaseName)
	{
		super(context, context.getDatabasePath(databaseName).getAbsolutePath());
	}
}
