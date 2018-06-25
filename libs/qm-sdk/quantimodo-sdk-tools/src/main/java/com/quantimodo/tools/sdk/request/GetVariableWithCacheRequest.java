package com.quantimodo.tools.sdk.request;

import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.*;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.models.*;
import com.quantimodo.tools.models.Variable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetVariableWithCacheRequest extends SdkRequest<GetVariableWithCacheRequest.Result>{

    @Inject
    DaoSession mDaoSession;

    @Inject
    ToolsPrefs prefs;

    private final String search;
    private final String category;

    public GetVariableWithCacheRequest(String search, String category) {
        super(Result.class);
        this.search = search;
        this.category = category;
    }

    @Override
    public Result loadDataFromNetwork() throws Exception {
        final ArrayList<Variable> newVariables;

        if (search != null && search.length() > 0)     // If there's a search query available we use the QM autocomplete api
        {
            newVariables = getQmSuggestedVariables(search, 5, false);
        } else                                        // Otherwise we populate the autocomplete with variables from the user's history
        {
            newVariables = getHistorySuggestedVariables();
        }

//        Collections.sort(newVariables, COMPARATOR);

        return new Result(newVariables);
    }

    private ArrayList<Variable> getHistorySuggestedVariables() throws Exception {
        VariableDao dao = mDaoSession.getVariableDao();

        List<Variable> vars = dao.queryBuilder()
                .limit(10)
                .orderDesc(VariableDao.Properties.LatestMeasurementTime)
                .list();

        return new ArrayList<>(vars);
    }

    private ArrayList<Variable> getQmSuggestedVariables(String search, int limit, boolean filtered) throws Exception {
        CategoryDao categoryDao = mDaoSession.getCategoryDao();
        Category cat = categoryDao.queryBuilder()
                .where(CategoryDao.Properties.Name.eq(category))
                .unique();

        if (isNetworkAvailable()) {
            SdkResponse<ArrayList<com.quantimodo.android.sdk.model.Variable>> variableSdkResponse;
            if (filtered) {
                variableSdkResponse = getClient().searchVariables(getCtx(), getToken(), search, limit, 0, prefs.getApplicationSource(), category);
            } else {
                variableSdkResponse = getClient().searchVariables(getCtx(), getToken(), search, limit, 0, null, category);
            }

            checkResponse(variableSdkResponse);

            //@TODO convert to Variable from model
        } else {
            VariableDao dao = mDaoSession.getVariableDao();
            long category = cat.getId();
            //@TODO check like condition
            List<Variable> vars = dao.queryBuilder()
                    .limit(limit)
                    .where(VariableDao.Properties.CategoryId.eq(category), VariableDao.Properties.Name.like("?" + search + "?"))
                    .list();
        }
//        return variableSdkResponse.getData();
        return null;
    }


    public static class Result{
        public final ArrayList<Variable> variables;

        public Result(ArrayList<Variable> variables) {
            this.variables = variables;
        }
    }
}
