package com.quantimodo.android.sdk.model;

public class Measurement {
    @Deprecated
    public long timestamp;
    @Deprecated
    public double value;
    @Deprecated
    public int duration;

    public String note;

    public int id;

    private Measurement() {
    }

    public Measurement(long epochTimestamp, double value, int durationSeconds) {
        this.timestamp = epochTimestamp;
        this.value = value;
        this.duration = durationSeconds;
    }

    public Measurement(long epochTimestamp, double value) {
        this.timestamp = epochTimestamp;
        this.value = value;
        this.duration = -1;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
