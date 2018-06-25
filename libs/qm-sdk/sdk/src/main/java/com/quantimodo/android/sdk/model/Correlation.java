package com.quantimodo.android.sdk.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Correlation implements Serializable {

    double correlationCoefficient;
    String causeCategory;
    String cause;
    String causeName;    //same as cause, is used for the new model
    String causeUnit;
    int causeUnitId;
    String originalCause;
    String effectCategory;
    String effect;
    String originalEffect;
    @SerializedName("onsetDelay")
    int onSetDelay;
    int durationOfAction;
    int numberOfPairs;
    long timestamp;
    Double userVote;
    String predictorExplanation;
    String valuePredictingHighOutcomeExplanation;
    String valuePredictingLowOutcomeExplanation;

    public double getCorrelationCoefficient() {
        return correlationCoefficient;
    }

    public String getCauseCategory() {
        return causeCategory;
    }

    public String getCause() {
        if(TextUtils.isEmpty(cause))
            return causeName;
        return cause;
    }

    public String getOriginalCause() {
        return originalCause;
    }

    public String getCauseUnit() {
        return causeUnit;
    }

    public int getCauseUnitId() {
        return causeUnitId;
    }

    public String getEffectCategory() {
        return effectCategory;
    }

    public String getEffect() {
        return effect;
    }

    public String getOriginalEffect() {
        return originalEffect;
    }

    public int getOnSetDelay() {
        return onSetDelay;
    }

    public int getDurationOfAction() {
        return durationOfAction;
    }

    public int getNumberOfPairs() {
        return numberOfPairs;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Double getUserVote() {
        return userVote;
    }

    public double getWeight(){
        double uv = userVote == null ? 1 : userVote;
        return correlationCoefficient * uv;
    }

    public void setUserVote(Double userVote) {
        this.userVote = userVote;
    }

    public String getPredictorExplanation() {
        return predictorExplanation;
    }

    public String getValuePredictingHighOutcomeExplanation() {
        return valuePredictingHighOutcomeExplanation;
    }

    public String getValuePredictingLowOutcomeExplanation() {
        return valuePredictingLowOutcomeExplanation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Correlation that = (Correlation) o;

        if (Double.compare(that.correlationCoefficient, correlationCoefficient) != 0) return false;
        if (onSetDelay != that.onSetDelay) return false;
        if (durationOfAction != that.durationOfAction) return false;
        if (numberOfPairs != that.numberOfPairs) return false;
        if (timestamp != that.timestamp) return false;
        if (causeCategory != null ? !causeCategory.equals(that.causeCategory) : that.causeCategory != null)
            return false;
        if (!cause.equals(that.cause)) return false;
        if(!causeName.equals(that.causeName)) return false;
        if (originalCause != null ? !originalCause.equals(that.originalCause) : that.originalCause != null)
            return false;
        if (effectCategory != null ? !effectCategory.equals(that.effectCategory) : that.effectCategory != null)
            return false;
        if (!effect.equals(that.effect)) return false;
        if (originalEffect != null ? !originalEffect.equals(that.originalEffect) : that.originalEffect != null)
            return false;
        return !(userVote != null ? !userVote.equals(that.userVote) : that.userVote != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(correlationCoefficient);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (causeCategory != null ? causeCategory.hashCode() : 0);
        result = 31 * result + cause.hashCode();
        result = 31 * result + causeName.hashCode();
        result = 31 * result + (originalCause != null ? originalCause.hashCode() : 0);
        result = 31 * result + (effectCategory != null ? effectCategory.hashCode() : 0);
        result = 31 * result + effect.hashCode();
        result = 31 * result + (originalEffect != null ? originalEffect.hashCode() : 0);
        result = 31 * result + onSetDelay;
        result = 31 * result + durationOfAction;
        result = 31 * result + numberOfPairs;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (userVote != null ? userVote.hashCode() : 0);
        return result;
    }
}


//{
//        "correlationCoefficient":0.95770001411438,
//        "cause":"vitamin b complex (pills)",
//        "originalCause":"vitamin b complex (pills)",
//        "effect":"Overall Mood",
//        "originalEffect":"Overall Mood",
//        "onsetDelay":1800,
//        "durationOfAction":432000,
//        "numberOfPairs":2219,
//        "effectSize":null,
//        "statisticalSignificance":null,
//        "timestamp":1414413587,
//        "reverseCorrelation":null,
//        "causalityFactor":null,
//        "causeCategory":"Treatments",
//        "effectCategory":"Mood",
//        "averageElevated":null,
//        "averageDepressed":null,
//        "vote":null
//        }