package com.quantimodo.android.sdk;

import java.util.List;
import java.util.Map;

/**
 * Default response object, contains info about response
 * <ul>
 *     <li>Http code</li>
 *     <li>Response headers</li>
 *     <li>Exception</li>
 * </ul>
 * @param <T> Type of result
 */
public class SdkResponse<T> {

    public static final int ERROR_NO = 0;
    public static final int ERROR_AUTH = 1;
    public static final int ERROR_NO_INTERNET = 2;
    public static final int ERROR_UNKNOWN = Integer.MAX_VALUE;

    private int mErrorCode = ERROR_NO;
    private int mHttpCode = 200;
    private Exception mCause;
    private String mMessage;
    private String mStringBody;
    private T mData;
    private Map<String,List<String>> mHeaders;

    public SdkResponse(){

    }

    public SdkResponse(T mData){
        mData = mData;
    }

    public SdkResponse(int mErrorCode, int mHttpCode, Exception mCause, String mMessage, T mData) {
        this.mErrorCode = mErrorCode;
        this.mHttpCode = mHttpCode;
        this.mCause = mCause;
        this.mMessage = mMessage;
        this.mData = mData;
    }

    public void setErrorCode(int mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    public void setHttpCode(int mHttpCode) {
        this.mHttpCode = mHttpCode;
    }

    public void setCause(Exception mCause) {
        this.mCause = mCause;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public void setData(T mData) {
        this.mData = mData;
    }

    /**
     * @return int http code, like ( 200, 404, 403, etc )
     */
    public int getHttpCode() {
        return mHttpCode;
    }

    /**
     * @return true if request was successful
     */
    public boolean isSuccessful(){
        return mErrorCode == ERROR_NO;
    }

    /**
     * Error code, can be
     * <ul>
     *     <li>{@link #ERROR_NO} &ndash; no error</li>
     *     <li>{@link #ERROR_AUTH} &ndash; problem with auth ( in most cases that means that access token need to be refreshed )</li>
     *     <li>{@link #ERROR_NO_INTERNET} &ndash; problem with network connection</li>
     *     <li>{@link #ERROR_UNKNOWN} &ndash; Unknown error</li>
     * </ul>
     * @return int code
     */
    public int getErrorCode() {
        return mErrorCode;
    }

    /**
     * Exception, that caused request failure ( for example no network, or error when parsing entity data)
     * @return Exception
     */
    public Exception getCause() {
        return mCause;
    }

    /**
     * Error message, if applicable
     * @return String message
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Response data
     * @return data
     */
    public T getData() {
        return mData;
    }

    public String getStringBody() {
        return mStringBody;
    }

    public void setStringBody(String mStringBody) {
        this.mStringBody = mStringBody;
    }

    /**
     * Response headers
     * @return response headers
     */
    public Map<String, List<String>> getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Map<String, List<String>> mHeaders) {
        this.mHeaders = mHeaders;
    }
}
