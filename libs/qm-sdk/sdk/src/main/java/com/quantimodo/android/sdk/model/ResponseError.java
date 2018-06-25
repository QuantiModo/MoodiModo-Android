package com.quantimodo.android.sdk.model;

public class ResponseError {
    @Deprecated
    final ResponseErrorInternal error;

    final String message;

    public ResponseError(ResponseErrorInternal error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getErrorMessage(){
        if (message != null){
            return message;
        }

        if (error != null){
            return error.getMessage();
        }
        return "";
    }

    public ResponseErrorInternal getError() {
        return error;
    }
}
