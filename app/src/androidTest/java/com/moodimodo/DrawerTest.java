package com.moodimodo;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.intent.IntentStubberRegistry;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import com.moodimodo.activities.MainActivity;
import com.quantimodo.tools.adapters.DrawerAdapter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static espresso.QViewActions.waitId;
import static org.hamcrest.Matchers.*;
import static espresso.QMatchers.*;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class DrawerTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    private void clickItem(int idx) throws Exception{
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onData(is(instanceOf(DrawerAdapter.DrawerItem.class)))
                .inAdapterView(withId(R.id.items_list))
                .atPosition(idx)
                .perform(click());

        Thread.sleep(1000);
    }

    @Test
    public void testEachMenuItem() throws Exception{
        ExpandableListView list = (ExpandableListView) mActivityRule.getActivity().findViewById(R.id.items_list);

        Intents.init();

        Intents.intending(IntentMatchers.anyIntent()).respondWith(new Instrumentation.ActivityResult(10,new Intent()));

        ExpandableListAdapter adapter = list.getExpandableListAdapter();
        int n = adapter.getGroupCount();
        int count = n;
        for (int i=0; i<n; i++){
            count += adapter.getChildrenCount(i);
        }

        for (int i=0; i<count; i++){
            clickItem(i);
        }

        Intents.release();
    }

}
