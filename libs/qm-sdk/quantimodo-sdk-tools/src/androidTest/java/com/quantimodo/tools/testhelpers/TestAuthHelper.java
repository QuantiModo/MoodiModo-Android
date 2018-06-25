package com.quantimodo.tools.testhelpers;

import android.content.Context;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.sdk.AuthHelper;

import java.util.concurrent.ExecutionException;

public class TestAuthHelper extends AuthHelper {

    private final String token;

    public TestAuthHelper(Context ctx, ToolsPrefs prefs) throws ExecutionException, InterruptedException {
        super(ctx, prefs);
        token = TestHelper.logIn(ctx);
    }

    @Override
    public String getAuthToken() {
        return token;
    }

    @Override
    public String getAuthTokenWithRefresh() {
        return token;
    }
}
