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

public class FailedSignUpTest {
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
    public void checkActivity() {
        solo.assertCurrentActivity("Not in SignUpActivity", SignupActivity.class);
    }

    @Test
    public void emptyField() {
        solo.enterText((EditText) solo.getView(R.id.SignupFirstName), "te");
        solo.enterText((EditText) solo.getView(R.id.SignupLastName), "st");
        solo.enterText((EditText) solo.getView(R.id.SignupEmail), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.SignupUserName), "test9");
        solo.enterText((EditText) solo.getView(R.id.SignupPassword), "123123");
        solo.clickOnButton("Sign Up");
        assertTrue(solo.waitForText("Please enter all the details", 1, 2000));
    }

    @Test
    public void nonMatchingPassword() {
        solo.enterText((EditText) solo.getView(R.id.SignupFirstName), "te");
        solo.enterText((EditText) solo.getView(R.id.SignupLastName), "st");
        solo.enterText((EditText) solo.getView(R.id.SignupEmail), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.SignupUserName), "test9");
        solo.enterText((EditText) solo.getView(R.id.SignupPhone), "123123123");
        solo.enterText((EditText) solo.getView(R.id.SignupPassword), "123123");
        solo.enterText((EditText) solo.getView(R.id.SignupREpassword), "123");
        solo.clickOnButton("Sign Up");
        assertTrue(solo.waitForText("Password and reentry of password do not match", 1, 2000));
    }

    @Test
    public void nonUniqueUsername() {
        solo.enterText((EditText) solo.getView(R.id.SignupFirstName), "te");
        solo.enterText((EditText) solo.getView(R.id.SignupLastName), "st");
        solo.enterText((EditText) solo.getView(R.id.SignupEmail), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.SignupPhone), "123123123");
        solo.enterText((EditText) solo.getView(R.id.SignupUserName), "yoyo");
        solo.enterText((EditText) solo.getView(R.id.SignupPassword), "123123");
        solo.enterText((EditText) solo.getView(R.id.SignupREpassword), "123123");
        solo.clickOnButton("Sign Up");
        assertTrue(solo.waitForText("Please choose a unique username", 1, 2000));
    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
