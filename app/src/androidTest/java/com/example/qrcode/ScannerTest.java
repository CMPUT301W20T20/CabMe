package com.example.cabme;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.cabme.qrscanner.ScannerQR;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ScannerTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<ScannerQR> rule = new ActivityTestRule<>(ScannerQR.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.assertCurrentActivity("Wrong Activity", ScannerQR.class);
    }

    @Test
    public void CheckQR() {


    }


}

