package com.quantimodo.android.sdk.model;

/*
{
    "id": 1244,
    "name": "2C-E",
    "originalName": "2C-E",
    "category": "Medications",
    "unit": "mg",
    "sources": "Github,Med Helper",
    "minimumValue": 0,
    "maximumValue": "Infinity",
    "combinationOperation": "MEAN",
    "fillingValue": null,
    "joinWith": null,
    "joinedVariables": [],
    "onsetDelay": 0,
    "durationOfAction": 86400
}
*/

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.quantimodo.android.sdk.SdkDefs;

import java.io.Serializable;
import java.util.Date;

public class Variable implements Serializable {
    public static final String COMBINE_SUM = SdkDefs.COMBINE_SUM;
    public static final String COMBINE_MEAN = SdkDefs.COMBINE_MEAN;

    @Deprecated
    public final long id;
    @Deprecated
    public final String name;
    @Deprecated
    public final String originalName;
    @Deprecated
    public final String parent;
    @Deprecated
    public final String category;

    @Deprecated
    @SerializedName("abbreviatedUnitName")
    public final String defaultAbbreviatedUnitName;
    @Deprecated
    public final String combinationOperation;

    @Deprecated
    public Date updated;
    @Deprecated
    public Date latestMeasurementTime;

    private Double mostCommonValue;
    private String mostCommonUnit;

    private String lastUnit;
    private Double lastValue;

    public Variable(long id, String originalName, String parent, String category, String defaultAbbreviatedUnitName, String combinationOperation) {
        this.id = id;
        this.name = originalName;
        this.originalName = originalName;
        this.parent = parent;
        this.category = category;
        this.defaultAbbreviatedUnitName = defaultAbbreviatedUnitName;
        if (combinationOperation.equals(COMBINE_SUM) || combinationOperation.equals(COMBINE_MEAN)) {
            this.combinationOperation = combinationOperation;
        } else {
            throw new IllegalArgumentException("combinationOperation must be " + COMBINE_SUM + " or " + COMBINE_MEAN);
        }
    }

    public Variable(long id, String name, String originalName, String parent, String category, String defaultAbbreviatedUnitName, String combinationOperation) {
        this.id = id;
        this.name = name;
        this.originalName = originalName;
        this.parent = parent;
        this.category = category;
        this.defaultAbbreviatedUnitName = defaultAbbreviatedUnitName;
        if (combinationOperation.equals(COMBINE_SUM) || combinationOperation.equals(COMBINE_MEAN)) {
            this.combinationOperation = combinationOperation;
        } else {
            throw new IllegalArgumentException("combinationOperation must be " + COMBINE_SUM + " or " + COMBINE_MEAN);
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getParent() {
        return parent;
    }

    public String getCategory() {
        return category;
    }

    public String getDefaultAbbreviatedUnitName() {
        return defaultAbbreviatedUnitName;
    }

    public String getCombinationOperation() {
        return combinationOperation;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getLatestMeasurementTime() {
        return latestMeasurementTime;
    }

    public void setLatestMeasurementTime(Date latestMeasurementTime) {
        this.latestMeasurementTime = latestMeasurementTime;
    }

    public Double getDefaultValue(){
        if (lastValue != null){
            return lastValue;
        } else {
            return mostCommonValue;
        }
    }

    public Double getMostCommonValue() {
        return mostCommonValue;
    }

    public String getMostCommonUnit() {
        return mostCommonUnit;
    }

    public String getLastUnit() {
        return lastUnit;
    }

    public Double getLastValue() {
        return lastValue;
    }
}
