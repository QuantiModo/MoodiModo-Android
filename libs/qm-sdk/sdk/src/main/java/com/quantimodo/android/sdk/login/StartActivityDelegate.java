package com.quantimodo.android.sdk.login;

import android.app.Activity;
import android.content.Intent;

interface StartActivityDelegate {
    public void startActivityForResult(Intent intent);

    public Activity getActivityContext();
}
