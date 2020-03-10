package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class r_newrideinfo_activity extends AppCompatActivity {

    Button r_RequestRideButton;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_newrideinfo_activity);

        r_RequestRideButton = findViewById(R.id.r_RequestRideButton);
        r_RequestRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //Intent intent = new Intent(r_newrideinfo_activity.this, r_historylist_activity.class);
                //startActivity(intent);
            }
        });


    }


}
