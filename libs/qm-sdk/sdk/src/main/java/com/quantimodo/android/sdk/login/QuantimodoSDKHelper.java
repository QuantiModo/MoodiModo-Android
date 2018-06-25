package com.quantimodo.android.sdk.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Class that encapsulate work with tokens and auth
 * Tokens stored in private shared preferences
 * This class should be use as Singleton, in most cases it would be injected as dependency
 */
public class QuantimodoSDKHelper {

    public static final String AUTH_PREF = "authPref";
    public static final String PREF_KEY = "key";
    public static final String PREF_EXPIRE = "expire_in";
    public static final String PREF_REFRESH = "refresh";

    private Context mCtx;
    private ToolsPrefs mToolsPrefs;

    private String mClientId;
    private String mClientSecret;
    private SharedPreferences mPrefs;
    private String mToken;
    private boolean isInitialized = false;
    private String mApplicationSource;

    private AuthListener mListener;

    private final Object refreshLock = new Object();


    public interface AuthListener{
        void onSetAuthToken();
        void onLogOut();
    }

    protected Context getCtx() {
        return mCtx;
    }

    private static QuantimodoSDKHelper instance = new QuantimodoSDKHelper();

    public static QuantimodoSDKHelper getInstance() {
        return instance;
    }

    public void initialize(Context context, String applicationSource) {
        isInitialized = true;
        mCtx = context.getApplicationContext();
        mToolsPrefs = ToolsPrefs.getInstance();
        mPrefs = mCtx.getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
        mToken = mPrefs.getString(PREF_KEY, null);
        this.mApplicationSource = applicationSource;
        mClientId = null;
        mClientSecret = null;
//        QTools.getInstance().postStickyEvent(new TokenEvent(false));
    }

    public void initialize(Context context, String applicationSource, String clientId, String clientSecret) {
        isInitialized = true;
        mCtx = context.getApplicationContext();
        mToolsPrefs = ToolsPrefs.getInstance();
        mPrefs = mCtx.getSharedPreferences(AUTH_PREF, Context.MODE_PRIVATE);
        mToken = mPrefs.getString(PREF_KEY, null);
        this.mApplicationSource = applicationSource;
        mClientId = clientId;
        mClientSecret = clientSecret;
    }

    /**
     * Makes request to QuantiModo webservice
     * @return new AuthToken object
     * @throws NoNetworkConnection thrown when refresh failed because of connectivity problems
     * @throws IllegalStateException thrown when the AuthHelper is not initialized or
     * when client id or secret are missing
     */
    public AuthToken refreshToken() throws NoNetworkConnection {
        AuthToken authToken = readAuthToken();
        if(!isInitialized){
            throw new IllegalStateException("AuthHelper is not initialized, " +
                    "please call AuthHelper.initialize()");
        }
        if(TextUtils.isEmpty(mClientId)){
            throw new IllegalStateException("Client id is missing");
        }
        if(TextUtils.isEmpty(mClientSecret)){
            throw new IllegalStateException("Client secret is missing");
        }
        try {
            JsonObject tokenResult = Ion.with(mCtx, mToolsPrefs.getApiUrl() + "api/oauth2/token")
                    .setTimeout(35000)
                    .setBodyParameter("client_id", mClientId)
                    .setBodyParameter("client_secret", mClientSecret)
                    .setBodyParameter("grant_type", "refresh_token")
                    .setBodyParameter("refresh_token", authToken.refreshToken)
                    .asJsonObject()
                    .get();

            String accessToken = null;
            String refreshToken = authToken.refreshToken;
            int expiresIn = 0;
            if (tokenResult.has("error")) {
//                QTools.getInstance().postEvent(new NoAuthEvent());
                mPrefs.edit().clear().apply();
                mToken = null;
                return null;
            }
            if (tokenResult.has("access_token")) {
                accessToken = tokenResult.get("access_token").getAsString();
                expiresIn = tokenResult.get("expires_in").getAsInt();
            }
            if (tokenResult.has("refresh_token")) {
                refreshToken = tokenResult.get("refresh_token").getAsString();
            }

            AuthToken at = new AuthToken(accessToken, refreshToken, System.currentTimeMillis() / 1000 + expiresIn);
            saveAuthToken(at);
            return authToken;
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof UnknownHostException || ex.getCause() instanceof TimeoutException){
                throw new NoNetworkConnection();
            } else {
                onLogCrash(ex);
            }
        }
        catch (Exception e) {
            onLogCrash(e);
        }

