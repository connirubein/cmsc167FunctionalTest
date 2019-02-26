package com.connirubein.cmsc167;



import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)

public class TestIntents {
    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mainActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(SignUpActivity.class.getName(),null,false);
    //Instrumentation.ActivityMonitor mainMonitor = getInstrumentation().add

    @Before
    public void setUp() throws Exception{
        mainActivity = activityActivityTestRule.getActivity();
    }

    @Test
    public void click_signUp() throws Exception{
        // Type text and then press the button.
        assertNotNull(mainActivity.findViewById(R.id.btnSignup));
        try {
            onView(withId(R.id.btnSignup)).perform(click());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Activity signUpActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(signUpActivity);
        signUpActivity.finish();

    }

    @After
    public void tearDown() throws Exception{
        mainActivity = null;
    }


}
