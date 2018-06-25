package com.quantimodo.android.sdk.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * Performs the login starting the proper Activity and delivers the result on a callback or as
 * Activity Result.
 */
public class LoginManager {
    public static final int LOGIN_REQUEST_CODE = 856173496;
    private static volatile LoginManager instance;

    /**
     * Getter for the login manager.
     * @return The login manager.
     */
    public static LoginManager getInstance() {
        if (instance == null) {
            synchronized (LoginManager.class) {
                if (instance == null) {
                    instance = new LoginManager();
                }
            }
        }

        return instance;
    }

    private LoginManager() {
    }

    /**
     * Registers a login callback to the given callback manager.
     * @param callbackManager The callback manager that will encapsulate the callback.
     * @param callback The login callback that will be called on login completion.
     */
    public void registerCallback(
            final CallbackManager callbackManager,
            final LoginCallback callback) {
        if (!(callbackManager instanceof CallbackManagerImpl)) {
            throw new RuntimeException("Unexpected CallbackManager, " +
                    "please use the provided Factory.");
        }
        ((CallbackManagerImpl) callbackManager).registerCallback(
                0,
                new CallbackManagerImpl.Callback() {
                    @Override
                    public boolean onActivityResult(int resultCode, Intent data) {
                        return LoginManager.this.onActivityResult(
                                resultCode,
                                data,
                                callback);
                    }
                }
        );
    }

    /**
     * Performs the login from an Activity
     * @param activity the current Activity
     */
    public void performLogin(Activity activity) {
        startLogin(new ActivityStartActivityDelegate(activity));
    }

    /**
     * Performs the login from a Fragment
     * @param fragment the current Fragment
     */
    public void performLogin(Fragment fragment){
        startLogin(new FragmentStartActivityDelegate(fragment));
    }

    private void startLogin(StartActivityDelegate delegate){
        // Make sure the static handler for login is registered if there isn't an explicit callback
        CallbackManagerImpl.registerStaticCallback(
                LOGIN_REQUEST_CODE,
                new CallbackManagerImpl.Callback() {
                    @Override
                    public boolean onActivityResult(int resultCode, Intent data) {
                        return LoginManager.this.onActivityResult(resultCode, data);
                    }
                }
        );
        Intent intent = new Intent(
                delegate.getActivityContext(), AuthenticatorActivity.class);
        delegate.startActivityForResult(intent);
    }

    private boolean onActivityResult(int resultCode, Intent data) {
        return onActivityResult(resultCode, data, null);
    }

    private boolean onActivityResult(int resultCode, Intent data, LoginCallback callback) {
        if(resultCode == Activity.RESULT_OK) callback.onSuccess();
        else if(resultCode == Activity.RESULT_CANCELED) callback.onCancel();
        else callback.onError("Internal error when getting the access token");
        return true;
    }

    private static class ActivityStartActivityDelegate implements StartActivityDelegate {
        private final Activity activity;

        ActivityStartActivityDelegate(@NonNull final Activity activity) {
            this.activity = activity;
        }

        @Override
        public void startActivityForResult(Intent intent) {
            activity.startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }

        @Override
        public Activity getActivityContext() {
            return activity;
        }
    }

    private static class FragmentStartActivityDelegate implements StartActivityDelegate {
        private final Fragment fragment;

        FragmentStartActivityDelegate(@NonNull final Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void startActivityForResult(Intent intent) {
            fragment.startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }

        @Override
        public Activity getActivityContext() {
            return fragment.getActivity();
        }
    }
}
