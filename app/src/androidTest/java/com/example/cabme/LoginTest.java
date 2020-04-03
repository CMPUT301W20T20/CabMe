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

public class LoginTest {
	private Solo solo;

	@Rule
	public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

	@Before
	public void setUp() throws Exception{
		solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
	}

	@Test
	public void wrongPassword() {
		solo.enterText((EditText) solo.getView(R.id.email), "ant@man.com");
		solo.enterText((EditText) solo.getView(R.id.password), "123");

		solo.clickOnButton("Login");
		assertTrue(solo.waitForText("The password is invalid or the user does not have a password", 1, 2000));
		solo.assertCurrentActivity("Login Failed", LoginActivity.class);
	}

	@Test
	public void wrongEmail() {
		solo.enterText((EditText) solo.getView(R.id.email), "ant@man");
		solo.enterText((EditText) solo.getView(R.id.password), "123123");

		solo.clickOnButton("Login");
		assertTrue(solo.waitForText("There is no user record corresponding to this identifier. The user may have been deleted.", 1, 2000));
		solo.assertCurrentActivity("Login Failed", LoginActivity.class);
	}

	@Test
	public void checkLogin() {
		solo.enterText((EditText) solo.getView(R.id.email), "ant@man.com");
		solo.enterText((EditText) solo.getView(R.id.password), "123123");

		solo.clickOnButton("Login");
		solo.assertCurrentActivity("Login Successful", TitleActivity.class);
		solo.clickOnButton("Logout");
		solo.assertCurrentActivity("Logout Successful", LoginActivity.class);
	}

	@After
	public void tearDown() throws Exception{
		solo.finishOpenedActivities();
	}

}
