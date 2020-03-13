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

public class ProfileCheckTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("Sign Up");
        solo.enterText((EditText) solo.getView(R.id.SignupFirstName), "te");
        solo.enterText((EditText) solo.getView(R.id.SignupLastName), "st");
        solo.enterText((EditText) solo.getView(R.id.SignupEmail), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.SignupUserName), "tested");
        solo.enterText((EditText) solo.getView(R.id.SignupPassword), "123123");
        solo.enterText((EditText) solo.getView(R.id.SignupREpassword), "123123");
        solo.enterText((EditText) solo.getView(R.id.SignupPhone), "1800123123");

        solo.clickOnButton("Sign Up");
        solo.waitForActivity(LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "123123");
        solo.clickOnButton("Login");
        solo.clickOnButton("View Profile");
    }

    @Test
    public void checkText() {
        assertTrue(solo.waitForText("te", 1, 1000));
        assertTrue(solo.waitForText("st", 1, 1000));
        assertTrue(solo.waitForText("test@test.com", 1, 1000));
        assertTrue(solo.waitForText("tested", 1, 1000));
        assertTrue(solo.waitForText("1800123123", 1, 1000));
    }

    @Test
    public void checkBack() {
        solo.clickOnButton("Return");
        solo.assertCurrentActivity("Wrong Activity", TitleActivity.class);
        solo.clickOnButton("View Profile");
    }

    @Test
    public void checkEdit() {
        solo.clickOnButton("Edit Profile");
        solo.clearEditText((EditText) solo.getView(R.id.lastname));
        solo.enterText((EditText) solo.getView(R.id.lastname), "edit");
        solo.clickOnButton("Save Changes");
        solo.clickOnButton("Return");
        solo.clickOnButton("View Profile");
        assertTrue(solo.waitForText("edit", 1, 1000));
    }

    @After
    public void tearDown() throws Exception{
        solo.clickOnButton("Edit Profile");
        solo.clickOnButton("Delete Profile");
        solo.clickOnButton("Confirm");
        solo.finishOpenedActivities();
    }

}

