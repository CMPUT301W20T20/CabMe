package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class driver_rider_activity extends AppCompatActivity{
    private Button iAmRiderButton;
    private Button iAmDriverButton;


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        //set view
        setContentView(R.layout.driver_rider_activity);

        //onclick listener for iAmRiderButton for activity r_historylist_activity
        iAmRiderButton = findViewById(R.id.IamRiderButton);
        iAmRiderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(driver_rider_activity.this, r_historylist_activity.class);
                startActivity(intent);
            }
        });

    }


}
