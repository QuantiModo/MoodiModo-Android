package com.quantimodo.tools.sdk.request;

import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.VariableCategory;
import com.quantimodo.tools.models.Category;
import com.quantimodo.tools.models.DaoSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Get categories request
 * Can be cached see {@link #getCachedSpiceRequest() getCachedSpiceRequest} method, by default cached for 2 days
 */
public class GetCategoriesRequest extends SdkRequest<GetCategoriesRequest.GetCategoriesResponse> {

    private static boolean LAST_UPDATED = false;

    @Inject
    DaoSession mDaoSession;

    public GetCategoriesRequest() {
        super(GetCategoriesResponse.class);
    }

    @Override
    public GetCategoriesResponse loadDataFromNetwork() throws Exception {
        List<Category> categories = mDaoSession.getCategoryDao().loadAll();
        ArrayList<VariableCategory> result = new ArrayList<>();
        if (categories.size() > 0 && LAST_UPDATED){
            for (Category c : categories) {
                result.add(c.toVariableCategory());
            }
        } else {
            SdkResponse<ArrayList<VariableCategory>> categorySdkResponse = getClient().getCategories(getCtx(), getToken());
            checkResponse(categorySdkResponse);
            result.addAll(categorySdkResponse.getData());
            for (VariableCategory vc : result){
                categories.add(Category.fromVariableCategory(vc));
            }
            mDaoSession.getCategoryDao().insertOrReplaceInTx(categories);
            LAST_UPDATED = true;
        }
        return new GetCategoriesResponse(result);
    }

    @Override
    protected long getCacheTime() {
        return 1; //0 Moved to database cache
    }

    @Override
    protected String getCacheKey() {
        return "getCategories";
    }

    public static final class GetCategoriesResponse{
        final public ArrayList<VariableCategory> categories;


        public GetCategoriesResponse(ArrayList<VariableCategory> categories) {
            this.categories = categories;
        }
    }
}
