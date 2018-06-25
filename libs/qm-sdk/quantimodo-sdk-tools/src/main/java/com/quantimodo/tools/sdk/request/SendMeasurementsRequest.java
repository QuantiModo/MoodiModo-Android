package com.quantimodo.tools.sdk.request;

import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.MeasurementSet;
import com.quantimodo.android.sdk.model.Variable;

import java.util.ArrayList;

/**
 * Send measurements to Quantimodo WebService
 */
public class SendMeasurementsRequest extends SdkRequest<Boolean> {

    private final Variable variable;
    private final ArrayList<MeasurementSet> measurements;

    public SendMeasurementsRequest(Variable variable, ArrayList<MeasurementSet> measurements) {
        super(Boolean.class);
        this.variable = variable;
        this.measurements = measurements;
    }

    @Override
    public Boolean loadDataFromNetwork() throws Exception {
        // Send measurements to QM, add to history
        SdkResponse<Integer> response = getClient().putMeasurements(getCtx(), getToken(), measurements);
        checkResponse(response);
        return true;
    }
}
