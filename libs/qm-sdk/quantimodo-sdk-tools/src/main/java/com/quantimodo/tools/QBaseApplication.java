package com.quantimodo.tools;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.widget.Toast;
import com.quantimodo.tools.sdk.QSpiceService;
import com.quantimodo.tools.sync.SyncService;
import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;

/**
 * Base implementation of QApplication interface
 * which provides ObjectGraph and EventBus
 */
public abstract class QBaseApplication extends Application implements QApplication {

    protected ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        QTools.init(this);
        if (!isDebugBuild()) {
            initCrashReports();
        }

        initApis();

        mObjectGraph = createObjectGraph();
    }

    @Override
    public <T> T getInstanceOf(Class<T> t){
        return mObjectGraph.get(t);
    }

    @Override
    public void addToGraph(Object object){
        mObjectGraph = mObjectGraph.plus(object);
    }

    protected boolean isDebugBuild(){
        return (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    protected abstract ObjectGraph createObjectGraph();
    protected abstract void initCrashReports();
    protected abstract void initApis();

    @Override
    public void inject(Object object){
        mObjectGraph.inject(object);
    }
    @Override
    public void register(Object object){
        EventBus.getDefault().register(object);
    }
    @Override
    public void registerSticky(Object object){
        EventBus.getDefault().registerSticky(object);
    }
    @Override
    public void postStickyEvent(Object object){
        EventBus.getDefault().postSticky(object);
    }
    @Override
    public void postEvent(Object object){
        EventBus.getDefault().post(object);
    }
    @Override
    public void showToast(String message,int duration){
        Toast.makeText(this, message, duration).show();
    }
    @Override
    public void showToast(int message,int duration){
        Toast.makeText(this,message,duration).show();
    }
    @Override
    public void unregister(Object object){
        EventBus.getDefault().unregister(object);
    }

    @Override
    public abstract Class<? extends QSpiceService> getServiceClass();
    @Override
    public abstract Class<? extends SyncService> getSyncServiceClass();
}
