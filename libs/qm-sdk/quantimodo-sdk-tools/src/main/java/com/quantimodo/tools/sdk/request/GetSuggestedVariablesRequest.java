package com.quantimodo.tools.sdk.request;

import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.ToolsPrefs;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Get suggested variables from search box
 * Would sort them by latest measurement time DESC
 * If now search string is provided, would search for Variables with source equals current application source
 */
public class GetSuggestedVariablesRequest extends SdkRequest<GetSuggestedVariablesRequest.GetSuggestedVariablesResponse> {

    @Inject
    ToolsPrefs prefs;

    private final String search;
    private final String category;
    private final int limit;

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
    public GetSuggestedVariablesRequest(String search) {
        super(GetSuggestedVariablesResponse.class);
        this.search = search;
        category = null;
        this.limit = 5;
    }

    /**
     * @param search search string for variable
     * @param category category in which variable should be searched
     */
    public GetSuggestedVariablesRequest(String search, String category) {
        super(GetSuggestedVariablesResponse.class);
        this.search = search;
        this.category = category;
        this.limit = 10;
    }

    public GetSuggestedVariablesRequest(String search, String category, int limit) {
        super(GetSuggestedVariablesResponse.class);
        this.search = search;
        this.category = category;
        this.limit = limit;
    }

    @Override
    public GetSuggestedVariablesResponse loadDataFromNetwork() throws Exception {
        final ArrayList<Variable> newVariables;


        if (search != null && search.length() > 0)     // If there's a search query available we use the QM autocomplete api
        {
            newVariables = getQmSuggestedVariables(search, limit, false);
        } else                                        // Otherwise we populate the autocomplete with variables from the user's history
        {
            newVariables = getQmSuggestedVariables("", 10, true);
        }

        Collections.sort(newVariables, COMPARATOR);

        return new GetSuggestedVariablesResponse(newVariables);
    }

    private ArrayList<Variable> getQmSuggestedVariables(String search, int limit, boolean filtered) throws NoNetworkConnection {
        SdkResponse<ArrayList<Variable>> variableSdkResponse;
        if (filtered) {
            variableSdkResponse = getClient().searchVariables(getCtx(), getToken(), search, limit, 0,
                    prefs.getApplicationSource(), category, false);
        } else {
            variableSdkResponse = getClient().searchVariables(getCtx(), getToken(), search, limit, 0, null, category);
        }

        try {
            checkResponse(variableSdkResponse);
            return variableSdkResponse.getData() == null ? new ArrayList<Variable>() : variableSdkResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static final class GetSuggestedVariablesResponse{
        public final ArrayList<Variable> variables;

        public GetSuggestedVariablesResponse(ArrayList<Variable> variables) {
            this.variables = variables;
        }
    }
}
