package com.moodimodo;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.moodimodo.activities.MainActivity;
import com.moodimodo.testhelpers.TestHelper;
import com.moodimodo.testhelpers.TestModule;
import com.quantimodo.android.sdk.model.Unit;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.QTools;
import com.quantimodo.tools.adapters.DrawerAdapter;
import com.quantimodo.tools.sdk.AuthHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static espresso.QMatchers.*;
import static espresso.QViewActions.waitId;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TrackingFragmentTest {

    @Inject
    AuthHelper mAuthHelper;

    final static String UNIT_NAME = "Applications";
    final static String VALUE = "999";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @BeforeClass
    public static void main() throws Exception{
        TestModule.addToGraph();
    }

    private void login() throws Exception{
        if (mAuthHelper == null){
            QTools.getInstance().inject(this);
        }

        if (!mAuthHelper.isLoggedIn()) {
            TestHelper.logInProccess(mActivityRule.getActivity());
        }
    }

    @Before
    public void before() throws Exception{
        login();
    }

    private void openTrackWithName(String name) throws Exception{
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onData(allOf(is(instanceOf(DrawerAdapter.DrawerItem.class)), withChildName("Track")))
                .inAdapterView(withId(R.id.items_list))
                .perform(click());

        onData(allOf(is(instanceOf(DrawerAdapter.DrawerItem.class)), withChildName(name)))
                .inAdapterView(withId(R.id.items_list))
                .perform(click());


        //Wait till data is loaded
        Thread.sleep(10000);
    }

    private void addVariableWithName(String variableName) throws Exception{
        onView(withId(com.quantimodo.tools.R.id.etVariableName)).perform(click());
        onData(isFooter()).inAdapterView(withId(com.quantimodo.tools.R.id.lvVariableSuggestions)).perform(click());
        onView(isRoot()).perform(waitId(com.quantimodo.tools.R.id.lnMeasurementsContainer, 3000));

        onView(allOf(withId(com.quantimodo.tools.R.id.btTime), withParent(withId(com.quantimodo.tools.R.id.lnMeasurementsButtons)))).perform(scrollTo(),click());

        //Write data
        onView(withId(com.quantimodo.tools.R.id.etVariableNameNew)).perform(scrollTo(), click(), typeText(variableName), ViewActions.closeSoftKeyboard());
        onView(withId(com.quantimodo.tools.R.id.rbVariableCombinationOperationAverage)).perform(scrollTo(), click());


        onView(withId(com.quantimodo.tools.R.id.spMeasurementDate)).perform(scrollTo(),click());
        onView(withText("Today")).perform(click());

        onView(withId(com.quantimodo.tools.R.id.spMeasurementTime)).perform(scrollTo(),click());
        onView(withText("At this time")).perform(click());

        onView(withId(com.quantimodo.tools.R.id.spMeasurementUnit)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(Unit.class)), unitWithName(UNIT_NAME))).perform(click());

        Thread.sleep(3000);
        onView(withId(com.quantimodo.tools.R.id.etMeasurementValue)).perform(scrollTo(), click(), clearText(), typeText(VALUE), ViewActions.closeSoftKeyboard());

        onView(withText("Done")).perform(scrollTo(), click());

        Thread.sleep(1000);

        onView(withId(android.R.id.button1)).perform(click());

        Thread.sleep(15000);
    }

    private void checkSearch(String name) throws Exception{
        onView(withId(com.quantimodo.tools.R.id.etVariableName)).perform(click(), typeText(name), ViewActions.closeSoftKeyboard());

        //Wait till data loaded
        Thread.sleep(10000);
        onData(allOf(is(instanceOf(Variable.class)),variableWithName(name))).inAdapterView(withId(com.quantimodo.tools.R.id.lvVariableSuggestions)).atPosition(0).perform(click());
        onView(isRoot()).perform(waitId(com.quantimodo.tools.R.id.lnMeasurementsContainer, 5000));
        onView(withText("Done")).perform(scrollTo(), click());

        Thread.sleep(10000);
    }

    @Test
    public void testSubmitNewFood() throws Exception{
        openTrackWithName("Diet");

        String name = "TestFood " + String.valueOf(System.currentTimeMillis() / 1000);
        addVariableWithName(name);

        checkSearch(name);
    }

    @Test
    public void testSubmitAny() throws Exception{
        openTrackWithName("Anything");

        String name = "TestVariable" + String.valueOf(System.currentTimeMillis() / 1000);
        addVariableWithName(name);

        checkSearch(name);
    }

}
