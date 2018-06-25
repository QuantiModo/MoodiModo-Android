package com.quantimodo.tools.sdk.db;

import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.quantimodo.tools.models.DaoSession;

import javax.inject.Inject;

public abstract class DbRequest<T> extends SpiceRequest<T> {

    private static final DefaultRetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(0,0,0);

    public DbRequest(Class<T> clazz) {
        super(clazz);
    }

    @Inject
    DaoSession mSession;

    public DaoSession getDaoSession() {
        return mSession;
    }
}
