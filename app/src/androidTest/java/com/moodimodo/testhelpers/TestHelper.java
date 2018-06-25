package com.moodimodo.testhelpers;

import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.web.sugar.Web;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.quantimodo.tools.activities.QuantimodoWebAuthenticatorActivity;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.webdriver.DriverAtoms.*;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static android.support.test.espresso.web.webdriver.Locator.ID;
import static android.support.test.espresso.web.webdriver.Locator.NAME;
import static android.support.test.espresso.web.webdriver.Locator.CLASS_NAME;
import static junit.framework.Assert.*;

public class TestHelper {
    public static final String TEST_USERNAME = "Quantimodo";
    public static final String TEST_PASSWORD = "B1ggerstaff!";
    public static final String QUANTIMODO_ADDRESS = "https://staging.quantimo.do/";
    public static final String QUANTIMODO_AUTH_ADDRESS = "https://staging.quantimo.do/api/v2/auth/social/authorizeToken";


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

    public static void logInProccess(Context context) throws Exception{
        Intent intent = new Intent(context, QuantimodoWebAuthenticatorActivity.class);
        context.startActivity(intent);

        Thread.sleep(4000);

        Web.WebInteraction wi = Web.onWebView(withId(com.quantimodo.tools.R.id.web));

        wi
                .withElement(findElement(NAME,"user_login")).perform(webKeys(TestHelper.TEST_USERNAME))
                .withElement(findElement(NAME,"user_pass")).perform(webKeys(TestHelper.TEST_PASSWORD))
                .withElement(findElement(CLASS_NAME,"btn-primary")).perform(webClick());

        //Wait sometime until page is loaded
        Thread.sleep(3000);

        String text = wi.withElement(findElement(ID,"request-heading")).perform(getText()).get().toString();
        assertTrue(text.contains("would like to"));
        wi.withElement(findElement(ID,"button-approve")).perform(webClick());

        Thread.sleep(3000);
    }


}
