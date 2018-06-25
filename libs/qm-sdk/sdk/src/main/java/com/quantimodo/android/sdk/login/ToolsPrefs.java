package com.quantimodo.android.sdk.login;

import com.quantimodo.android.sdk.BuildConfig;

import java.util.Arrays;

/**
 * Configuration of toolprefs and quantimodo app
 */
public class ToolsPrefs {

    public static final String DEBUG_TAG = "QuantimodoSDK";
//    public static final String QUANTIMODO_PREF_KEY = "syncPrefs";
//    public static final String PREF_SYNC_FROM = "loadDataFrom";
//    public static final String PREF_SYNC_ENABLED = "enableSync";
//    public static final String PREF_LAST_SYNC_TIMESTAMP = "lastSuccessfulSync";
//    public static final String PREF_SHOW_CORRELATION_CONFIRM = "correlationConfirm";
//    public static final String FOOTER = "D_FOOTER";
    public static final String[] QM_SCOPES_ARRAY = {"basic","writemeasurements","readmeasurements"};
    private static final String QUANTIMODO_SCOPES = createScopeString(QM_SCOPES_ARRAY);

    private static final String mApiUrl = BuildConfig.APP_BASE_PATH;
    private static final String mApiSocialAuth = mApiUrl + "api/v2/auth/social/authorizeToken";
    private static ToolsPrefs instance = new ToolsPrefs();
    private boolean isInitialized = false;

    public static ToolsPrefs getInstance() {
        return instance;
    }

    public void initialize() {
        this.isInitialized = true;
    }

    private ToolsPrefs(){

    }

    private void checkInitialized(){
        if(!isInitialized){
            throw new RuntimeException("You need to call ToolsPrefs.initialize() first.");
        }
    }
    public String getApiScopes() {
        checkInitialized();
        return QUANTIMODO_SCOPES;
    }

    public String getApiUrl() {
        checkInitialized();
        return mApiUrl;
    }

    public String getApiSocialAuth(){
        checkInitialized();
        return mApiSocialAuth;
    }

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
}
