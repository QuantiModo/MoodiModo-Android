package com.quantimodo.android.sdk.login;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class CallbackManagerImpl implements CallbackManager {
    private static Map<Integer, Callback> staticCallbacks = new HashMap<>();

    /**
     * If there is no explicit callback, but we still need to call the Facebook component,
     * because it's going to update some state, e.g., login, like. Then we should register a
     * static callback that can still handle the response.
     * @param requestCode The request code.
     * @param callback The callback for the feature.
     */
    public synchronized static void registerStaticCallback(
            int requestCode,
            @NonNull Callback callback) {
        if (staticCallbacks.containsKey(requestCode)) {
            return;
        }
        staticCallbacks.put(requestCode, callback);
    }

    private static synchronized Callback getStaticCallback(Integer requestCode) {
        return staticCallbacks.get(requestCode);
    }

    private static boolean runStaticCallback(
            int requestCode,
            int resultCode,
            Intent data) {
        Callback callback = getStaticCallback(requestCode);
        if (callback != null) {
            return callback.onActivityResult(resultCode, data);
        }
        return false;
    }

    private Map<Integer, Callback> callbacks = new HashMap<>();

    public void registerCallback(int requestCode, @NonNull Callback callback) {
        callbacks.put(requestCode, callback);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Callback callback = callbacks.get(requestCode);
        if (callback != null) {
            return callback.onActivityResult(resultCode, data);
        }
        return runStaticCallback(requestCode, resultCode, data);
    }

    public interface Callback {
        public boolean onActivityResult(int resultCode, Intent data);
    }

}
