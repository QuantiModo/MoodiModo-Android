package com.quantimodo.tools.testhelpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import com.quantimodo.tools.sdk.QSpiceService;
import com.quantimodo.tools.sdk.request.GetCategoriesRequest;
import com.quantimodo.tools.sdk.request.GetUnitsRequest;
import com.quantimodo.tools.sdk.request.SearchCorrelationsRequest;

import java.util.ArrayList;
import java.util.List;

public class TestService extends QSpiceService {

    @Override
    protected List<Class<?>> getClassesEnabledToCache(){
        return new ArrayList<>();
    }

    @Override
    public int getMaximumThreadCount() {
        return 1;
    }

    @Override
    public Notification createDefaultNotification() {
        Notification notification = super.createDefaultNotification();
        notification.icon = com.quantimodo.tools.test.R.drawable.icon;
        return notification;
    }
}
