package com.moodimodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import com.moodimodo.activities.MainActivity;
import com.moodimodo.testhelpers.TestHelper;
import com.moodimodo.testhelpers.TestModule;
import com.quantimodo.sdk.testing.utils.Utils;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.events.SyncFinished;
import com.quantimodo.tools.events.SyncStarted;
import com.quantimodo.tools.models.DaoSession;
import com.quantimodo.tools.models.Measurement;
import com.quantimodo.tools.models.MeasurementDao;
import com.quantimodo.tools.sdk.AuthHelper;
import de.greenrobot.dao.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.moodimodo.testhelpers.Utils.Condition;
import static com.moodimodo.testhelpers.Utils.waitForCondition;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest  extends ActivityInstrumentationTestCase2<MainActivity> {

    @Inject
    DaoSession mSession;

    @Inject
    AuthHelper authHelper;

    private boolean eventFired = false;
    private boolean syncEnd = false;
    private boolean syncSuccessful = false;
    private long timestamp = Long.MAX_VALUE;

    public MainActivityTest() {
        super(MainActivity.class);
        QTools.getInstance().inject(this);
    }

    @BeforeClass
    public static void main() throws Exception{
        TestModule.addToGraph();
    }

    @Before
    public void setup(){
        Global.welcomeCompleted = true;
        final SharedPreferences sharedPref = InstrumentationRegistry.getInstrumentation().getContext().getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean("entered_first_time", false).commit();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        Utils.unlockScreen(getActivity(),getInstrumentation());
        QTools.getInstance().register(this);
        timestamp = System.currentTimeMillis() / 1000;
        clean(timestamp - 70);
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
        com.quantimodo.sdk.testing.utils.Utils.closeAllActivities(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testSyncOnLogon() throws Exception{
        TestHelper.logInProccess(getActivity());

        waitForCondition(new Condition() {
            @Override
            public boolean check() {
                return eventFired;
            }
        }, 2 * 60 * 1000);

        assertTrue("Sync not started", eventFired);
    }

    @Test
    public void testSubmitMeasurement() throws Exception {
        TestHelper.logInProccess(getActivity());

        onView(withId(R.id.btEcstatic)).perform(scrollTo(), click());
        Thread.sleep(5000);

        Measurement last = mSession.getMeasurementDao()
                .queryBuilder()
                .orderDesc(MeasurementDao.Properties.Timestamp)
                .limit(1)
                .unique();

        assertEquals(5d, last.getValue());
        long t = Math.abs(System.currentTimeMillis()  - last.getTimestamp().getTime()) / 1000;
        assertTrue(String.valueOf(t),t < 15); //Less than 15 seconds
    }

    @Test
    public void testSubmitMeasurementWithoutLogin() throws Exception {
        authHelper.logOut();

        onView(withId(R.id.btEcstatic)).perform(click());
        Thread.sleep(3000);

        Measurement last = mSession.getMeasurementDao()
                .queryBuilder()
                .orderDesc(MeasurementDao.Properties.Timestamp)
                .limit(1)
                .unique();

        assertEquals(5d, last.getValue());
        long t = Math.abs(System.currentTimeMillis()  - last.getTimestamp().getTime()) / 1000;
        assertTrue(String.valueOf(t),t < 15); //Less than 15 seconds
    }

    @Test
    public void testRemoveLastMeasurement() throws Exception {
        Query<Measurement> measurementQuery = mSession.getMeasurementDao()
                .queryBuilder()
                .orderDesc(MeasurementDao.Properties.Timestamp)
                .limit(1).build();

        double previousMoodRating = measurementQuery.unique().getValue();

        onView(withId(R.id.btEcstatic)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.tvUndoLastMood)).perform(click());
        Thread.sleep(3000);

        Measurement last = measurementQuery.unique();
        assertEquals(previousMoodRating, last.getValue());
        long t = Math.abs(System.currentTimeMillis() - last.getTimestamp().getTime()) / 1000;
        assertTrue(String.valueOf(t), t > 60); //More than 60 seconds
    }

    @Test
    public void testSendMeasurement() throws Exception {
        TestHelper.logInProccess(getActivity());

        onView(withId(R.id.btEcstatic)).perform(click());
        Thread.sleep(1000);

        waitForCondition(new Condition() {
            @Override
            public boolean check() {
                return syncEnd;
            }
        }, 50000);

        assertTrue(syncSuccessful);
    }

    public void onEvent(SyncStarted syncStarted){
        eventFired = true;
    }

    public void onEvent(SyncFinished syncFinished){
        syncEnd = true;
        syncSuccessful = syncFinished.isSuccessful;
    }

}
