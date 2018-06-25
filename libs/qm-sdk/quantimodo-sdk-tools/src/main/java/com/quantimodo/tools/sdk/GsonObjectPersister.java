package com.quantimodo.tools.sdk;

import android.app.Application;
import android.util.Log;
import com.google.gson.Gson;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.file.InFileObjectPersister;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class GsonObjectPersister<T> extends InFileObjectPersister<T> {

    private final static String TAG = GsonObjectPersister.class.toString();

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================

    private final Gson gson;

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================
    public GsonObjectPersister(Application application, Class<T> clazz, File cacheFolder) throws CacheCreationException {
        super(application, clazz, cacheFolder);
        this.gson = new Gson();
    }


    // ============================================================================================
    // METHODS
    // ============================================================================================

    protected T deserializeData(String json) {
        return gson.fromJson(json, getHandledClass());
    }

    @Override
    public T saveDataToCacheAndReturnData(final T data, final Object cacheKey) throws CacheSavingException {

        try {
            if (isAsyncSaveEnabled()) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            saveData(data, cacheKey);
                        } catch (IOException | CacheSavingException e) {
                            Log.e(TAG, "An error occured on saving request " + cacheKey + " data asynchronously", e);
                        }
                    }
                };
                t.start();
            } else {
                saveData(data, cacheKey);
            }
        } catch (CacheSavingException e) {
            throw e;
        } catch (Exception e) {
            throw new CacheSavingException(e);
        }
        return data;
    }

    protected void saveData(T data, Object cacheKey) throws IOException, CacheSavingException {
        String resultJson;
        // transform the content in json to store it in the cache
        resultJson = gson.toJson(data);

        // finally store the json in the cache
        if (!StringUtils.isEmpty(resultJson)) {
            FileUtils.writeStringToFile(getCacheFile(cacheKey), resultJson, CharEncoding.UTF_8);
        } else {
            throw new CacheSavingException("Data was null and could not be serialized in json");
        }
    }

    @Override
    protected T readCacheDataFromFile(File file) throws CacheLoadingException {
        try {
            String resultJson = null;
            synchronized (file.getAbsolutePath().intern()) {
                resultJson = FileUtils.readFileToString(file, CharEncoding.UTF_8);
            }
            if (!StringUtils.isEmpty(resultJson)) {
                T result = deserializeData(resultJson);
                return result;
            }
            throw new CacheLoadingException("Unable to restore cache content : cache file is empty");
        } catch (FileNotFoundException e) {
            // Should not occur (we test before if file exists)
            // Do not throw, file is not cached
            return null;
        } catch (CacheLoadingException e) {
            throw e;
        } catch (Exception e) {
            throw new CacheLoadingException(e);
        }
    }

    public GsonObjectPersister(Application application, Class<T> clazz) throws CacheCreationException {
        this(application, clazz, null);
    }

}