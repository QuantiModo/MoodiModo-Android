package com.quantimodo.android.sdk.login;

import android.content.Intent;

public interface CallbackManager {

    /**
     * The method that should be called from the Activity's or Fragment's onActivityResult method.
     * @param requestCode The request code that's received by the Activity or Fragment.
     * @param resultCode  The result code that's received by the Activity or Fragment.
     * @param data        The result data that's received by the Activity or Fragment.
     * @return true If the result could be handled.
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * The factory class for the {@link CallbackManager}.
     */
    public static class Factory {
        /**
         * Creates an instance of {@link CallbackManager}.
         * @return an instance of {@link CallbackManager}.
         */
        public static CallbackManager create() {
            return new CallbackManagerImpl();
        }
    }
}

