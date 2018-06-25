package com.quantimodo.android.sdk.model;

import java.util.ArrayList;

public class MeasurementSet {
    public static final String COMBINE_SUM = "SUM";
    public static final String COMBINE_MEAN = "MEAN";

    @Deprecated
    public String name;
    @Deprecated
    public String parent;
    @Deprecated
    public String category;
    @Deprecated
    public String unit;
    @Deprecated
    public String combinationOperation;
    @Deprecated
    public String source;

    @Deprecated
    public ArrayList<Measurement> measurements;

    // Do not use this constructor
    private MeasurementSet() {
    }

    public MeasurementSet(Variable variable, String source) {
        this.measurements = new ArrayList<Measurement>();

        this.name = variable.originalName;
        this.parent = variable.parent;
        this.category = variable.category;
        this.unit = variable.defaultAbbreviatedUnitName;
        this.combinationOperation = variable.combinationOperation;
        this.source = source;
    }

    public MeasurementSet(Variable variable, String source, ArrayList<Measurement> measurements) {
        this.measurements = measurements;

        this.name = variable.originalName;
        this.parent = variable.parent;
        this.category = variable.category;
        this.unit = variable.defaultAbbreviatedUnitName;
        this.combinationOperation = variable.combinationOperation;
        this.source = source;
    }

    public MeasurementSet(String name, String parent, String category, String unit, String combinationOperation, String source) {
        this.measurements = new ArrayList<Measurement>();

        this.name = name;
        this.parent = parent;
        this.category = category;
        this.unit = unit;
        this.combinationOperation = combinationOperation;
        this.source = source;
    }

    public MeasurementSet(String name, String parent, String category, String unit, String combinationOperation, String source, ArrayList<Measurement> measurements) {
        this.measurements = measurements;

        this.name = name;
        this.parent = parent;
        this.category = category;
        this.unit = unit;
        this.combinationOperation = combinationOperation;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCombinationOperation() {
        return combinationOperation;
    }

    public void setCombinationOperation(String combinationOperation) {
        this.combinationOperation = combinationOperation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ArrayList<Measurement> measurements) {
        this.measurements = measurements;
    }
}
