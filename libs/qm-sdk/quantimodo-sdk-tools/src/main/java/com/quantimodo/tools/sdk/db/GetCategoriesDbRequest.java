package com.quantimodo.tools.sdk.db;

import com.quantimodo.tools.models.Category;

import java.util.List;

/**
 * Load categories from database
 */
public class GetCategoriesDbRequest extends DbRequest<GetCategoriesDbRequest.Result> {

    public GetCategoriesDbRequest() {
        super(Result.class);
    }

    @Override
    public GetCategoriesDbRequest.Result loadDataFromNetwork() throws Exception {
        return new Result(getDaoSession().getCategoryDao().loadAll());
    }

    public static class Result{
        public final List<Category> categories;

        public Result(List<Category> categories) {
            this.categories = categories;
        }
    }
}
