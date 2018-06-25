package com.moodimodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.models.Measurement;
import com.quantimodo.tools.models.MeasurementDao;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Global
{
	public static final String BUGSENSE_KEY = "3a3d937d";

	public static final String[] QM_SCOPES_ARRAY = {"basic","writemeasurements","readmeasurements"};
	public static final String QUANTIMODO_SCOPES = createScopeString(QM_SCOPES_ARRAY);
	public static final String QUANTIMODO_PREF_KEY = "com.moodimodo_preferences";
	public static final String PREF_SYNC_FROM = "loadDataFrom";
	public static final String PREF_SYNC_ENABLED = "enableSync";
	public static final String PREF_NOTES_ENABLED = "addNoteAfterRating";
	public static final String PREF_DOUBLE_TAP_CLOSE = "doubleTapClose";
	public static final String PREF_ASK_MORE_QUESTIONS = "askMoreQuestions";
	public static final String QUANTIMODO_SOURCE_NAME = BuildConfig.APPLICATION_SOURCE;
	public static final String DEFAULT_UNIT = "/5";

	public static final String DATABASE_NAME = "db";

	public static final String DEFAULT_DATE_FORMAT = "MMM dd yyyy";
	public static final String DEFAULT_DATE_FORMAT_WO_YEAR = "MMM dd";
	public static final SimpleDateFormat DEFAULT_MOOD_DATE_FORMATTER = new SimpleDateFormat(Global.DEFAULT_DATE_FORMAT + " HH:mm");

	public static final String QM_ADDRESS = BuildConfig.API_HOST;
	public static final String QM_AUTH_SOCIAL_URL = BuildConfig.API_HOST + "api/v2/auth/social/authorizeToken";

	public static Date moodChartStart;
	public static Date moodChartEnd;
	public static Date rangeStart;
	public static Date rangeEnd;
	public static Date rangeEndRequested;

	// Preferences
	public static int moodInterval;

	// Flags
	public static boolean welcomeCompleted;


	public static String createScopeString(String[] scopes) {
		Arrays.sort(scopes);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < scopes.length; i++) {
			sb.append(scopes[i]);
			if (i != scopes.length - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}


	public static void init(Context context,DaoSession helper)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		Global.moodInterval = Integer.valueOf(prefs.getString("moodInterval", "2"));
		Global.welcomeCompleted = prefs.getBoolean("welcomeCompleted", false);

		final Calendar cal = Calendar.getInstance();
		Measurement m = helper.getMeasurementDao().queryBuilder().orderAsc(MeasurementDao.Properties.Timestamp).limit(1).unique();
		rangeStart = m != null ? m.getTimestamp() : new Date();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		rangeEnd = rangeEndRequested = cal.getTime();
		if(moodChartStart == null) moodChartStart = rangeStart;
		if(moodChartEnd == null) moodChartEnd = rangeEnd;
	}

	public static void updateDates(Context context, DaoSession helper){
		final Calendar cal = Calendar.getInstance();
//		rangeStart = helper.getFirstMoodReportDate();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);

		rangeEnd = rangeEndRequested = cal.getTime();
	}
}