package com.moodimodo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.widget.ListView;
import com.moodimodo.activities.HistoryActivity;
import com.moodimodo.activities.MainActivity;
import com.moodimodo.sync.SyncService;
import com.moodimodo.testhelpers.TestHelper;
import com.moodimodo.testhelpers.TestModule;
import com.quantimodo.sdk.testing.utils.Utils;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.adapters.DrawerAdapter;
import com.quantimodo.tools.events.SyncFinished;
import com.quantimodo.tools.events.SyncStarted;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.models.MeasurementDao;
import com.quantimodo.tools.sync.SyncHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Date;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static espresso.QMatchers.withChildName;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class HistoryActivityTest extends InstrumentationTestCase {

    @Inject
    DaoSession mSession;


    /***
     * Flag to know when a sync operation just started
     */
    private boolean syncStarted = false;

    /***
     * Flag to know when a sync operation just finishes
     */
    private boolean syncEnd = false;
    /***
     * Flag to know when a sync operation was successful or not after finishes
     */
    private boolean syncSuccessful = false;
    private long timestamp = Long.MAX_VALUE;

    public HistoryActivityTest() {
        QTools.getInstance().inject(this);
    }

    @BeforeClass
    public static void main() throws Exception{
        TestModule.addToGraph();
    }

    @Before
    public void setup(){
        Global.welcomeCompleted = true;
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
//        Utils.unlockScreen(getActivity(),getInstrumentation());
        QTools.getInstance().register(this);
        timestamp = System.currentTimeMillis() / 1000;
        clean(timestamp - 70);

        final SharedPreferences sharedPref = getInstrumentation()
                .getTargetContext()
                .getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean("entered_first_time", false).apply();
    }

    private void clean(long timestamp){
        mSession.getMeasurementDao()
                .queryBuilder()
                .where(MeasurementDao.Properties.Timestamp.ge(new Date(timestamp * 1000)))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    @After
    public void tearDown() throws Exception{
        clean(timestamp);
        Utils.closeAllActivities(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testDeleteMood() throws Exception {
        Instrumentation instrumentation = getInstrumentation();

        // Register we are interested in MainActivity
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(MainActivity.class.getName(), null, false);

        // Start the MainActivity as the first activity
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), MainActivity.class.getName());
        instrumentation.startActivitySync(intent);

        // Makes sure the Activity starts
        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        assertThat(currentActivity, is(notNullValue()));

        syncEnd = false;
        //Login in
        TestHelper.logInProccess(currentActivity);

        //Wait till sync ends
        Utils.waitForCondition(new Utils.Condition() {
            @Override
            public boolean check() {
                return syncEnd && syncStarted;
            }
        }, 60000 * 4);

        //Click on happiest mood face
        onView(withId(R.id.btEcstatic)).perform(scrollTo(), click());
        Thread.sleep(3000);

        currentActivity = openHistoryFromMain(instrumentation, monitor, currentActivity);

        ListView listView = (ListView)currentActivity.findViewById(android.R.id.list);
        Object item = listView.getItemAtPosition(0);
        //perform click the first element of the list
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());

        //click on delete button
        onView(withText(currentActivity.getString(R.string.fragment_mood_edit_dialog_delete_button))).perform(click());
        //click on ok button to confirm deletion
        onView(withText(currentActivity.getString(android.R.string.ok))).perform(click());

        Thread.sleep(5000);

        if(listView.getCount() > 0) {
            Object current = listView.getItemAtPosition(0);
            assertNotSame(item, current);
        }
    }

    @Test
    public void testUpdateNote() throws Exception{

        Instrumentation instrumentation = getInstrumentation();

        // Register we are interested in MainActivity
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(MainActivity.class.getName(), null, false);

        // Start the MainActivity as the first activity
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), MainActivity.class.getName());
        instrumentation.startActivitySync(intent);

        // Makes sure the Activity starts
        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        assertThat(currentActivity, is(notNullValue()));

        syncEnd = false;
        //Login in
        TestHelper.logInProccess(currentActivity);

        //Click on happiest mood face
        onView(withId(R.id.btEcstatic)).perform(scrollTo(), click());
        Thread.sleep(3000);

        currentActivity = openHistoryFromMain(instrumentation, monitor, currentActivity);

        //Wait till any other sync process ends before testing
        Utils.waitForCondition(new Utils.Condition() {
            @Override
            public boolean check() {
                return syncEnd && syncStarted;
            }
        }, 60000 * 4);
        //perform click the first element of the list
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());

        //write a test text as note and submit it
        final String note = "Testing note " + String.valueOf(System.currentTimeMillis() / 1000);
        onView(withId(R.id.etNote)).perform(click(), clearText(), typeText(note), ViewActions.closeSoftKeyboard());
        onView(withText(currentActivity.getString(R.string.fragment_mood_edit_dialog_positive_button))).perform(click());

        syncEnd = false;
        SyncHelper.invokeFullSync(currentActivity, false);
        //Wait till sync ends
        Utils.waitForCondition(new Utils.Condition() {
            @Override
            public boolean check() {
                return syncEnd && syncStarted;
            }
        }, 60000 * 4);

        pressBack();

        Thread.sleep(3000);

        openHistoryFromMain(instrumentation, monitor, currentActivity);

        //opens the first element on the list again and check if the item has the note that was previously made
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());
        onView(withId(R.id.etNote)).check(ViewAssertions.matches(ViewMatchers.withText(note)));

    }

    private Activity openHistoryFromMain(
            Instrumentation instrumentation,
            Instrumentation.ActivityMonitor monitor,
            Activity currentActivity){

        instrumentation.removeMonitor(monitor);
        //we expect to open HistoryActivity so it's being monitored
        monitor = instrumentation.addMonitor(HistoryActivity.class.getName(), null, false);

        //Opening History Activity from drawer, if run several times, group could be already open
        try {
            onData(allOf(is(instanceOf(DrawerAdapter.DrawerItem.class)),
                    withChildName("Overall Mood")))
                    .inAdapterView(withId(R.id.items_list))
                    .perform(click());
        } catch (Exception ex){
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
            onData(allOf(is(instanceOf(DrawerAdapter.DrawerItem.class)),
                    withChildName(currentActivity.getString(R.string.drawer_item_history))))
                    .inAdapterView(withId(R.id.items_list))
                    .perform(click());

            onData(allOf(is(instanceOf(DrawerAdapter.DrawerItem.class)),
                    withChildName("Overall Mood")))
                    .inAdapterView(withId(R.id.items_list))
                    .perform(click());
        }




        // Wait for HistoryActivity starts
        currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        assertThat(currentActivity, is(notNullValue()));

        return currentActivity;
    }

    public void onEvent(SyncStarted syncStarted){
        this.syncStarted = true;
    }

    public void onEvent(SyncFinished syncFinished){
        syncEnd = true;
        syncSuccessful = syncFinished.isSuccessful;
    }

    public String getClassName(Class activity) {
        final String pkg = activity.getPackage().getName();
        final String cls = activity.getName();
        int packageLen = pkg.length();
        if (!cls.startsWith(pkg) || cls.length() <= packageLen
                || cls.charAt(packageLen) != '.') {
            return cls;
        }
        return cls.substring(packageLen+1);
    }
}
