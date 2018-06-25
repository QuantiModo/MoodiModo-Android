package com.quantimodo.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.quantimodo.android.sdk.model.QuantimodoUser;

/**
 * Class to manage the user data like name, id, and others
 */
public class UserPreferences {
    private static final String PREFERENCES_NAME = "QMPreferencesId";
    private static final String PREF_USER_NAME = "userDisplayName";
    private static final String PREF_USER_ID= "userId";
    private static final String PREF_USER_EMAIL= "userEmail";

    private static SharedPreferences getPrefs(final Context context){
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void setFullUserdata(final Context context, final QuantimodoUser user){
        setUserName(context, user.getDisplayName());
        setUserId(context, user.getId());
        setUserEmail(context, user.getEmail());
    }

    public static String getUserName(final Context context){
        return getPrefs(context).getString(PREF_USER_NAME, null);
    }

    public static void setUserName(final Context context, final String userName){
        getPrefs(context).edit().putString(PREF_USER_NAME, userName).apply();
    }

    public static int getUserId(final Context context){
        return getPrefs(context).getInt(PREF_USER_ID, 0);
    }

    public static void setUserId(final Context context, final int userId){
        getPrefs(context).edit().putInt(PREF_USER_ID, userId).apply();
    }

    public static String getUserEmail(final Context context){
        return getPrefs(context).getString(PREF_USER_EMAIL, null);
    }

    public static void setUserEmail(final Context context, final String userEmail){
        getPrefs(context).edit().putString(PREF_USER_EMAIL, userEmail).apply();
    }
}
