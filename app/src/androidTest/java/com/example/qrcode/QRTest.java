package com.example.qrcode;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.cabme.qrscanner.QRActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class QRTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<QRActivity> rule = new ActivityTestRule<>(QRActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.assertCurrentActivity("Wrong Activity", QRActivity.class);
    }

    @Test
    public void CheckQR() {
        solo.clickOnButton("Pay with QR-Bucks");
    }


}

