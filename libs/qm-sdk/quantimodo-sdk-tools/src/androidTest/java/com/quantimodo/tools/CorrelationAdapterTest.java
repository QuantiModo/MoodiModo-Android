package com.quantimodo.tools;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.quantimodo.android.sdk.model.Correlation;
import com.quantimodo.sdk.testing.utils.Utils;
import com.quantimodo.tools.adapters.CorrelationAdapter;
import com.quantimodo.tools.fixtures.CorrelationsFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CorrelationAdapterTest{

    private CorrelationAdapter adapter;

    @Before
    public void before(){
        Context ctx = InstrumentationRegistry.getContext();
        adapter = new CorrelationAdapter(ctx, CorrelationsFixture.CORRELATIONS,CorrelationAdapter.TYPE_POSITIVE);
    }

    @After
    public void tearDown() throws Exception{
        Utils.closeAllActivities(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testSort(){
        adapter.setType(CorrelationAdapter.TYPE_POSITIVE);

        int count = adapter.getCount();
        Correlation first = (Correlation) adapter.getItem(0);
        Correlation last = (Correlation) adapter.getItem(count - 1);

        assertTrue(Math.abs(first.getCorrelationCoefficient()) > Math.abs(last.getCorrelationCoefficient()));

        adapter.setType(CorrelationAdapter.TYPE_NEGATIVE);
        count = adapter.getCount();
        first = (Correlation) adapter.getItem(0);
        last = (Correlation) adapter.getItem(count - 1);

        assertTrue(Math.abs(first.getWeight()) > Math.abs(last.getWeight()));
    }

//    @Test
//    public void testSwitchType(){
//        DataSetObserver observer = createMock(DataSetObserver.class);
//        observer.onChanged();
//        replay(observer);
//        adapter.registerDataSetObserver(observer);
//
//
//        Correlation correlation = (Correlation) adapter.getItem(0);
//        assertTrue(correlation.getCorrelationCoefficient() > 0);
//
//
//        adapter.setType(CorrelationAdapter.TYPE_NEGATIVE);
//        verify(observer);
//
//        correlation = (Correlation) adapter.getItem(0);
//        assertTrue(correlation.getCorrelationCoefficient() < 0);
//    }
}
