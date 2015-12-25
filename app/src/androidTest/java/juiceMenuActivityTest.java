import android.test.ActivityInstrumentationTestCase2;

import com.example.kanjuice.JuiceAdapter;
import com.example.kanjuice.JuiceItem;
import com.example.kanjuice.JuiceMenuActivity;
import com.example.kanjuice.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class juiceMenuActivityTest extends ActivityInstrumentationTestCase2 {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public juiceMenuActivityTest() {
        super(JuiceAdapter.class);
    }

    public void testQuarterCircleIconIsDisplayed() {
//        onView(withId(R.id.quarter_circle_img))
//                .check(matches(isDisplayed()));
    }
}
