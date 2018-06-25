package com.moodimodo.activities;

import android.app.Activity;
import android.widget.Toast;
import com.moodimodo.R;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.events.SyncStarted;

public abstract class MActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        QTools.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        QTools.getInstance().unregister(this);
    }

    public void onEventMainThread(SyncStarted event){
        Toast.makeText(this, R.string.toast_sync_data, Toast.LENGTH_SHORT).show();
    }
}
