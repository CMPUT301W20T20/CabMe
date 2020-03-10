package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class r_historylist_activity extends AppCompatActivity {


    private Button newRideButton;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_historylist_activity);

        //onclick listener for newRideButton to start r_newrideinfo_activity
        newRideButton = findViewById(R.id.newRideButton);
        newRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(r_historylist_activity.this, r_newrideinfo_activity.class);
                startActivity(intent);
            }
        });

    }
}
