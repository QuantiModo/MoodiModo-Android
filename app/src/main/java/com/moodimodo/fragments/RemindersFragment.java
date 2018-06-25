package com.moodimodo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.moodimodo.Global;
import com.moodimodo.R;
import com.moodimodo.receivers.MoodTimeReceiver;
import com.moodimodo.receivers.RemindersReceiver;
import com.moodimodo.things.Question;
import com.moodimodo.widgets.FixedMultiSelectListPreference;

import java.util.Set;

public class RemindersFragment extends PreferenceFragment {
    private static final String TAG = RemindersFragment.class.getSimpleName();
    private String mPrefNameMood;
    private String mPrefNameDiet;
    private String mPrefNameTreatment;
    private String mPrefNameSymptom;
    private String mPrefNamePhysical;

    private Context mContext;
    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.mPrefNameMood = getString(R.string.reminders_key_mood);
        this.mPrefNameDiet = getString(R.string.reminders_key_diet);
        this.mPrefNameTreatment = getString(R.string.reminders_key_treatment);
        this.mPrefNameSymptom = getString(R.string.reminders_key_symptom);
        this.mPrefNamePhysical = getString(R.string.reminders_key_physical);

        addPreferencesFromResource(R.xml.reminders);

        initDietReminder();
        initTreatmentReminder();
        initSymptomReminder();
        initRatingPreferences();
        initPhysicalReminder();
    }

    private void initRatingPreferences() {
        ListPreference listPreference = (ListPreference) findPreference("moodInterval");
        String currentValue = listPreference.getEntries()[Global.moodInterval].toString();
        listPreference.setSummary(currentValue);
        listPreference.setOnPreferenceChangeListener(onMoodIntervalChanged);

        Question[] questions = Question.getAllQuestions(mContext);
        CharSequence[] questionEntries = new CharSequence[questions.length - 1];
        CharSequence[] questionEntryValues = new CharSequence[questions.length - 1];

        // We're not listing "Mood", so we start at 1
        for(int i = 1; i< questions.length; i++) {
            Question question = questions[i];
            int startLocation = question.title.indexOf("{") + 1;
            int endLocation = question.title.indexOf("}", startLocation);
            questionEntries[i - 1] = question.title.substring(startLocation, endLocation);
            questionEntryValues[i - 1] = String.valueOf(question.variableName);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String[] selectionValues = prefs.getStringSet("requiredRatings", null).toArray(new String[]{});
        String summary = "";
        if(selectionValues.length > 0) {
            for (String selectionValue : selectionValues) {
                for (int n = 0; n < questionEntryValues.length; n++) {
                    CharSequence questionEntryValue = questionEntryValues[n];
                    if (questionEntryValue.equals(selectionValue)) {
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
    }

    private Preference.OnPreferenceChangeListener onMoodIntervalChanged = new Preference.OnPreferenceChangeListener() {
        @Override public boolean onPreferenceChange(Preference preference, Object o) {
            Global.moodInterval = Integer.valueOf((String) o);
            String currentValue = ((ListPreference) preference).getEntries()[Global.moodInterval].toString();
            preference.setSummary(currentValue);
            MoodTimeReceiver.setAlarm(preference.getContext(), Global.moodInterval);
            return true;
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
            if(selectionValues.length > 0) {
                for (String selectionValue : selectionValues) {
                    for (int n = 0; n < questionEntryValues.length; n++) {
                        CharSequence questionEntryValue = questionEntryValues[n];
                        if (questionEntryValue.equals(selectionValue)) {
                            CharSequence selectedQuestion = questionEntries[n];
                            summary = summary.concat(selectedQuestion + ", ");
                            break;
                        }
                    }
                }
                summary = summary.substring(0, summary.length() - 2);
            }
            else {
                summary = preference.getContext().getString(R.string.pref_rating_required_none);
            }
            preference.setSummary(summary);

            return true;
        }
    };
    private void initDietReminder(){
        ListPreference listPreference = (ListPreference)findPreference(mPrefNameDiet);
        setSummary(listPreference, mPrefNameDiet);

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final int selectedIndex = Integer.parseInt(newValue.toString());
                setSummary(preference, selectedIndex);
                setAlarm(mContext, selectedIndex, RemindersReceiver.RemindersType.DIET);
                return true;
            }
        });
    }

    private void initTreatmentReminder(){
        ListPreference listPreference = (ListPreference)findPreference(mPrefNameTreatment);
        setSummary(listPreference, mPrefNameTreatment);

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final int selectedIndex = Integer.parseInt(newValue.toString());
                setSummary(preference, selectedIndex);
                setAlarm(mContext, selectedIndex, RemindersReceiver.RemindersType.TREATMENT);
                return true;
            }
        });
    }

    private void initSymptomReminder(){
        ListPreference listPreference = (ListPreference)findPreference(mPrefNameSymptom);
        setSummary(listPreference, mPrefNameSymptom);

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final int selectedIndex = Integer.parseInt(newValue.toString());
                setSummary(preference, selectedIndex);
                setAlarm(mContext, selectedIndex, RemindersReceiver.RemindersType.SYMPTOM);
                return true;
            }
        });
    }

    private void initPhysicalReminder(){
        ListPreference listPreference = (ListPreference)findPreference(mPrefNamePhysical);
        setSummary(listPreference, mPrefNamePhysical);

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final int selectedIndex = Integer.parseInt(newValue.toString());
                setSummary(preference, selectedIndex);
                setAlarm(mContext, selectedIndex, RemindersReceiver.RemindersType.PHYSICAL);
                return true;
            }
        });
    }

    /**
     * Creates or sets an alarm for a specific reminder
     * @param context the current application context
     * @param indexOption the selected frecuency option
     * <ul
     * <li>0: Never (or cancel an active alarm)</li>
     * <li>1: Hourly</li>
     * <li>2: Every three hours</li>
     * <li>3: Twice a day</li>
     * <li>4: Daily</li>
     * </ul>
     * @param type the type of the reminder. See {@code RemindersReceiver.RemindersType}
     */
    public static void setAlarm(Context context, int indexOption, RemindersReceiver.RemindersType type){
        RemindersReceiver alarm = new RemindersReceiver();
        switch (indexOption){
            case 0://never
                alarm.cancelAlarm(context, type);
                break;
            case 1://hourly
                alarm.setAlarm(context, type, RemindersReceiver.FrecuencyType.HOURLY);
                break;
            case 2://every three hours
                alarm.setAlarm(context, type, RemindersReceiver.FrecuencyType.EVERY_THREE_HOURS);
                break;
            case 3://twice a day
                alarm.setAlarm(context, type, RemindersReceiver.FrecuencyType.TWICE_A_DAY);
                break;
            case 4://daily
                alarm.setAlarm(context, type, RemindersReceiver.FrecuencyType.DAILY);
                break;
        }
    }

    /**
     * Sets the preference summary given the key to get the value
     * @param preference the preference to set the summary on
     * @param keyName the key string to get the preference value
     */
    private void setSummary(final Preference preference, final String keyName){
        int savedValue = Integer.parseInt(mPrefs.getString(keyName, "0"));
        String[] array = mContext.getResources().getStringArray(R.array.mood_interval_entries);
        preference.setSummary(array[savedValue]);

    }

    /**
     * Sets the preference summary given the value selected
     * @param preference the preference to set the summary on
     * @param value the selected preference value
     */
    private void setSummary(final Preference preference, final int value){
        String[] array = mContext.getResources().getStringArray(R.array.mood_interval_entries);
        preference.setSummary(array[value]);

    }
}