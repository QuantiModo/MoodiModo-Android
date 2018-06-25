package com.quantimodo.tools;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.AdapterView;
import com.quantimodo.android.sdk.QuantimodoApiV2;
import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.HistoryMeasurement;
import com.quantimodo.android.sdk.model.Unit;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.sdk.testing.utils.Utils;
import com.quantimodo.tools.sdk.AuthHelper;
import com.quantimodo.tools.testhelpers.TestHelper;
import com.quantimodo.tools.testhelpers.TestMainActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static espresso.QMatchers.isFooter;
import static espresso.QMatchers.unitWithName;
import static espresso.QMatchers.variableWithName;
import static espresso.QViewActions.waitId;
import static org.hamcrest.Matchers.*;


@RunWith(AndroidJUnit4.class)
public class TestMainActivityTest extends ActivityInstrumentationTestCase2<TestMainActivity> {

    @Inject
    AuthHelper mAuthHelper;

    @Inject
    QuantimodoApiV2 mClient;

    private boolean isLogged = false;
    final static String variableName = "Android " + UUID.randomUUID().toString().substring(0,6);

//    final String variableName = "Android f7276b";
    final static String CATEGORY_NAME = "Miscellany";
    final static String UNIT_NAME = "Applications";
    final static String VALUE = "999";

    private static boolean isVariableCreated = false;

    public TestMainActivityTest() {
        super(TestMainActivity.class);
    }

    private void openTrackingFragment() throws Exception{
        Thread.sleep(10000);
    }

    private void createVariable() throws Exception{
        openTrackingFragment();

        onView(withId(R.id.etVariableName)).perform(click());
        onData(isFooter()).inAdapterView(withId(R.id.lvVariableSuggestions)).perform(click());
        onView(isRoot()).perform(waitId(R.id.lnMeasurementsContainer, 1000));

        //Write data
        onView(withId(R.id.etVariableNameNew)).perform(click(),typeText(variableName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.rbVariableCombinationOperationAverage)).perform(scrollTo(), click());

        onView(withId(R.id.spMeasurementDate)).perform(scrollTo(),click());
        onView(withText("Today")).perform(click());

        onView(withId(R.id.spMeasurementTime)).perform(scrollTo(),click());
        onView(withText("At this time")).perform(click());

        onView(withId(R.id.spMeasurementUnit)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(Unit.class)), unitWithName(UNIT_NAME))).perform(click());

        onView(withId(R.id.etMeasurementValue)).perform(click(), typeText(VALUE), ViewActions.closeSoftKeyboard());
        onView(withText("Done")).perform(scrollTo(), click());

        Thread.sleep(10000);

        isVariableCreated = true;
    }

    @Test
    public void addVariable() throws Exception{
        QTools.getInstance().inject(this);
        createVariable();

        //Check data using SDK
        String token = mAuthHelper.getAuthToken();
        SdkResponse<ArrayList<Variable>> sdkResponse = mClient.searchVariables(getActivity(), token, variableName, 1, 0, null, null);
        assertTrue(sdkResponse.isSuccessful());
        assertTrue(sdkResponse.getData().size() > 0);

        SdkResponse<ArrayList<HistoryMeasurement>> history = mClient.getMeasurmentHistory(getActivity(), token, new Date(System.currentTimeMillis() - 30 * 1000), null, variableName, null, null);
        assertTrue(history.isSuccessful());
        assertTrue(history.getData().size() > 0);
    }

    @Test
    public void searchForItem() throws Exception{
        QTools.getInstance().inject(this);
        if (!isVariableCreated){
            createVariable();
        }

        openTrackingFragment();
        onView(withId(R.id.etVariableName)).perform(click(), typeText(variableName), ViewActions.closeSoftKeyboard());

        //Wait till data loaded
        Thread.sleep(10000);
        onData(allOf(is(instanceOf(Variable.class)),variableWithName(variableName))).inAdapterView(withId(R.id.lvVariableSuggestions)).atPosition(0).perform(click());
        onView(isRoot()).perform(waitId(R.id.lnMeasurementsContainer, 5000));

        onView(withId(R.id.etMeasurementValue)).perform(scrollTo(),click(), typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withText("Done")).perform(scrollTo(), click());

        Thread.sleep(10000);


        String token = mAuthHelper.getAuthToken();
        SdkResponse<ArrayList<Variable>> sdkResponse = mClient.searchVariables(getActivity(), token, variableName, 1, 0, null, null);
        assertTrue(sdkResponse.isSuccessful());
        assertTrue(sdkResponse.getData().size() > 0);

        SdkResponse<ArrayList<HistoryMeasurement>> history = mClient.getMeasurmentHistory(getActivity(), token, new Date(System.currentTimeMillis() - 20 * 1000), null, variableName, null, null);
        assertTrue(history.isSuccessful());
        assertTrue(history.getData().size() > 0);
    }

    private ViewAssertion currentUnitItem(final String unitName) {
        return new ViewAssertion() {
            @java.lang.Override
            public void check(View view, NoMatchingViewException e) {
                Unit unit = (Unit) ((AdapterView)view).getSelectedItem();
                if (!unitName.equals(unit.name)){
                    fail();
                }
            }
        };
    }

    private ViewAssertion currentCategoryItem(final String categoryName) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException e) {
                AdapterView v = (AdapterView) view;
                String category = (String) v.getSelectedItem();
                if (!categoryName.equals(category)){
                    fail();
                }
            }
        };
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        Activity mActivity = getActivity();
        Utils.unlockScreen(mActivity, InstrumentationRegistry.getInstrumentation());

        if (!isLogged){
            Context ctx = InstrumentationRegistry.getContext();
            TestHelper.logIn(getActivity());
            isLogged = true;
        }
    }

    @After
    public void tearDown() throws Exception{
        Utils.closeAllActivities(InstrumentationRegistry.getInstrumentation());
    }



}
