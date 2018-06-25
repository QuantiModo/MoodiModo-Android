package com.quantimodo.android.sdk.model;

/*
{
    "name": "Kilometers",
    "abbreviatedName": "km",
    "category": "Distance",
    "minimum": "-Infinity",
    "maximum": "Infinity",
    "conversionSteps": [
        {
            "operation": "MULTIPLY",
            "value": 1000
        }
    ]
}
*/

import com.quantimodo.android.sdk.SdkDefs;

import java.util.ArrayList;

public class Unit {
    public static final String COMBINE_SUM = SdkDefs.COMBINE_SUM;
    public static final String COMBINE_MEAN = SdkDefs.COMBINE_MEAN;

    private final int id;
    @Deprecated
    public final String name;
    @Deprecated
    public final String abbreviatedName;
    @Deprecated
    public final String category;
    @Deprecated
    public final String minimum;
    @Deprecated
    public final String maximum;
    @Deprecated
    public final ArrayList<ConversionStep> conversionSteps;

    public Unit(int id, String name, String abbreviatedName, String category, String minimum,
                String maximum, ArrayList<ConversionStep> conversionSteps) {
        this.id = id;
        this.name = name;
        this.abbreviatedName = abbreviatedName;
        this.category = category;
        this.minimum = minimum;
        this.maximum = maximum;
        this.conversionSteps = conversionSteps;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviatedName() {
        return abbreviatedName;
    }

    public String getCategory() {
        return category;
    }

    public String getMinimum() {
        return minimum;
    }

    public String getMaximum() {
        return maximum;
    }

    public ArrayList<ConversionStep> getConversionSteps() {
        return conversionSteps;
    }

    public int getId() {
        return id;
    }
}
