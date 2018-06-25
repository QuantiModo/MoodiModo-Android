package com.quantimodo.tools.testhelpers;

import android.content.Context;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.quantimodo.android.sdk.SdkDefs;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TestHelper {
    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_PASSWORD = "testing123";
    public static final String QUANTIMODO_ADDRESS = "https://staging.quantimo.do/";
    public static final String QUANTIMODO_SOCIAL_AUTH = "https://staging.quantimo.do/api/v2/auth/social/authorizeToken";


    public static String logIn(final Context context) throws ExecutionException, InterruptedException {
        JsonObject response = Ion.with(context, QUANTIMODO_ADDRESS + "signin/")
                .setBodyParameter("log", TEST_USERNAME)
                .setBodyParameter("pwd", TEST_PASSWORD)
                .setBodyParameter("lwa", "1")
                .setBodyParameter("login-with-ajax", "login")
                .asJsonObject().get();


        String cookieToken  = null;
        if (response.get("result").getAsBoolean()) {
            CookieStore cookieStore = Ion.getDefault(context).getCookieMiddleware().getCookieStore();
            List<HttpCookie> cookieList = cookieStore.get(URI.create(QUANTIMODO_ADDRESS));

            for (HttpCookie cookie : cookieList) {
                if (cookie.getName().contains("wordpress_logged_in_")) {
                    cookieToken = cookie.getValue();
                    break;
                }
            }
        }



        return cookieToken;
    }


}
