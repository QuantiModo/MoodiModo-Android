package com.moodimodo.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.*;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;
import com.moodimodo.Global;
import com.moodimodo.Log;
import com.moodimodo.R;
import com.moodimodo.receivers.MoodTimeReceiver;
import com.moodimodo.things.Question;
import com.moodimodo.widgets.FixedMultiSelectListPreference;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.UserPreferences;
import com.quantimodo.tools.activities.QuantimodoLoginActivity;
import com.quantimodo.tools.events.SyncStarted;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;
import com.quantimodo.tools.sync.SyncHelper;
import com.quantimodo.tools.sync.SyncService;
import com.uservoice.uservoicesdk.UserVoice;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity
{

	public static final String MOOD_NOTIFICATION_ENABLED_KEY = "moodNotificationEnabled";
	public static final String MOOD_POPUP_ENABLED_KEY = "moodPopupEnabled";
	public static final String ADD_NOTE_AFTER_MOOD_KEY = "addNoteAfterRating";

	@Inject
	AuthHelper authHelper;

	@Inject
	DaoSession mDaoSession;

	@Inject
	QuantimodoApiV2 quantimodoApiV2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		QTools.getInstance().inject(this);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		addPreferencesFromResource(R.xml.settings);

		initRatingPreferences();
		initQuantimodoPreferences();
		initDataPreferences();
		initFeedbackPreferences();
	}

	public void onEvent(SyncStarted event){
		Toast.makeText(this, R.string.toast_sync_data, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		QTools.getInstance().register(this);
		Global.init(this,mDaoSession);
		initQuantimodoPreferences();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		Global.init(this,mDaoSession);
		QTools.getInstance().unregister(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			NavUtils.navigateUpTo(this, intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initRatingPreferences()
	{
		ListPreference listPreference = (ListPreference) findPreference("moodInterval");
		String currentValue = listPreference.getEntries()[Global.moodInterval].toString();
		listPreference.setSummary(currentValue);
		listPreference.setOnPreferenceChangeListener(onMoodIntervalChanged);

		Question[] questions = Question.getAllQuestions(this);
		CharSequence[] questionEntries = new CharSequence[questions.length - 1];
		CharSequence[] questionEntryValues = new CharSequence[questions.length - 1];

		// We're not listing "Mood", so we start at 1
		for(int i = 1; i< questions.length; i++)
		{
			Question question = questions[i];

			int startLocation = question.title.indexOf("{") + 1;
			int endLocation = question.title.indexOf("}", startLocation);

			questionEntries[i - 1] = question.title.substring(startLocation, endLocation);
			questionEntryValues[i - 1] = String.valueOf(question.variableName);
		}

		//
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String[] selectionValues = prefs.getStringSet("requiredRatings", null).toArray(new String[]{});
		String summary = "";
		if(selectionValues.length > 0)
		{
			for (String selectionValue : selectionValues)
			{
				for (int n = 0; n < questionEntryValues.length; n++)
				{
					CharSequence questionEntryValue = questionEntryValues[n];
					if (questionEntryValue.equals(selectionValue))
					{
						CharSequence selectedQuestion = questionEntries[n];
						summary = summary.concat(selectedQuestion + ", ");
						break;
					}
				}
			}

		}

		if (summary.isEmpty()){
			summary = getString(R.string.pref_rating_required_none);
		} else {
			summary = summary.substring(0, summary.length() - 2);
		}

		FixedMultiSelectListPreference multiListPreference = (FixedMultiSelectListPreference) findPreference("requiredRatings");
		multiListPreference.setEntries(questionEntries);
		multiListPreference.setEntryValues(questionEntryValues);
		multiListPreference.setSummary(summary);
		multiListPreference.setOnPreferenceChangeListener(onRequiredRatingsChangedListener);
		//multiListPreference.set

		/*CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("moodNotificationEnabled");
			checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
			{
				@Override public boolean onPreferenceClick(Preference preference)
				{
					return true;
				}
			});
			checkBoxPreference =  (CheckBoxPreference) findPreference("moodPopupEnabled");
			checkBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
			{
				@Override public boolean onPreferenceClick(Preference preference)
				{
					return true;
				}
			});*/
	}

	private void initFeedbackPreferences(){
		Preference preference = findPreference("prefUserVoiceHelp");
		preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				UserVoice.launchUserVoice(SettingsActivity.this);
				return true;
			}
		});

		preference = findPreference("prefUserVoiceFeedback");
		preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				UserVoice.launchForum(SettingsActivity.this);
				return true;
			}
		});

		preference = findPreference("prefUserVoiceContact");
		preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				UserVoice.launchContactUs(SettingsActivity.this);
				return true;
			}
		});

		preference = findPreference("prefUserVoiceIdea");
		preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				UserVoice.launchPostIdea(SettingsActivity.this);
				return true;
			}
		});
	}

	private void initQuantimodoPreferences()
	{
	    boolean syncAutomatically = SyncHelper.isSync(this);

		CheckBoxPreference syncMoodPreference = (CheckBoxPreference) findPreference("syncMoods");
		syncMoodPreference.setOnPreferenceChangeListener(onSyncMoodsChanged);
		syncMoodPreference.setChecked(syncAutomatically);
		if(syncAutomatically)
		{
			SharedPreferences prefs = getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_PRIVATE);
			long lastSuccessfulMoodSync = prefs.getLong("lastSuccessfulMoodSync", -1);

			Log.i("LastSuccessfulSync: " + lastSuccessfulMoodSync);

			if(lastSuccessfulMoodSync != -1)
			{
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				String formattedDate = simpleDateFormat.format(new Date(lastSuccessfulMoodSync));
				Resources res = getResources();
				String text = String.format(res.getString(R.string.pref_quantimodo_syncmoods_enabled), formattedDate);
				syncMoodPreference.setSummary(text);
			}
			else
			{
				syncMoodPreference.setSummary(R.string.pref_quantimodo_syncmoods_neversynced);
			}
		}
		else
		{
			syncMoodPreference.setSummary(R.string.pref_quantimodo_syncmoods_disabled);
		}

		Preference preference = findPreference("linkQuantimodoAccount");
		preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
		{
			@Override public boolean onPreferenceClick(Preference preference)
			{
				if (!authHelper.isLoggedIn()){

					Intent auth = new Intent(SettingsActivity.this,QuantimodoLoginActivity.class);
					auth.putExtra(QuantimodoLoginActivity.KEY_APP_NAME, getString(R.string.app_name));
					startActivity(auth);
				}  else {
					new AlertDialog.Builder(SettingsActivity.this)
							.setMessage(R.string.auth_logout)
							.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									authHelper.logOut();
									initQuantimodoPreferences();
									dialog.dismiss();
									new AlertDialog.Builder(SettingsActivity.this)
											.setMessage(R.string.delete_data_question)
											.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
                                                    mDaoSession.getMeasurementDao().deleteAll();
													initQuantimodoPreferences();
													resetLastSyncTime();
													dialog.dismiss();
												}
											})
											.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
												}
											}).create().show();
								}
							})
							.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).create().show();
				}
				return true;
			}
		});

		if(authHelper.isLoggedIn()) {
			final String userName = UserPreferences.getUserName(this);
			if(userName == null)
				preference.setSummary("Logged in");
			else
				preference.setSummary(userName);

			syncMoodPreference.setEnabled(true);
		} else {
			preference.setSummary("Not logged in");
		}
	}

	private void resetLastSyncTime(){
		SharedPreferences mSharePrefs = getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_PRIVATE);
		mSharePrefs.edit().putLong(Global.PREF_SYNC_FROM, 1).apply();
	}

	private void initDataPreferences()
	{
		Preference preference = findPreference("exportData");
		if (preference != null)
		{
			preference.setOnPreferenceClickListener(onExportDataPreferenceClicked);
		}
	}

	private Preference.OnPreferenceClickListener onExportDataPreferenceClicked = new Preference.OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			final Context context = preference.getContext();

			final ProgressDialog dialog = new ProgressDialog(context);
			dialog.setMessage(context.getString(R.string.pref_data_exporting));
			dialog.show();

            Integer value = null;
            try {
                value = quantimodoApiV2.requestCSV(context, authHelper.getAuthTokenWithRefresh()).getData();
                android.util.Log.d("SettingsActivity", "requestCSV, response: " + value);
            } catch (NoNetworkConnection noNetworkConnection) {
                Toast.makeText(context, "You need an internet connection to continue", Toast.LENGTH_LONG).show();
            }
            if(value != null && value > 0)
                Toast.makeText(context, getString(R.string.request_csv_message), Toast.LENGTH_LONG).show();

            dialog.dismiss();

			return false;
		}
	};

	private Preference.OnPreferenceChangeListener onRequiredRatingsChangedListener = new Preference.OnPreferenceChangeListener()
	{
		@Override public boolean onPreferenceChange(Preference preference, Object o)
		{
			FixedMultiSelectListPreference multiSelectPreference = (FixedMultiSelectListPreference) preference;

			CharSequence[] questionEntries = multiSelectPreference.getEntries();
			CharSequence[] questionEntryValues = multiSelectPreference.getEntryValues();
			String[] selectionValues = ((Set<String>) o).toArray(new String[]{});

			String summary = "";
			if(selectionValues.length > 0)
			{
				for (String selectionValue : selectionValues)
				{
					for (int n = 0; n < questionEntryValues.length; n++)
					{
						CharSequence questionEntryValue = questionEntryValues[n];
						if (questionEntryValue.equals(selectionValue))
						{
							CharSequence selectedQuestion = questionEntries[n];
							summary = summary.concat(selectedQuestion + ", ");
							break;
						}
					}
				}
				summary = summary.substring(0, summary.length() - 2);
			}
			else
			{
				summary = preference.getContext().getString(R.string.pref_rating_required_none);
			}

			preference.setSummary(summary);

			return true;
		}
	};

	private Preference.OnPreferenceChangeListener onMoodIntervalChanged = new Preference.OnPreferenceChangeListener()
	{
		@Override public boolean onPreferenceChange(Preference preference, Object o)
		{
			Global.moodInterval = Integer.valueOf((String) o);

			String currentValue = ((ListPreference) preference).getEntries()[Global.moodInterval].toString();
			preference.setSummary(currentValue);

			MoodTimeReceiver.setAlarm(preference.getContext(), Global.moodInterval);

			return true;
		}
	};

	private Preference.OnPreferenceChangeListener onSyncMoodsChanged = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(final Preference preference, Object o)
		{
			final boolean syncEnabled = (Boolean) o;
			if(syncEnabled)
			{
				if (authHelper.isLoggedIn()){
					SyncHelper.scheduleSync(SettingsActivity.this);

					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
					long lastSuccessfulTotalUsageSync = prefs.getLong("lastSuccessfulTotalUsageSync", -1);

					if (lastSuccessfulTotalUsageSync != -1)
					{
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						String formattedDate = simpleDateFormat.format(new Date(lastSuccessfulTotalUsageSync));
						Resources res = preference.getContext().getResources();
						String text = String.format(res.getString(R.string.pref_quantimodo_syncmoods_enabled), formattedDate);
						preference.setSummary(text);
					}
					else
					{
						preference.setSummary(R.string.pref_quantimodo_syncmoods_neversynced);
					}
				} else {
					Toast.makeText(preference.getContext(), "Authorization failed", Toast.LENGTH_SHORT).show();
					((CheckBoxPreference) preference).setChecked(false);
				}
			}
			else
			{
				preference.setSummary(R.string.pref_quantimodo_syncmoods_disabled);
				SyncHelper.unscheduleSync(SettingsActivity.this);
				return true;
			}
			return true;
		}
	};
}
