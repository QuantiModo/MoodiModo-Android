package com.quantimodo.android.sdk.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.android.sdk.R;
import com.quantimodo.android.sdk.model.QuantimodoUser;

import java.util.Random;

/**
 * Activity for auth, used in pair with {@link QuantimodoSDKHelper AuthHelper}
 */
public class AuthenticatorActivity extends Activity {
    private String mNonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void initQMWebView(final QMAuthClient.OnAuthenticationCompleteListener listener) {
        CookieManager.getInstance().removeAllCookie();
        Ion.getDefault(this).getCookieMiddleware().getCookieStore().removeAll();

        WebView webView = (WebView) findViewById(R.id.web);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        webView.setWebViewClient(new QMAuthClient(listener));

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
        Uri uri = Uri.parse(ToolsPrefs.getInstance().getApiUrl());
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setCookie(ToolsPrefs.getInstance().getApiUrl(),
                "wordpress_test_cookie=WP+Cookie+check; domain=" + uri.getHost());
        webView.loadUrl(ToolsPrefs.getInstance().getApiUrl() + "api/oauth2/authorize");
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
                finish();

            }
        }));

        final String url = ToolsPrefs.getInstance().getApiUrl() + "api/oauth2/authorize?client_id=" + QuantimodoSDKHelper.getInstance().getClientId()
                + "&response_type=code&scope=" + ToolsPrefs.getInstance().getApiScopes() + "&state=" + mNonce;
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
        Ion.with(this, ToolsPrefs.getInstance().getApiUrl() + "api/oauth2/token")
                .setBodyParameter("client_id", QuantimodoSDKHelper.getInstance().getClientId())
                .setBodyParameter("client_secret", QuantimodoSDKHelper.getInstance().getClientSecret())
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

                        QuantimodoSDKHelper.getInstance().setAuthToken(
                                new QuantimodoSDKHelper.AuthToken(
                                        accessToken, refreshToken, System.currentTimeMillis() / 1000 + expiresIn
                                ), true
                        );
                        getUserData();
                        setResult(RESULT_OK);
                        finish();
                    } catch (NullPointerException ignored) {
                        Log.i(ToolsPrefs.DEBUG_TAG, "Error getting access token: " + result.get("error").getAsString()
                                + ", " + result.get("error_description").getAsString());
                        finish();
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
    public void onBackPressed(){
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static class QMAuthClient extends WebViewClient {
        private final OnAuthenticationCompleteListener listener;
        private final CookieManager cookieManager;

        protected interface OnAuthenticationCompleteListener {
            void onSuccess(String cookies);
            void onFailed();
        }

        public QMAuthClient(OnAuthenticationCompleteListener listener) {
            this.listener = listener;
            this.cookieManager = CookieManager.getInstance();
            this.cookieManager.removeAllCookie();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(ToolsPrefs.getInstance().getApiUrl())) {
                String cookies = cookieManager.getCookie(ToolsPrefs.getInstance().getApiUrl());
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
        try {
            QuantimodoApiV2 quantimodoApiV2 = QuantimodoApiV2.getInstance(
                    ToolsPrefs.getInstance().getApiUrl(),
                    QuantimodoSDKHelper.getInstance().getAuthTokenWithRefresh());
            QuantimodoUser user = quantimodoApiV2.getUser(this, QuantimodoSDKHelper.getInstance().getAuthTokenWithRefresh()).getData();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AuthenticatorActivity.this);
            prefs.edit().putString("userDisplayName", user.getDisplayName()).apply();
        } catch (NoNetworkConnection e){

        }
    }

    private static class OAuthClient extends WebViewClient {
        private final OnReceivedAuthorizeResponse listener;

        protected interface OnReceivedAuthorizeResponse {
            void onSuccess(String authorizationCode, String state);
            void onError(String error, String errorDescription);
        }

        public OAuthClient(OnReceivedAuthorizeResponse listener) {
            this.listener = listener;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.startsWith(ToolsPrefs.getInstance().getApiUrl())) {
                int startCode = url.indexOf("code=") + 5;
                int endCode = url.indexOf("&state=");

                if (startCode == 4)  // -1 + 5
                {
                    // Code and state not found, so we got an error
                    int startError = url.indexOf("error=") + 6;
                    int endError = url.indexOf("error_description=");

                    String error = url.substring(startError, endError);
                    String errorDescription = url.substring(endError + 6, url.length());
                    listener.onError(error, errorDescription);
                } else {
                    String code = url.substring(startCode, endCode);
                    String state = url.substring(endCode + 7, url.length());
                    listener.onSuccess(code, state);
                }

                return true;
            } else {
                view.loadUrl(url);
            }

            return true;
        }
    }
}
