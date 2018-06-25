package com.quantimodo.tools.testhelpers;

import android.os.Bundle;
import com.quantimodo.tools.sync.SyncService;

public class TestSyncService extends SyncService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name
     */
    public TestSyncService() {
        super("Test sync service");
    }

    @Override
    protected void handleException(Exception ex) {
        ex.printStackTrace();
        throw new RuntimeException(ex);
    }

    @Override
    protected void initCrashReporting() {

    }

    @Override
    protected int getIconId() {
        return 0;
    }

    @Override
    protected void sync(Bundle bundle) throws Exception {
        defaultSync();
    }
}
