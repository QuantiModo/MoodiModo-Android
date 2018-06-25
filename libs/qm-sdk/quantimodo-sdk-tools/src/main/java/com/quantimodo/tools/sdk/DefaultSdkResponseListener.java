package com.quantimodo.tools.sdk;

import android.widget.Toast;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.R;
import com.quantimodo.tools.events.NoAuthEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  Intended to use with SdkRequest
 *  Checks error cause, and by default show Toast with message.
 *  If there is no auth, fires NoAuthEvent, to invoke relog.
 */
public abstract class DefaultSdkResponseListener<T> implements RequestListener<T> {

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        if (spiceException.getCause() instanceof SdkException){
            SdkException ex = (SdkException) spiceException.getCause();
            onSdkException(ex);
        } else {
            if (spiceException.getCause() != null && spiceException.getCause().getMessage() != null) {
                QTools.getInstance().showToast(spiceException.getCause().getMessage(), Toast.LENGTH_SHORT);
            } else {
                QTools.getInstance().showToast(R.string.sdk_unknown_error, Toast.LENGTH_SHORT);
            }
        }
    }

    public void onSdkException(SdkException ex){
        if (ex.getErrorCode() == SdkResponse.ERROR_AUTH){
            QTools.getInstance().postEvent(new NoAuthEvent());
        }

        try {
            //We log payload if we have Crashlytics
            Class cls = Class.forName("com.crashlytics.android.Crashlytics");
            Method method = cls.getMethod("log",String.class);
            method.invoke(null,ex.getStringBody());
        } catch (Exception ignored) {
        }
    }

}
