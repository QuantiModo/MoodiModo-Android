package com.quantimodo.tools.testhelpers;

import com.quantimodo.tools.QBaseApplication;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.sdk.QSpiceService;
import com.quantimodo.tools.sync.SyncService;
import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;

import java.util.concurrent.ExecutionException;

public class TestBaseApplication extends QBaseApplication {

    @Override
    protected ObjectGraph createObjectGraph() {
        EventBus eventBus = EventBus.getDefault();
        ToolsPrefs prefs = new ToolsPrefs(TestHelper.QUANTIMODO_ADDRESS,"readmeasurements writemeasurements","QuantimodoTest", TestHelper.QUANTIMODO_SOCIAL_AUTH);
        TestModule requestModule = null;
        try {
            requestModule = new TestModule(getApplicationContext(),prefs);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
//        eventBus.registerSticky(requestModule);
        return ObjectGraph.create(requestModule);
    }

    @Override
    protected void initCrashReports() {

    }

    @Override
    protected void initApis() {

    }

    @Override
    public Class<? extends QSpiceService> getServiceClass() {
        return TestService.class;
    }

    @Override
    public Class<? extends SyncService> getSyncServiceClass() {
        return null;
    }

}
