package com.example.cabme.riders;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cabme.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.annotation.Nullable;


public class Pop extends Activity {

    private String TAG = "Firestore";


    TextView username;
    Button select;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_offer_popup);

        //getting device screen height and width
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //setting layout settings
        getWindow().setLayout((int) (width * 0.8), (int) (height*0.15));

        //get intent
        getIncomingIntent();

        //make button click to set driver uid into the database
        select = findViewById(R.id.offer_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set values in database


                //move document to different collection?


                //change activity
            }
        });

    }

    /**
     * Purpose: getting the intent sent from RiderOffer activity, getting the username
     */
    private void getIncomingIntent(){

        username = findViewById(R.id.username);

        Log.d(TAG, "Get incoming intent");
        if(getIntent().hasExtra("username") && getIntent().getStringExtra("username").getClass() == String.class){

            Log.d(TAG, getIntent().getStringExtra("username"));
            String user = getIntent().getStringExtra("username");
            username.setText(user);
        }

    }
}
