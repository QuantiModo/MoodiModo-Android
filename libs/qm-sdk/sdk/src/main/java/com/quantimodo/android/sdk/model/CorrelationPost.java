package com.quantimodo.android.sdk.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CorrelationPost {

    public static final int VOTE_UP = 1;
    public static final int VOTE_DOWN = 2;

    @IntDef({VOTE_UP,VOTE_DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Vote{}

    String cause;
    String effect;
    double correlation;
    int vote;

    public CorrelationPost() {
    }

    public CorrelationPost(String cause, String effect, double correlation,@Vote int vote) {
        this.cause = cause;
        this.effect = effect;
        this.correlation = correlation;
        this.vote = vote;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(@Vote int vote) {
        this.vote = vote;
    }
}
