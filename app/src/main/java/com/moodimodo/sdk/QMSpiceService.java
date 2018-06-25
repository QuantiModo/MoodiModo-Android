package com.moodimodo.sdk;

import com.quantimodo.tools.sdk.QSpiceService;
import com.quantimodo.tools.sdk.request.GetCategoriesRequest;
import com.quantimodo.tools.sdk.request.GetUnitsRequest;
import com.quantimodo.tools.sdk.request.SearchCorrelationsRequest;

import java.util.ArrayList;
import java.util.List;

public class QMSpiceService extends QSpiceService {

    public static final int THREAD_COUNT = 3;

    @Override
    protected List<Class<?>> getClassesEnabledToCache() {
        return new ArrayList<>();
    }

    @Override
    public int getMaximumThreadCount() {
        return THREAD_COUNT;
    }

}
