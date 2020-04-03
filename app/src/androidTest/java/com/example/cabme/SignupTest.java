package com.example.cabme;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class SignupTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("Signup Instead");

    }

    @Test
    public void checkSignUp() {
        solo.enterText((EditText) solo.getView(R.id.SignupFirstName), "te");
        solo.enterText((EditText) solo.getView(R.id.SignupLastName), "st");
        solo.enterText((EditText) solo.getView(R.id.SignupEmail), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.SignupUserName), "test9");
        solo.enterText((EditText) solo.getView(R.id.SignupPassword), "123123");
        solo.enterText((EditText) solo.getView(R.id.SignupREpassword), "123123");
        solo.enterText((EditText) solo.getView(R.id.SignupPhone), "1800123123");

        solo.clickOnButton("Sign Up");
        assertTrue(solo.waitForText("Successfully Registered, Upload complete!", 1, 2000));

        solo.assertCurrentActivity("Signup Succeeded", TitleActivity.class);
    }


    @After
    public void tearDown() throws Exception{
        solo.waitForActivity(TitleActivity.class);
        solo.clickOnButton("View Profile");
        solo.clickOnButton("Edit Profile");
        solo.clickOnButton("Delete Profile");
        solo.clickOnButton("Confirm");
        solo.finishOpenedActivities();
    }

}
