package com.quantimodo.tools.sdk.request;

import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.ToolsPrefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

/**
 * Get public suggested variables from search box
 * Would sort them by latest measurement time DESC
 * If now search string is provided, would search for Variables with source equals current application source
 */
public class GetPublicSuggestedVariablesRequest extends SdkRequest<GetPublicSuggestedVariablesRequest.Response> {

    private String search;
    private String categoryName;

    private static final Comparator<Variable> COMPARATOR = new Comparator<Variable>() {
        @Override
        public int compare(Variable variable1, Variable variable2) {
            if (variable1.getLatestMeasurementTime() != null && variable2.getLatestMeasurementTime() != null) {
                return variable2.getLatestMeasurementTime().compareTo(variable1.getLatestMeasurementTime());
            } else if (variable1.getLatestMeasurementTime() != null) {
                return -1;
            } else {
                return 1;
            }
        }
    };

    /**
     * @param search search string for variable
     */
    public GetPublicSuggestedVariablesRequest(String search, String categoryName) {
        super(Response.class);
        this.search = search;
        this.categoryName = categoryName;
    }

    @Override
    public Response loadDataFromNetwork() throws Exception {
        final ArrayList<Variable> newVariables;
        if (search == null && search.length() == 0) search = "";
        newVariables = getQmSuggestedVariables(search, 5, categoryName);
        Collections.sort(newVariables, COMPARATOR);

        return new Response(newVariables);
    }

    private ArrayList<Variable> getQmSuggestedVariables(String search, int limit, String categoryName) throws Exception {
        SdkResponse<ArrayList<Variable>> variableSdkResponse;
        variableSdkResponse = getClient()
                .searchPublicVariables(getCtx(), getToken(), search, limit, 0, categoryName);

        try {
            checkResponse(variableSdkResponse);
            return variableSdkResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static final class Response{
        public final ArrayList<Variable> variables;

        public Response(ArrayList<Variable> variables) {
            this.variables = variables;
        }
    }
}
