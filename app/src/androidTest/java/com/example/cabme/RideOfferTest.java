package com.example.cabme;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.cabme.drivers.DriverRequestListActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class RideOfferTest {
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
		assertTrue(solo.waitForText("View Profile", 1, 20000));
		solo.clickOnImage(2);
		assertTrue(solo.waitForText("Do you need a ride?", 1, 20000));
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
		assertTrue(solo.waitForText("Waiting for a driver...", 1, 20000));
		assertTrue(solo.waitForText("11455 Saskatchewan Dr NW", 1, 20000));
		assertTrue(solo.waitForText("8770 170 St NW", 1, 20000));
		solo.goBack();
		solo.assertCurrentActivity("Wrong Activity",TitleActivity.class);
		solo.clickOnButton("Logout");
		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
	}

	@Test
	public void checkRideOffer() {

		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
		solo.enterText((EditText) solo.getView(R.id.email), "ant@man.com");
		solo.enterText((EditText) solo.getView(R.id.password), "123123");
		solo.clickOnButton("Login");
		solo.assertCurrentActivity("Login Successful", TitleActivity.class);
		assertTrue(solo.waitForText("View Profile", 1, 20000));
		solo.clickOnImage(1);
		assertTrue(solo.waitForText("Where are you driving today?", 1, 30000));
		solo.clickOnButton("Check Ride Requests");
		solo.waitForActivity(DriverRequestListActivity.class);
		solo.clickOnText("ant woman");
		solo.clickOnButton("Ride with ant");
		assertTrue(solo.waitForText("Ride Details", 1, 30000));
		solo.goBack();
		solo.assertCurrentActivity("Wrong Activity",TitleActivity.class);
		solo.clickOnButton("Logout");
		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
		assertTrue(solo.waitForText("Login", 1, 30000));

		solo.enterText((EditText) solo.getView(R.id.email), "ant@woman.com");
		solo.enterText((EditText) solo.getView(R.id.password), "123123");
		solo.clickOnButton("Login");
		solo.assertCurrentActivity("Login Successful", TitleActivity.class);
		assertTrue(solo.waitForText("View Profile", 1, 30000));
		solo.clickOnImage(2);
		//assertTrue(solo.waitForText("Ride Details", 1, 20000));
		assertTrue(solo.waitForText("View Offers", 1, 20000));
		solo.clickOnButton("View Offers");
		assertTrue(solo.waitForText("Offers", 1, 20000));
		solo.clickOnText("ant man");
		solo.clickOnButton("Ride with ant");
		assertTrue(solo.waitForText("Rider Ready", 1, 30000));
		solo.goBack();
		solo.assertCurrentActivity("Wrong Activity",TitleActivity.class);
		solo.clickOnButton("Logout");
		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
		assertTrue(solo.waitForText("Login", 1, 20000));
		solo.enterText((EditText) solo.getView(R.id.email), "ant@man.com");
		solo.enterText((EditText) solo.getView(R.id.password), "123123");
		solo.clickOnButton("Login");
		solo.assertCurrentActivity("Login Successful", TitleActivity.class);
		assertTrue(solo.waitForText("View Profile", 1, 30000));
		solo.clickOnImage(1);
		assertTrue(solo.waitForText("The rider accepted your offer, start the ride!", 1, 40000));
		solo.clickOnText("OK");
		assertTrue(solo.waitForText("Active", 1, 30000));
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("Wrong Activity",TitleActivity.class);
		solo.clickOnButton("Logout");
		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
		assertTrue(solo.waitForText("Login", 1, 20000));
		solo.enterText((EditText) solo.getView(R.id.email), "ant@woman.com");
		solo.enterText((EditText) solo.getView(R.id.password), "123123");
		solo.clickOnButton("Login");
		solo.assertCurrentActivity("Login Successful", TitleActivity.class);
		assertTrue(solo.waitForText("View Profile", 1, 20000));
		solo.clickOnImage(2);
		assertTrue(solo.waitForText("OK", 1, 40000));
		solo.clickOnText("OK");
		solo.clickOnButton("Complete Ride");
		assertTrue(solo.waitForText("Rate Driver", 1, 30000));
		solo.clickOnImageButton(1);
		//assertTrue(solo.waitForText("PAY WITH QR-BUCKS", 1, 30000));
		solo.clickOnButton("Pay with QR-Bucks");
		solo.clickOnButton("Go Back to Main");
		//solo.clickOnText("GO BACK TO MAIN");
		solo.assertCurrentActivity("Wrong Activity",TitleActivity.class);
		solo.clickOnButton("Logout");
		solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
	}

	@After
	public void tearDown() throws Exception{
		solo.finishOpenedActivities();
	}
}


