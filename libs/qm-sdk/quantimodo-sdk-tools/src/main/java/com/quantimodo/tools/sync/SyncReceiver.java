package com.quantimodo.tools.sync;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.quantimodo.tools.QTools;

import static com.quantimodo.tools.sync.SyncService.SYNC_FROM_SCRATCH_KEY;

/**
 * Used to invoke sync process, in IntentService with WakeLock
 * Must be added to AndroidManifest.xml file
 */
public class SyncReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, QTools.getInstance().getSyncServiceClass());
        service.putExtra("withLock", true);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            service.putExtra(SYNC_FROM_SCRATCH_KEY,extras.getBoolean(SYNC_FROM_SCRATCH_KEY));
        }
        startWakefulService(context,service);
    }
}
