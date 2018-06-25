package com.quantimodo.tools.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.android.sdk.model.QuantimodoUser;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.R;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.UserPreferences;
import com.quantimodo.tools.sdk.AuthHelper;

import javax.inject.Inject;
import java.util.Random;
import java.util.Set;

/**
 * Activity for auth, used in pair with {@link AuthHelper AuthHelper},
 * which should be injected by {@link com.quantimodo.tools.QApplication QApplication}
 */
public class QuantimodoWebAuthenticatorActivity extends Activity
{
    public static final java.lang.String KEY_SHOW_LOGIN_AGAIN = "show_login_again";

    @Inject
    AuthHelper authHelper;

    @Inject
    ToolsPrefs mPrefs;

    @Inject
    QuantimodoApiV2 quantimodoApiV2;

    private String mNonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QTools.getInstance().inject(this);
        int min = 100000000;
        int max = 999999999;
        mNonce = String.valueOf(new Random().nextInt(max - min) + min) + String.valueOf(new Random().nextInt(max - min) + min);

        setContentView(R.layout.qmt_authenticator_quantimodoweb);
        initQMWebView(new QMAuthClient.OnAuthenticationCompleteListener() {
            @Override
            public void onSuccess(final String cookies) {
                initOAuthWebView();
            }

            @Override
            public void onFailed() {
                Log.i(ToolsPrefs.DEBUG_TAG, "Couldn't log in");
            }
        });

        if (getIntent().hasExtra(KEY_SHOW_LOGIN_AGAIN) && getIntent().getExtras().getBoolean(KEY_SHOW_LOGIN_AGAIN,false)) {
            Toast.makeText(this, R.string.oauth_refresh_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void initQMWebView(final QMAuthClient.OnAuthenticationCompleteListener listener) {
        CookieManager.getInstance().removeAllCookie();
        Ion.getDefault(this).getCookieMiddleware().getCookieStore().removeAll();

        WebView webView = (WebView) findViewById(R.id.web);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        webView.setWebViewClient(new QMAuthClient(listener, mPrefs));

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else if (progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(newProgress);
            }
        });

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSavePassword(false);       // Deprecated in 18, passwords won't be saved by default from then on
        webView.getSettings().setJavaScriptEnabled(true);
        Uri uri = Uri.parse(mPrefs.getApiUrl());
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setCookie(mPrefs.getApiUrl(), "wordpress_test_cookie=WP+Cookie+check; domain=" + uri.getHost());
        webView.loadUrl(mPrefs.getApiUrl() + "api/oauth2/authorize");
    }

    private void initOAuthWebView() {
        WebView webView = (WebView) findViewById(R.id.web);
        webView.setWebViewClient(new OAuthClient(new OAuthClient.OnReceivedAuthorizeResponse() {
            @Override
            public void onSuccess(String authorizationCode, String state) {
                handleAuthorizationSuccess(authorizationCode, state);
            }

            @Override
            public void onError(String error, String errorDescription) {
                Log.d("QMWebAuthActivity", "Error: " + error + ", description: " + errorDescription);
            }
        }, mPrefs));

        final String url = mPrefs.getApiUrl() + "api/oauth2/authorize?client_id=" + authHelper.getClientId()
                + "&response_type=code&scope=" + mPrefs.getApiScopes() + "&state=" + mNonce;
        Log.d("QMWebAuthActivity", "Loading: " + url);
        webView.loadUrl(url);
    }

    private void handleAuthorizationSuccess(String authorizationCode, String nonce) {
        if (!mNonce.equals(nonce)) {
            Toast.makeText(this, "Request was tampered with", Toast.LENGTH_SHORT).show();
        } else {
            getAccessToken(authorizationCode);
        }
    }


    private void getAccessToken(String authorizationCode) {
        Ion.with(this, mPrefs.getApiUrl() + "api/oauth2/token")
                .setBodyParameter("client_id", authHelper.getClientId())
                .setBodyParameter("client_secret", authHelper.getClientSecret())
                .setBodyParameter("grant_type", "authorization_code")
                .setBodyParameter("code", authorizationCode)
                .asJsonObject()
                .withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> response) {
                JsonObject result = response.getResult();
                if (result != null) {
                    try {
                        String accessToken = result.get("access_token").getAsString();
                        String refreshToken = result.get("refresh_token").getAsString();
                        int expiresIn = result.get("expires_in").getAsInt();

                        authHelper.setAuthToken(new AuthHelper.AuthToken(accessToken,refreshToken, System.currentTimeMillis()/1000 + expiresIn));
                        getUserData();
                        setResult(RESULT_OK);
                        finish();
                    } catch (NullPointerException ignored) {
                        Log.i(ToolsPrefs.DEBUG_TAG,"Error getting access token: " + result.get("error").getAsString()
                                + ", " + result.get("error_description").getAsString());
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        CookieManager.getInstance().removeAllCookie();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static class QMAuthClient extends WebViewClient {
        private final OnAuthenticationCompleteListener listener;
        private final CookieManager cookieManager;
        private final ToolsPrefs toolsPrefs;

        protected interface OnAuthenticationCompleteListener {
            void onSuccess(String cookies);
            void onFailed();
        }

        public QMAuthClient(OnAuthenticationCompleteListener listener, ToolsPrefs toolsPrefs) {
            this.listener = listener;
            this.toolsPrefs = toolsPrefs;
            this.cookieManager = CookieManager.getInstance();
            this.cookieManager.removeAllCookie();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(toolsPrefs.getApiUrl())) {
                String cookies = cookieManager.getCookie(toolsPrefs.getApiUrl());
                if (cookies != null && cookies.contains("wordpress_logged_in")) {
                    String[] splits = cookies.split("; ");
                    for (String cookie : splits) {
                        if (cookie.startsWith("wordpress_logged_in")) {
                            listener.onSuccess(cookie);
                            return true;
                        }
                    }
                }
            }

            view.loadUrl(url);

            return true;
        }
    }

    private void getUserData(){
        QuantimodoUser user = quantimodoApiV2.getUser(this, authHelper.getAuthToken()).getData();
        UserPreferences.setFullUserdata(this, user);
    }

    private static class OAuthClient extends WebViewClient {
        private final OnReceivedAuthorizeResponse listener;
        private final ToolsPrefs mPrefs;

        protected interface OnReceivedAuthorizeResponse {
            void onSuccess(String authorizationCode, String state);
            void onError(String error, String errorDescription);
        }

        public OAuthClient(OnReceivedAuthorizeResponse listener, ToolsPrefs mPrefs) {
            this.listener = listener;
            this.mPrefs = mPrefs;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.startsWith(mPrefs.getApiUrl())) {
                Uri uri = Uri.parse(url);

                Set<String> params = uri.getQueryParameterNames();

                if (params.contains("code")){
                    String code = uri.getQueryParameter("code");
                    String state = uri.getQueryParameter("state");
                    listener.onSuccess(code, state);
                } else {
                    String error = uri.getQueryParameter("error");
                    String errorDescription = uri.getQueryParameter("error_description");
                    listener.onError(error, errorDescription);
                }
            } else {
                view.loadUrl(url);
            }

            return true;
        }
    }
}
