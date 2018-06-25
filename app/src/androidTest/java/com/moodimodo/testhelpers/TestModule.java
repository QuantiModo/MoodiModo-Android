package com.moodimodo.testhelpers;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.moodimodo.HistoryActivityTest;
import com.moodimodo.MainActivityTest;
import com.moodimodo.TrackingFragmentTest;
import com.moodimodo.activities.MainActivity;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.tools.QApplication;
import com.quantimodo.tools.QBaseApplication;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.activities.QuantimodoWebAuthenticatorActivity;
import com.quantimodo.tools.fragments.ImportWebFragment;
import com.quantimodo.tools.fragments.TrackingFragment;
import com.quantimodo.tools.models.DaoMaster;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.*;

import dagger.Module;
import dagger.Provides;

import java.util.concurrent.ExecutionException;

@Module(
        injects = {
                QuantimodoWebAuthenticatorActivity.class,
                TestService.class,
                MainActivityTest.class,
                MainActivity.class,

                ImportWebFragment.class,
                TrackingFragment.class,

                //Requests
                SearchCorrelationsRequest.class,
                VoteCorrelationRequest.class,
                GetUnitsRequest.class,
                GetCategoriesRequest.class,
                GetSuggestedVariablesRequest.class,
                SendMeasurementsRequest.class,

                //Tests
                TrackingFragmentTest.class,
                HistoryActivityTest.class
        }
)
public class TestModule {

    public synchronized static void addToGraph() throws Exception{
        if (!added) {
            QApplication application = QTools.getInstance();
//            ToolsPrefs prefs = application.getInstanceOf(ToolsPrefs.class);
            ToolsPrefs prefs = new ToolsPrefs(TestHelper.QUANTIMODO_ADDRESS,"readmeasurements writemeasurements","QuantimodoTest", TestHelper.QUANTIMODO_AUTH_ADDRESS);

            TestModule module = new TestModule((QBaseApplication) application, prefs);
            application.addToGraph(module);
            added = true;
        }
    }

    public static boolean added = false;

    //Application context
    private Context ctx;
    private String token;
    private QuantimodoApiV2 client;
    private ToolsPrefs mPrefs;
    private AuthHelper mAuthHelper;
    private DaoSession mDaoSession;

    public TestModule(Context applicationContext, ToolsPrefs prefs) throws ExecutionException, InterruptedException {
        ctx = applicationContext;
        client = QuantimodoApiV2.getInstance(prefs.getApiUrl(),null);
        mPrefs = prefs;
        mAuthHelper = new TestAuthHelper(applicationContext,prefs);
        token = "";
        SQLiteOpenHelper helper = new DaoMaster.DevOpenHelper(ctx,"db",null);
        DaoMaster master = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = master.newSession();
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
    public DaoSession getDaoSession(){
        return mDaoSession;
    }
}
