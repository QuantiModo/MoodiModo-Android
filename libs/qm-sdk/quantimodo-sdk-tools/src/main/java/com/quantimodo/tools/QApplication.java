package com.quantimodo.tools;

import android.support.annotation.IntDef;
import android.widget.Toast;
import com.quantimodo.tools.sdk.QSpiceService;
import com.quantimodo.tools.sync.SyncService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *  Should provide dependencies and EventBus for other components of quantimodo-tools
 *  For example implementations see {@link QBaseApplication QBaseApplication}
 */
public interface QApplication {

    @IntDef({Toast.LENGTH_SHORT,Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    @interface ToastDuration {
    }

    /**
     *
     * @param t
     * @param <T>
     * @return
     */
    <T> T getInstanceOf(Class<T> t);

    /**
     *
     * @param object
     */
    void addToGraph(Object object);

    /**
     * Inject dependencies of provided object
     * @param object Object in which dependencies should be injected
     */
    void inject(Object object);

    /**
     * Register in EventBus as subscriber
     * @param object Subscriber
     */
    void register(Object object);

    /**
     * Register in EventBus as subscriber, this includes re-deliver of sticky events
     * @param object Subscriber
     */
    void registerSticky(Object object);

    /**
     * Send sticky event, which should be re-delivered to all new "sticky" subscribers
     * @param object Event
     */
    void postStickyEvent(Object object);

    /**
     * Send event
     * @param object Event
     */
    void postEvent(Object object);

    /**
     * Unregister subscriber from EventBus
     * @param object Subscriber
     */
    void unregister(Object object);

    /**
     * Show toast message
     * @param message Toast message
     * @param duration Toast duration
     */
    void showToast(String message,@ToastDuration int duration);

    /**
     * Show toast message
     * @param message Toast message id
     * @param duration Toast duration
     */
    void showToast(int message,@ToastDuration int duration);

    /**
     * QuantimodoTools uses <a href="https://github.com/stephanenicolas/robospice">Robospice</a>  as request processor
     * You need to implement QSpiceService, and provide class object of this implementation in this method
     * Don't forget to add this service in AndroidManifest.xml
     * For more info check {@link QSpiceService QSpiceService}
     * @return QSpiceService derived class
     */
    Class<? extends QSpiceService> getServiceClass();

    /**
     * Sync service, which should provide syncing with QuantiModo
     * For into check {@link SyncService SyncService}
     * @return
     */
    Class<? extends SyncService> getSyncServiceClass();

}
