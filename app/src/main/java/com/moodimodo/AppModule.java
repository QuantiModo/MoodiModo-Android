package com.moodimodo;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.moodimodo.activities.HistoryActivity;
import com.moodimodo.activities.MainActivity;
import com.moodimodo.activities.SettingsActivity;
import com.moodimodo.databases.OpenHelper;
import com.moodimodo.fragments.AllMeasurementsFragment;
import com.moodimodo.fragments.InboxWebFragment;
import com.moodimodo.fragments.VariableFragment;
import com.moodimodo.fragments.WelcomeFragment;
import com.moodimodo.receivers.MoodResultReceiver;
import com.moodimodo.sdk.MAuthHelper;
import com.moodimodo.sync.MigrationService;
import com.moodimodo.sync.SyncService;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.tools.ToolsPrefs;
import com.quantimodo.tools.activities.CustomRemindersCreateActivity;
import com.quantimodo.tools.activities.QuantimodoLoginActivity;
import com.quantimodo.tools.activities.QuantimodoWebAuthenticatorActivity;
import com.quantimodo.tools.activities.QuantimodoWebValidatorActivity;
import com.quantimodo.tools.dialogs.CustomReminderDialog;
import com.quantimodo.tools.fragments.FactorsFragment;
import com.quantimodo.tools.fragments.ImportWebFragment;
import com.quantimodo.tools.fragments.QuantimodoWebFragment;
import com.quantimodo.tools.fragments.TrackingFragment;
import com.quantimodo.tools.models.DaoMaster;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.receivers.CustomRemindersReceiver;
import com.quantimodo.tools.receivers.RemindersService;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.sdk.request.*;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MainActivity.class,
                QuantimodoWebAuthenticatorActivity.class,
                QuantimodoWebValidatorActivity.class,
                QuantimodoLoginActivity.class,
                SettingsActivity.class,
                SyncService.class,
                HistoryActivity.class,
                MoodResultReceiver.class,
                SyncService.class,
                MigrationService.class,
                CustomRemindersReceiver.class,
                CustomReminderDialog.class,
                CustomRemindersCreateActivity.class,
                RemindersService.class,

                //Fragments
                WelcomeFragment.class,
                ImportWebFragment.class,
                InboxWebFragment.class,
                TrackingFragment.class,
                QuantimodoWebFragment.class,
                VariableFragment.class,
                FactorsFragment.class,
                AllMeasurementsFragment.class,

                //Requests
                SearchCorrelationsRequest.class,
                SearchCustomCorrelationsRequest.class,
                VoteCorrelationRequest.class,
                GetUnitsRequest.class,
                GetCategoriesRequest.class,
                GetSuggestedVariablesRequest.class,
                GetPublicSuggestedVariablesRequest.class,
                SendMeasurementsRequest.class,

                ToolsPrefs.class,
                ConfigModule.class
        },
        includes = {
                ConfigModule.class
        }
)
public class AppModule{

    private Context mContext;
    private QuantimodoApiV2 mClient;
    private AuthHelper mAuthHelper;
    private ToolsPrefs mPrefs;
    private DaoSession mDaoSession;

    public AppModule(Context ctx,ToolsPrefs prefs) {
        mContext = ctx.getApplicationContext();
        mPrefs = prefs;
        mAuthHelper = new MAuthHelper(mContext,prefs);
        mClient = QuantimodoApiV2.getInstance(BuildConfig.API_HOST);
        SQLiteOpenHelper helper = new OpenHelper(ctx,Global.DATABASE_NAME,null);
        DaoMaster master = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = master.newSession();
    }

    @Provides
    public Context getCtx(){
        return mContext;
    }

    public String getToken(){
        return mAuthHelper.getAuthToken();
    }

    @Provides
    public AuthHelper getAuthHelper() {
        return mAuthHelper;
    }

    @Provides
    public QuantimodoApiV2 getClient() {
        return mClient;
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