        return null;
    }

    /**
     * Set AuthToken to current instance of AuthHelper and save it in storage
     * @param token
     */
    public void setAuthToken(AuthToken token, boolean startCallback){
        saveAuthToken(token);
        mToken = token.accessToken;
        if (mListener != null){
            mListener.onSetAuthToken();
        }
    }

    /**
     * Saves AuthToken in storage
     * @param token
     */
    private void saveAuthToken(AuthToken token){
        mPrefs.edit()
                .putString(PREF_KEY,token.accessToken)
                .putString(PREF_REFRESH,token.refreshToken)
                .putLong(PREF_EXPIRE,token.expireDate).apply();
    }

    /**
     * Reads AuthToken from storage
     * @return
     */
    private AuthToken readAuthToken(){
        return new AuthToken(
                mPrefs.getString(PREF_KEY, null),
                mPrefs.getString(PREF_REFRESH,null),
                mPrefs.getLong(PREF_EXPIRE,0)
        );
    }

    protected ToolsPrefs getToolPrefs(){
        return mToolsPrefs;
    }


//    public void onEvent(NoAuthEvent event){
//        mPrefs.edit().clear().apply();
//        mToken = null;
//    }

    /**
     * Checks if user is logged in
     * @return true if user is logged
     */
    public boolean isLoggedIn(){
        return readAuthToken().refreshToken != null;
    }

    /**
     * Request auth token string, if token is expired AuthHelper would try to refresh it
     * @return token string, usable in requests
     * @throws NoNetworkConnection if refresh failed 2 times because of connectivity problems, this exception would be thrown
     */
    public String getAuthTokenWithRefresh() throws NoNetworkConnection {
        AuthToken token = readAuthToken();
        if (!token.isExpired()){
            return token.accessToken;
        }

        if (token.refreshToken != null){
            //prevents double-token refresh
            synchronized (refreshLock) {
                AuthToken t = readAuthToken();
                if (t.isExpired()) {
                    try {
                        return refreshToken().accessToken;
                    } catch (NoNetworkConnection ex){
                        return refreshToken().accessToken;
                    }
                } else {
                    return token.accessToken;
                }
            }
        }

        return null;
    }

    public String getClientId() {
        return mClientId;
    }

    public String getClientSecret() {
        return mClientSecret;
    }

    public void setClientId(String mClientId) {
        this.mClientId = mClientId;
    }

    public void setClientSecret(String mClientSecret) {
        this.mClientSecret = mClientSecret;
    }

    /**
     * Removes auth info from storage and de-auth user
     */
    public void logOut() {
        mPrefs.edit().clear().apply();
        if (mListener != null){
            mListener.onLogOut();
        }
    }

    /**
     * Should be overridden, if exceptions should be logged
     * @param ex
     */
    protected void onLogCrash(Exception ex){

    }

    public String getApplicationSource() {
        return mApplicationSource;
    }

    public static class AuthToken {
        public final String accessToken;
        public final String refreshToken;
        public final long expireDate;

        public AuthToken(String accessToken, String refreshToken, long expireTimestamp ) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expireDate = expireTimestamp;
        }

        public AuthToken(String refreshToken) {
            this.accessToken = null;
            this.expireDate = 0;
            this.refreshToken = refreshToken;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() / 1000 > expireDate;
        }
    }
}
