package com.moodimodo.testhelpers;

import com.quantimodo.tools.sdk.QSpiceService;

import java.util.ArrayList;
import java.util.List;

public class TestService extends QSpiceService {

    @Override
    protected List<Class<?>> getClassesEnabledToCache() {
        return new ArrayList<>();
    }

    @Override
    public int getMaximumThreadCount() {
        return 1;
    }

}
