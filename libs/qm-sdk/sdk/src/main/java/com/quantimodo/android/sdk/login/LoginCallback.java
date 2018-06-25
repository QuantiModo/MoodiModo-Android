package com.quantimodo.android.sdk.login;

/**
 * Interface used to deliver the result from a login process
 */
public interface LoginCallback {
    /**
     * Called when the dialog completes without error
     * If the operation was succeed you can get the token from now on using
     * {@link QuantimodoSDKHelper#getInstance()#getAuthTokenWithRefresh()}
     */
    void onSuccess();

    /**
     * Called when the dialog is canceled
     */
    void onCancel();

    /**
     * Called when the dialog finishes with an error
     * @param error The error that occurred
     */
    void onError(String error);
}
