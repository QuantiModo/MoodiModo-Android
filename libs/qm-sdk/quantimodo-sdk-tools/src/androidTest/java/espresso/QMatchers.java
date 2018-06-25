package espresso;


import android.view.View;
import android.widget.ImageView;
import com.quantimodo.android.sdk.model.Correlation;
import com.quantimodo.android.sdk.model.Unit;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.tools.ToolsPrefs;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.*;

public class QMatchers {

    @SuppressWarnings("unchecked")
    public static Matcher<String> isFooter() {
        return allOf(is(instanceOf(String.class)), is(ToolsPrefs.FOOTER));
    }

    public static Matcher<Unit> unitWithName(final String displayName) {
        return new BaseMatcher<Unit>() {

            @Override
            public boolean matches(Object o) {
                return o instanceof Unit && displayName.equals(((Unit) o).name);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    public static Matcher<Correlation> correlationMatcher(final String displayName){
        return new BaseMatcher<Correlation>() {
            @Override
            public boolean matches(Object item) {
                return item instanceof Correlation && displayName.equals(((Correlation)item).getCause());
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }


    public static Matcher<Variable> variableWithName(final String name){
        return new BaseMatcher<Variable>() {

            @Override
            public boolean matches(Object o) {
                return o instanceof Variable && name.equals(((Variable) o).name);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
