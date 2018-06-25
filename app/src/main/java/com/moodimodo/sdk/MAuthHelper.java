package com.moodimodo.sdk;

import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.koushikdutta.ion.Ion;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.events.NoAuthEvent;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.NoNetworkConnection;
import com.quantimodo.tools.sync.SyncHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MAuthHelper extends AuthHelper {
    public MAuthHelper(Context ctx, ToolsPrefs prefs) {
        super(ctx, prefs);
    }

    @Override
    public AuthToken refreshToken(AuthToken authToken) throws NoNetworkConnection {
        String jsonString = "";
        try {
             jsonString = Ion.with(getCtx(), getToolPrefs().getApiUrl() + "api/oauth2/token")
                    .setTimeout(35000)
                    .setBodyParameter("client_id", getClientId())
                    .setBodyParameter("client_secret", getClientSecret())
                    .setBodyParameter("grant_type", "refresh_token")
                    .setBodyParameter("refresh_token", authToken.refreshToken)
                    .asString()
                    .get();

            JSONObject tokenResult = new JSONObject(jsonString);
            String accessToken = null;
            String refreshToken = authToken.refreshToken;
            int expiresIn = 0;
            if (tokenResult.has("error")) {
                QTools.getInstance().postEvent(new NoAuthEvent());
                return null;
            }
            if (tokenResult.has("access_token")) {
                accessToken = tokenResult.getString("access_token");
                expiresIn = tokenResult.getInt("expires_in");
            }
            if (tokenResult.has("refresh_token")) {
                refreshToken = tokenResult.getString("refresh_token");
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
        } catch (JSONException jsonException){
            Crashlytics.log(jsonString);
            onLogCrash(jsonException);
        }
        catch (Exception e) {
            onLogCrash(e);
        }

        return null;
    }

    @Override
    public void setAuthToken(AuthToken token) {
        super.setAuthToken(token);
        SyncHelper.scheduleSync(getCtx());
    }

    @Override
    protected void onLogCrash(Exception ex) {
        Crashlytics.logException(ex);
    }
}
