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

public class RideRequestTest {
	private Solo solo;

	@Rule
	public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

	@Before
	public void setUp() throws Exception{
		solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
		solo.enterText((EditText) solo.getView(R.id.email), "ant@woman.com");
		solo.enterText((EditText) solo.getView(R.id.password), "123123");
		solo.clickOnButton("Login");
		solo.assertCurrentActivity("Login Successful", TitleActivity.class);
		solo.clickOnImage(2);
		assertTrue(solo.waitForText("Do you need a ride?", 1, 20000));
	}

	@Test
	public void checkRequest() {

		solo.clickOnButton("Request a Ride");
		solo.clickOnText("Starting Location");
		solo.enterText(2,"west edm");
		assertTrue(solo.waitForText("West Edmonton Mall", 1, 10000));
		solo.clickOnText("West Edmonton Mall");
		solo.clickOnText("Destination Location");
		solo.enterText(2,"ccis");
		assertTrue(solo.waitForText("CCIS", 1, 10000));
		solo.clickOnText("CCIS");
		solo.clickOnButton("Add tip");
		solo.clickOnText("Tip Amount...");
		solo.enterText(2,".35");
		solo.clickOnButton("Confirm");
		solo.clickOnButton("Request Ride");
		assertTrue(solo.waitForText("Waiting for a driver...", 1, 10000));
		assertTrue(solo.waitForText("11455 Saskatchewan Dr NW", 1, 10000));
		assertTrue(solo.waitForText("8770 170 St NW", 1, 10000));
		solo.clickOnButton("Cancel Request");
		assertTrue(solo.waitForText("Do you need a ride?", 1, 20000));
		solo.clickOnButton("Ride History");
		solo.scrollToTop();
		assertTrue(solo.waitForText("Cancelled", 1, 5000));
		assertTrue(solo.waitForText("8770 170 St NW", 1, 5000));
		assertTrue(solo.waitForText("11455 Saskatchewan Dr NW", 1, 5000));
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("Wrong Activity",TitleActivity.class);
		solo.clickOnButton("Logout");
		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
	}

	@After
	public void tearDown() throws Exception{
		solo.finishOpenedActivities();
	}
}


