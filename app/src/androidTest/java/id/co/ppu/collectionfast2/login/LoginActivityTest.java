package id.co.ppu.collectionfast2.login;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import id.co.ppu.collectionfast2.MainActivity;
import id.co.ppu.collectionfast2.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Eric on 14-Mar-17.
 */
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActitivyTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity mActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        mActivity = mActitivyTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    @Test
    public void onSignInClick() throws Exception {
        View view = mActivity.findViewById(R.id.username);

        onView(withId(R.id.sign_in_button)).perform(click());

        Activity secondAct = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);

        assertNotNull(secondAct);

        secondAct.finish();
    }

}