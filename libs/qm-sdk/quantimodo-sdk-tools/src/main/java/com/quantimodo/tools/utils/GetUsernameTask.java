package com.quantimodo.tools.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.activities.QuantimodoLoginActivity;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * AsynkTask to fetch the google token and handling the result
 */
public class GetUsernameTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = GetUsernameTask.class.getSimpleName();

    ToolsPrefs mPrefs;

    QuantimodoLoginActivity mActivity;
    String mScope;
    String mEmail;

    public GetUsernameTask(QuantimodoLoginActivity activity, String name, String scope, ToolsPrefs mPrefs) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = name;
        this.mPrefs = mPrefs;
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            String token = fetchToken();
            if (token != null)
                return token;
            else
                Log.d(TAG, "Google token null :(");
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "Sending Google token to QM Server: " + result);
        mActivity.sendToken("google", result);
    }


    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            Log.d("GetUsernameTask", "account id: " + GoogleAuthUtil.getAccountId(mActivity, mEmail));
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            mActivity.handleException(userRecoverableException);
//            userRecoverableException.printStackTrace();
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            fatalException.printStackTrace();
        }
        return null;
    }
}