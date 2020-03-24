package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cabme.riders.RiderHistoryListActivity;
import androidx.appcompat.app.AppCompatActivity;

public class driver_rider_activity extends AppCompatActivity{
    private Button riderButton;
    private Button driverButton;


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        //set view
        setContentView(R.layout.title_activity);

        //onclick listener for iAmRiderButton for activity RiderHistoryListActivity
        riderButton = findViewById(R.id.rider);
        riderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(driver_rider_activity.this, RiderHistoryListActivity.class);
                startActivity(intent);
            }
        });

    }


}
