package com.quantimodo.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.web.sugar.Web;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.quantimodo.sdk.testing.utils.Utils;
import com.quantimodo.tools.activities.QuantimodoWebAuthenticatorActivity;
import com.quantimodo.tools.events.SyncFinished;
import com.quantimodo.tools.events.SyncStarted;
import com.quantimodo.tools.models.*;
import com.quantimodo.tools.sync.SyncService;
import com.quantimodo.tools.testhelpers.TestHelper;
import com.quantimodo.tools.testhelpers.TestModule;
import com.quantimodo.tools.testhelpers.TestSyncService;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.List;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webKeys;
import static android.support.test.espresso.web.webdriver.Locator.CLASS_NAME;
import static android.support.test.espresso.web.webdriver.Locator.ID;
import static android.support.test.espresso.web.webdriver.Locator.NAME;
import static com.quantimodo.tools.sync.SyncService.SYNC_FROM_SCRATCH_KEY;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SyncServiceTest {

    private boolean syncInProgress = false;
    private boolean syncStarted = false;

    @Inject
    DaoSession session;

    @BeforeClass
    public static void main() throws Exception{
        TestModule.addToGraph();
    }

    @Before
    public void setup(){
        QTools.getInstance().inject(this);
        session.getMeasurementDao().deleteAll();
        session.getVariableDao().deleteAll();
        session.getUnitDao().deleteAll();
        session.getCategoryDao().deleteAll();

        SharedPreferences prefs = InstrumentationRegistry.getContext().getSharedPreferences(ToolsPrefs.QUANTIMODO_PREF_KEY, Context.MODE_PRIVATE);
        prefs.edit().remove(SyncService.LAST_META_SYNC).apply();

        QTools.getInstance().register(this);
    }

    @Test
    public void testSync() throws Exception {
        logIn();
        Intent intent = new Intent(InstrumentationRegistry.getContext(), TestSyncService.class);
        intent.putExtra(SYNC_FROM_SCRATCH_KEY, false);
        InstrumentationRegistry.getContext().startService(intent);

        Utils.waitForCondition(new Utils.Condition() {
            @Override
            public boolean check() {
                return !syncInProgress && syncStarted;
            }
        }, 60000 * 4);

        assertTrue(session.getCategoryDao().count() > 0);
        assertTrue(session.getUnitDao().count() > 0);
        assertTrue(session.getVariableDao().count() > 0);

        VariableDao variableDao = session.getVariableDao();
        List<Variable> variables = variableDao.loadAll();
        Variable variable = variables.get(0);

        assertNotNull(variable.getCategory());
        assertNotNull(variable.getUnit());
    }

    public void onEvent(SyncStarted syncStarted){
        syncInProgress = true;
        this.syncStarted = true;
    }

    public void onEvent(SyncFinished syncFinished){
        syncInProgress = false;
    }

    @After
    public void down(){
        QTools.getInstance().unregister(this);
    }

    private void logIn() throws InterruptedException {
        Intent intent = new Intent(InstrumentationRegistry.getContext(), QuantimodoWebAuthenticatorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        InstrumentationRegistry.getContext().startActivity(intent);

        Web.WebInteraction wi = Web.onWebView(withId(R.id.web));

        Thread.sleep(5 * 1000);

        wi
                .withElement(findElement(NAME, "user_login")).perform(webKeys(TestHelper.TEST_USERNAME))
                .withElement(findElement(NAME,"user_pass")).perform(webKeys(TestHelper.TEST_PASSWORD))
                .withElement(findElement(CLASS_NAME, "btn-primary")).perform(webClick());

        //Wait sometime until page is loaded
        Thread.sleep(15 * 1000);
        Log.d("WebAuthActivityTest", "content: " + wi.toString());
        String text = wi.withElement(findElement(ID,"request-heading")).perform(getText()).get().toString();
        assertTrue(text.contains("would like to"));
        wi.withElement(findElement(ID,"button-approve")).perform(webClick());

        Thread.sleep(15 * 1000);
    }

}
