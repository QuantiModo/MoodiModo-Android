package com.quantimodo.android.sdk.model;

/*
{
    {
        "operation": "MULTIPLY",
        "value": 1000
    }
}
*/

public class ConversionStep {
    public static final String OPERATION_MULTIPLY = "MULTIPLY";
    public static final String OPERATION_SUM = "SUM";

    @Deprecated
    public final String operation;
    @Deprecated
    public final double value;

    public ConversionStep(String operation, double value) {
        this.operation = operation;
        this.value = value;
    }

    public String getOperation() {
        return operation;
    }

    public double getValue() {
        return value;
    }
}
