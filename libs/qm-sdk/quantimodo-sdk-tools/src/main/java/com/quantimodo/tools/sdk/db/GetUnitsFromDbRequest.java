package com.quantimodo.tools.sdk.db;


import com.quantimodo.tools.models.Unit;

import java.util.List;

/**
 * Load units from database
 */
public class GetUnitsFromDbRequest extends DbRequest<GetUnitsFromDbRequest.Result> {

    public GetUnitsFromDbRequest(Class<Result> clazz) {
        super(Result.class);
    }

    @Override
    public GetUnitsFromDbRequest.Result loadDataFromNetwork() throws Exception {
        return new Result(getDaoSession().getUnitDao().loadAll());
    }

    public static class Result{
        public final List<Unit> units;

        public Result(List<Unit> units) {
            this.units = units;
        }
    }
}
