package com.quantimodo.tools.sdk.request;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.SdkException;
import com.quantimodo.tools.utils.QtoolsUtils;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Base class for request made for QuantimodoAPI
 * Includes Quantimodo client, and AuthHelper. Redefines default retry policy.
 * @param <T> Class of result, returned by {@link #loadDataFromNetwork() loadDataFromNetwork()} method
 */
public abstract class SdkRequest<T> extends SpiceRequest<T>{

    private static final  DefaultRetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(0,0,0);

    @Inject
    Context ctx;

    @Inject
    QuantimodoApiV2 client;

    @Inject
    AuthHelper mAuthHelper;

    public SdkRequest(Class<T> clazz) {
        super(clazz);
        setRetryPolicy(DEFAULT_RETRY_POLICY);
    }

    protected void checkResponse(SdkResponse response) throws Exception {
        if (!response.isSuccessful()){
            throw new SdkException(response);
        }
    }

    /**
     * @return cacheDuration in millis
     */
    protected long getCacheTime(){
        return 0;
    }

    protected String getCacheKey(){
        return "";
    }

    public CachedSpiceRequest<T> getCachedSpiceRequest(){
        return new CachedSpiceRequest<>(this,getCacheKey(),getCacheTime());
    }

    public Context getCtx() {
        return ctx;
    }

    public QuantimodoApiV2 getClient() {
        return client;
    }

    public String getToken() throws NoNetworkConnection {
        return mAuthHelper.getAuthTokenWithRefresh();
    }

    public boolean isNetworkAvailable() {
        return QtoolsUtils.hasInternetConnection(getCtx());
    }
}
