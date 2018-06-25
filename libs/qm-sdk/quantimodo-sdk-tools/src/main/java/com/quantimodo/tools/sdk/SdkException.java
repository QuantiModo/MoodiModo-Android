package com.quantimodo.tools.sdk;

import com.quantimodo.android.sdk.SdkResponse;

public class SdkException extends Exception {
    protected int mHttpCode;
    protected int mErrorCode;
    protected String mStringBody;

    public SdkException(SdkResponse sdkResponse){
        super(sdkResponse.getMessage(),sdkResponse.getCause());
        mHttpCode = sdkResponse.getHttpCode();
        mErrorCode = sdkResponse.getErrorCode();
        mStringBody = sdkResponse.getStringBody();
    }

    public int getHttpCode() {
        return mHttpCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getStringBody() {
        return mStringBody;
    }

}
