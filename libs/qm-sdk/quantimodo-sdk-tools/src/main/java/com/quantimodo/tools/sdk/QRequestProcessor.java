package com.quantimodo.tools.sdk;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.RequestProcessor;
import com.octo.android.robospice.request.RequestProgressManager;
import com.octo.android.robospice.request.RequestRunner;
import com.octo.android.robospice.request.listener.RequestListener;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.events.TokenEvent;
import com.quantimodo.tools.sdk.db.DbRequest;
import com.quantimodo.tools.sdk.request.SdkRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QRequestProcessor extends RequestProcessor {

    private boolean mWait = true;
    private Map<CachedSpiceRequest<?>,Set<RequestListener<?>>> mAwaitingRequests = new HashMap<>();

    /**
     * Build a request processor using a custom. This feature has been
     * implemented following a feature request from Riccardo Ciovati.
     *
     * @param cacheManager           the {@link CacheManager} that will be used to retrieve
     *                               requests' result and store them.
     * @param requestProgressManager
     * @param requestRunner
     */
    public QRequestProcessor(CacheManager cacheManager, RequestProgressManager requestProgressManager, RequestRunner requestRunner) {
        super(cacheManager, requestProgressManager, requestRunner);
        QTools.getInstance().registerSticky(this);
    }

    @Override
    public void shouldStop() {
        QTools.getInstance().unregister(this);
        super.shouldStop();
    }

    @Override
    public void addRequest(CachedSpiceRequest<?> request, Set<RequestListener<?>> listRequestListener) {
        if (request.getSpiceRequest() instanceof SdkRequest) {
            if (!mWait) {
                QTools.getInstance().inject(request.getSpiceRequest());
                super.addRequest(request, listRequestListener);
            } else {
                mAwaitingRequests.put(request, listRequestListener);
            }
        }  else if (request.getSpiceRequest() instanceof DbRequest) {
            QTools.getInstance().inject(request.getSpiceRequest());
            request.setOffline(true);
            super.addRequest(request,listRequestListener);
        }
        else {
            super.addRequest(request, listRequestListener);
        }
    }

    public void onEvent(TokenEvent loaded){
        mWait = loaded.shouldWait;
        if (!mWait) {
            for (Map.Entry<CachedSpiceRequest<?>, Set<RequestListener<?>>> r : mAwaitingRequests.entrySet()) {
                CachedSpiceRequest<?> request = r.getKey();
                if (request.getSpiceRequest() instanceof SdkRequest) {
                    QTools.getInstance().inject(request.getSpiceRequest());
                }
                addRequest(request, r.getValue());
            }
            mAwaitingRequests.clear();
        }
    }
}
