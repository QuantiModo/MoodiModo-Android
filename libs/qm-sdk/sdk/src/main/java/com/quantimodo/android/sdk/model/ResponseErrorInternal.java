package com.quantimodo.android.sdk.model;

public class ResponseErrorInternal {
    @Deprecated
    final String message;

    public ResponseErrorInternal(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
