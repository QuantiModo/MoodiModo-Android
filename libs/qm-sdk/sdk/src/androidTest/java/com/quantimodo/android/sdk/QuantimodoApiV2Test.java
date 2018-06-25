package com.quantimodo.android.sdk;


import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;
import com.quantimodo.android.sdk.model.*;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

public class QuantimodoApiV2Test extends AndroidTestCase{

    private String token;
    private QuantimodoApiV2 instance;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setContext(InstrumentationRegistry.getContext());
        if (token == null){
            token = TestHelper.logIn(InstrumentationRegistry.getContext());
        }

        instance = QuantimodoApiV2.getInstance(TestHelper.QUANTIMODO_ADDRESS,token);
    }

    public void testErrorNoAuth() throws Exception{
        SdkResponse<QuantimodoUser> response = instance.getUser(getContext(),null);
        assertFalse(response.isSuccessful());
        assertEquals(SdkResponse.ERROR_AUTH,response.getErrorCode());
    }

    public void testGetMeasurmentHistory() throws Exception {
        SdkResponse<ArrayList<Variable>> varResponse = instance.searchVariables(getContext(),token,"*",1,0,null,null, false);

        SdkResponse<ArrayList<HistoryMeasurement>> response = instance.getMeasurmentHistory(getContext(), token, null, null, varResponse.getData().get(0).name, null, null);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getData());
        assertTrue(response.getData().size() > 0);
    }


    public void testPutMeasurements() throws Exception {
        Measurement measurement = new Measurement(System.currentTimeMillis()/1000 - 1,1.0);
        ArrayList<Measurement> measurements = new ArrayList<>();
        measurements.add(measurement);
        MeasurementSet measurementSet = new MeasurementSet("Overall Mood",null,"Emotions","/5","MEAN","QuantiModo",measurements);
        List<MeasurementSet> measurementSets = new ArrayList<>();
        measurementSets.add(measurementSet);
        SdkResponse<Integer> response = instance.putMeasurements(getContext(), token, measurementSets);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getData());
        assertEquals(Integer.valueOf(1), response.getData());
    }

    public void testSearchVariables() throws Exception {
        SdkResponse<ArrayList<Variable>> response = instance.searchVariables(getContext(), token,
                "*", 1, 0, null, null, false);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getData());
        assertTrue(response.getData().size() == 1);
    }


    public void testGetUnits() throws Exception {
        SdkResponse<ArrayList<Unit>> response = instance.getUnits(getContext(), token);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getData());
        assertTrue(response.getData().size() > 0);
    }

    public void testGetCategories() throws Exception {
        SdkResponse<ArrayList<VariableCategory>> response = instance.getCategories(getContext(), token);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getData());
        assertTrue(response.getData().size() > 0);
    }


    public void testGetVariables() throws Exception {
        SdkResponse<ArrayList<Variable>> response = instance.getVariables(getContext(), token);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getData());
        assertTrue(response.getData().size() > 0);
    }


    public void testGetUser() throws Exception {
        SdkResponse<QuantimodoUser> response = instance.getUser(getContext(), token);
        assertTrue(response.isSuccessful());
        QuantimodoUser user = response.getData();
        assertEquals(user.loginName, TestHelper.TEST_USERNAME);
    }

    public void testSearchCorrelation() throws Exception {
        SdkResponse<ArrayList<Correlation>> response = instance.searchCorrelations(getContext(), token, "Overall Mood", SdkDefs.CORRELATION_EFFECT);
        assertTrue(response.isSuccessful());
        ArrayList<Correlation> correlations = response.getData();
        assertTrue(correlations.size() > 0);
    }

    public void testPostCorrelation() throws Exception {
        SdkResponse<ArrayList<Correlation>> response = instance.searchCorrelations(getContext(), token, "Overall Mood", SdkDefs.CORRELATION_EFFECT);
        ArrayList<Correlation> correlations = response.getData();
        if (!response.isSuccessful()){
            throw response.getCause();
        }
        Correlation one = correlations.get(0);

        CorrelationPost correlationPost = new CorrelationPost(one.getCause(),one.getEffect(),one.getCorrelationCoefficient(),CorrelationPost.VOTE_UP);
        SdkResponse<Void> r = instance.postCorrelation(getContext(), token, correlationPost);
        assertTrue("Request error: " + r.getMessage(),r.isSuccessful());
    }

    public void testVoteCorrelation() throws Exception {
        SdkResponse<ArrayList<Correlation>> response = instance.searchCorrelations(getContext(), token, "Overall Mood", SdkDefs.CORRELATION_EFFECT);
        ArrayList<Correlation> correlations = response.getData();
        if (!response.isSuccessful()){
            throw response.getCause();
        }
        Correlation one = correlations.get(0);

        CorrelationPost correlationPost = new CorrelationPost(one.getCause(),one.getEffect(),one.getCorrelationCoefficient(),CorrelationPost.VOTE_UP);
        SdkResponse<Void> r = instance.voteCorrelation(getContext(),token,correlationPost);
        assertTrue("Request error: " + r.getMessage(),r.isSuccessful());
    }
}