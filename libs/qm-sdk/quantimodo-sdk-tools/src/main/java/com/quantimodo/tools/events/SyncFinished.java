package com.quantimodo.tools.events;

public class SyncFinished {
    public final boolean isSuccessful;

    public SyncFinished(boolean isSuccesful) {
        this.isSuccessful = isSuccesful;
    }
}
