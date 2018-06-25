package com.quantimodo.tools.testhelpers;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.tools.*;
import com.quantimodo.tools.activities.QuantimodoWebAuthenticatorActivity;
import com.quantimodo.tools.fragments.FactorsFragment;
import com.quantimodo.tools.fragments.ImportWebFragment;
import com.quantimodo.tools.fragments.TrackingFragment;
import com.quantimodo.tools.models.DaoMaster;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.*;
import com.quantimodo.tools.sync.SyncService;
import dagger.Module;
import dagger.Provides;

import java.util.concurrent.ExecutionException;

@Module(
        injects = {
                QuantimodoWebAuthenticatorActivity.class,
                TestService.class,
                SyncService.class,

                ImportWebFragment.class,
                TrackingFragment.class,
                FactorsFragment.class,

                //Requests
                SearchCorrelationsRequest.class,
                VoteCorrelationRequest.class,
                GetUnitsRequest.class,
                GetCategoriesRequest.class,
                GetSuggestedVariablesRequest.class,
                SendMeasurementsRequest.class,

                //Tests
                TestMainActivityTest.class,
                WebAuthActivityTest.class,
                FactorsFragmentTest.class,

                //SyncServiceTest
                SyncServiceTest.class,
                TestSyncService.class
        }
)
public class TestModule {

    private static boolean added = false;
    //Application context
    private Context ctx;
    private String token;
    private QuantimodoApiV2 client;
    private ToolsPrefs mPrefs;
    private AuthHelper mAuthHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mCurrentSession;

    public TestModule(Context applicationContext, ToolsPrefs prefs) throws ExecutionException, InterruptedException {
        ctx = applicationContext;
        client = QuantimodoApiV2.getInstance(prefs.getApiUrl(),null);
        mPrefs = prefs;
        mAuthHelper = new TestAuthHelper(applicationContext,prefs);
        token = "";
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(applicationContext,"testDb",null);
        mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        mCurrentSession = mDaoMaster.newSession();
    }

    public synchronized static void addToGraph() throws Exception{
        if (!added) {
            QApplication application = QTools.getInstance();
//            ToolsPrefs prefs = application.getInstanceOf(ToolsPrefs.class);
            ToolsPrefs prefs = new ToolsPrefs(TestHelper.QUANTIMODO_ADDRESS,"readmeasurements writemeasurements","QuantimodoTest", TestHelper.QUANTIMODO_SOCIAL_AUTH);

            TestModule module = new TestModule((QBaseApplication) application, prefs);
            application.addToGraph(module);
            added = true;
        }
    }


    @Provides
    public Context getCtx(){
        return ctx;
    }


    public String getToken(){
        return token;
    }

    @Provides
    public QuantimodoApiV2 getClient(){
        return client;
    }

    @Provides
    public AuthHelper getAuthHelper(){
        return mAuthHelper;
    }

    @Provides
    public ToolsPrefs getToolPrefs(){
        return mPrefs;
    }

    @Provides
    public DaoSession getCurrentSession() {
        return mCurrentSession;
    }

}
