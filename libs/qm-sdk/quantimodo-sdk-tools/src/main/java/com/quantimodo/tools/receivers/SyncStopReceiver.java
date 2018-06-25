package com.quantimodo.tools.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.events.RequestSyncStopEvent;

/**
 * Used from notification, to stop sync process
 */
public class SyncStopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        QTools.getInstance().postEvent(new RequestSyncStopEvent());
    }
}
