package com.quantimodo.tools.sdk;

import android.app.Application;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.file.InFileObjectPersisterFactory;
import com.octo.android.robospice.request.RequestProcessor;
import com.octo.android.robospice.request.RequestProgressManager;
import com.octo.android.robospice.request.RequestRunner;
import com.quantimodo.tools.sdk.request.GetCategoriesRequest;
import com.quantimodo.tools.sdk.request.GetUnitsRequest;
import com.quantimodo.tools.sdk.request.SearchCorrelationsRequest;
import com.quantimodo.tools.sdk.request.SearchCustomCorrelationsRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service for running network requests
 * Should be derived if application uses quantimodo-tools
 * Uses file storage for cache, objects encoded in json by gson.
 */
public abstract class QSpiceService extends SpiceService {

    /**
     * Return classes that should be cached by this Service
     * @return List of class that should be cached
     */
    protected abstract List<Class<?>> getClassesEnabledToCache();

    //Default classes to cache
    private static final Class<?>[] DEFAULT_CLASSES_ENABLED_TO_CACHE = {
            SearchCorrelationsRequest.CorrelationsResponse.class,
            SearchCustomCorrelationsRequest.CorrelationsResponse.class,
            GetUnitsRequest.GetUnitsResponse.class,
            GetCategoriesRequest.GetCategoriesResponse.class
    };

    @Override
    public abstract int getMaximumThreadCount();

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();

        List<Class<?>> classes = getClassesEnabledToCache();
        Collections.addAll(classes,DEFAULT_CLASSES_ENABLED_TO_CACHE);

        InFileObjectPersisterFactory inFileObjectPersisterFactory = new GsonObjectPersisterFactory(getApplication(),classes,getCacheDir());
        cacheManager.addPersister(inFileObjectPersisterFactory);

        return cacheManager;
    }

    @Override
    protected RequestProcessor createRequestProcessor(CacheManager cacheManager, RequestProgressManager requestProgressManager, RequestRunner requestRunner) {
        return new QRequestProcessor(cacheManager, requestProgressManager, requestRunner);
    }
}
