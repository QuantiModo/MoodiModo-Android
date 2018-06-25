package com.quantimodo.tools.sdk.db;

import com.quantimodo.tools.models.Measurement;

/**
 */
public class SubmitMeasurementDbRequest extends DbRequest<Boolean> {

    private final Measurement measurement;

    public SubmitMeasurementDbRequest(Measurement measurement) {
        super(Boolean.class);
        this.measurement = measurement;
    }

    @Override
    public Boolean loadDataFromNetwork() throws Exception {
        long result = getDaoSession().getMeasurementDao().insert(measurement);
        return result > 0 ? true : false;
    }
}
