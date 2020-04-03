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

public class ProfileTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("Signup Instead");
        solo.enterText((EditText) solo.getView(R.id.SignupFirstName), "te");
        solo.enterText((EditText) solo.getView(R.id.SignupLastName), "st");
        solo.enterText((EditText) solo.getView(R.id.SignupEmail), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.SignupUserName), "test889999");
        solo.enterText((EditText) solo.getView(R.id.SignupPassword), "123123");
        solo.enterText((EditText) solo.getView(R.id.SignupREpassword), "123123");
        solo.enterText((EditText) solo.getView(R.id.SignupPhone), "1800123123");

        solo.clickOnButton("Sign Up");
        solo.waitForActivity(TitleActivity.class);
        solo.clickOnButton("View Profile");
    }

    @Test
    public void checkText() {
        assertTrue(solo.waitForText("te", 1, 1000));
        assertTrue(solo.waitForText("st", 1, 1000));
        assertTrue(solo.waitForText("test@test.com", 1, 1000));
        assertTrue(solo.waitForText("test889999", 1, 1000));
        assertTrue(solo.waitForText("1800123123", 1, 1000));
    }

    @Test
    public void checkBack() {
        solo.goBack();
        solo.assertCurrentActivity("Wrong Activity", TitleActivity.class);
        solo.clickOnButton("View Profile");
    }

	@Test
	public void checkEditName() {
		solo.clickOnButton("Edit Profile");
		solo.clearEditText((EditText) solo.getView(R.id.firstname));
		solo.enterText((EditText) solo.getView(R.id.firstname), "newfname");
		solo.clearEditText((EditText) solo.getView(R.id.lastname));
		solo.enterText((EditText) solo.getView(R.id.lastname), "newlname");
		solo.clickOnButton("Save");
		solo.goBack();
		solo.clickOnButton("View Profile");
		assertTrue(solo.waitForText("newfname", 1, 1000));
		assertTrue(solo.waitForText("newlname", 1, 1000));
		solo.clickOnButton("Edit Profile");
		solo.clearEditText((EditText) solo.getView(R.id.firstname));
		solo.clickOnButton("Save");
		assertTrue(solo.waitForText("First name field is empty \n", 1, 1000));
		solo.enterText((EditText) solo.getView(R.id.firstname), "newerfname");
		solo.clickOnButton("Save");
		solo.goBack();
		solo.clickOnButton("View Profile");
		assertTrue(solo.waitForText("newerfname", 1, 1000));
	}


    @Test
    public void checkUsername() {
        solo.clickOnButton("Edit Profile");
        solo.clearEditText((EditText) solo.getView(R.id.username));
        solo.enterText((EditText) solo.getView(R.id.username), "yoyo");
        solo.clickOnButton("Save");
        assertTrue(solo.waitForText("Username is taken", 1, 1000));
        solo.clearEditText((EditText) solo.getView(R.id.username));
        solo.enterText((EditText) solo.getView(R.id.username), "newUname");
        solo.clickOnButton("Save");
        solo.goBack();
        solo.clickOnButton("View Profile");
        assertTrue(solo.waitForText("newUname", 1, 1000));
    }

	@Test
	public void checkEmail() {
		solo.clickOnButton("Edit Profile");
		solo.clearEditText((EditText) solo.getView(R.id.email));
		solo.enterText((EditText) solo.getView(R.id.email), "yoyo");
		solo.clickOnButton("Save");
		assertTrue(solo.waitForText("The email address is badly formatted.", 1, 1000));
		solo.clearEditText((EditText) solo.getView(R.id.email));
		solo.enterText((EditText) solo.getView(R.id.email), "newemail@email.com");
		solo.clickOnButton("Save");
		solo.goBack();
		solo.clickOnButton("View Profile");
		assertTrue(solo.waitForText("newemail@email.com", 1, 1000));
	}

	@Test
	public void checkphone() {
		solo.clickOnButton("Edit Profile");
		solo.clearEditText((EditText) solo.getView(R.id.phone));
		solo.clickOnButton("Save");
		assertTrue(solo.waitForText("Phone number field is empty \n", 1, 1000));
		solo.enterText((EditText) solo.getView(R.id.phone), "7801234567");
		solo.clickOnButton("Save");
		solo.goBack();
		solo.clickOnButton("View Profile");
		assertTrue(solo.waitForText("7801234567", 1, 1000));
	}

    @After
    public void tearDown() throws Exception{
    	solo.goBack();

		solo.clickOnButton("View Profile");
        solo.clickOnButton("Edit Profile");
        solo.clickOnButton("Delete Profile");
        solo.clickOnButton("Confirm");
        solo.finishOpenedActivities();
    }

}

