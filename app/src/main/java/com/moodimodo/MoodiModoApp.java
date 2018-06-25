package com.moodimodo;

import android.content.Intent;
import android.os.StrictMode;

import com.amazon.device.associates.AssociatesAPI;
import com.crashlytics.android.Crashlytics;
import com.moodimodo.activities.MainActivity;
import com.moodimodo.events.MeasurementsUpdatedEvent;
import com.moodimodo.sdk.QMSpiceService;
import com.quantimodo.tools.QBaseApplication;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.activities.QuantimodoLoginActivity;
import com.quantimodo.tools.events.NoAuthEvent;
import com.quantimodo.tools.sdk.QSpiceService;
import com.quantimodo.tools.sync.SyncHelper;
import com.quantimodo.tools.sync.SyncService;
import com.quantimodo.tools.utils.CustomRemindersHelper;
import com.squareup.leakcanary.LeakCanary;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;
import io.swagger.client.SwaggerClient;

public class MoodiModoApp extends QBaseApplication {

    private long mLastNoAuthEvent = 0 ;
    private static final long TIME_LIMIT = 5000; //5 sec

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        if(isDebugBuild()){
            StrictMode.enableDefaults();
        }

        LeakCanary.install(this);
        SwaggerClient.getInstance().setAppBasePath(Global.QM_ADDRESS + "api");
        CustomRemindersHelper.getInstance().registerActivity(MainActivity.class);
        //QTools.setAppActionIcon(R.drawable.ic_action_appicon);
    }

    @Override
    protected ObjectGraph createObjectGraph() {
        ToolsPrefs prefs = new ToolsPrefs(Global.QM_ADDRESS,Global.QUANTIMODO_SCOPES, Global.QUANTIMODO_SOURCE_NAME, Global.QM_AUTH_SOCIAL_URL);
        AppModule requestModule = new AppModule(getApplicationContext(),prefs);
        ConfigModule configModule = new ConfigModule(requestModule.getDaoSession());
        return ObjectGraph.create(requestModule,configModule);
    }

    @Override
    protected void initCrashReports() {
        Fabric.with(this, new Crashlytics());
    }

    @Override
    protected void initApis() {
        Config config = new Config("quantimodo.uservoice.com");
        config.setForumId(211661);
        UserVoice.init(config, this);
        AssociatesAPI.initialize(new AssociatesAPI.Config("619f69e3e2744f6eae89cdea9d1b2772", this));
    }

    @Override
    public Class<? extends QSpiceService> getServiceClass() {
        return QMSpiceService.class;
    }

    @Override
    public Class<? extends SyncService> getSyncServiceClass() {
        return com.moodimodo.sync.SyncService.class;
    }

    public void onEventMainThread(NoAuthEvent event){
        long current = System.currentTimeMillis();
        if (current - mLastNoAuthEvent > TIME_LIMIT) {
            Intent intent = new Intent(this, QuantimodoLoginActivity.class);
            intent.putExtra(QuantimodoLoginActivity.KEY_SHOW_LOGIN_AGAIN, true);
            intent.putExtra(QuantimodoLoginActivity.KEY_APP_NAME, getString(R.string.app_name));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mLastNoAuthEvent = current;
        }
    }

    public void onEventMainThread(MeasurementsUpdatedEvent e){
        if (!e.fromSync) {
            SyncHelper.invokeSync(this);
        }
    }


}
