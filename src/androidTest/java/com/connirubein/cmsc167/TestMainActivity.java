package com.connirubein.cmsc167;



import android.app.Instrumentation;
import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Instrumentation.ActivityResult;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)

public class TestMainActivity {
    @Rule
    public ActivityTestRule<SignUpActivity> activityActivityTestRule = new ActivityTestRule<SignUpActivity>(SignUpActivity.class);
    private SignUpActivity signUpActivity = null;
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(SignUpActivity.class.getName(),null,false);

    @Before
    public void setUp() throws Exception{
        signUpActivity = activityActivityTestRule.getActivity();
    }

    @Test
    public void click_logIn() throws Exception{
        // Type text and then press the button.

        onView(withId(R.id.et_name)).perform(clearText(),typeText("abcdefghij"));
        onView(withId(R.id.et_password)).perform(clearText(),typeText("abcdefghij"));
        onView(withId(R.id.btnLogin)).perform(click());


        Thread.sleep(1000);


    }

    @Test
    public void click_signUp() throws Exception{
        // Type text and then press the button.

        try {
            onView(withId(R.id.btnSignup)).perform(click());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(1000);


    }





}
