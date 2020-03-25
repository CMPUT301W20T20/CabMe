package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*
 * Imported Classes
 */
import com.example.cabme.drivers.DriverRequestListActivity;
import com.example.cabme.riders.RiderMapActivity;

public class TitleActivity extends AppCompatActivity {
    private Button profileButton;
    private Button logoutButton;
    private ImageButton riderButton;
    private ImageButton driverButton;
    private User user;
    private String uid;

    public enum UserType {
        RIDER,
        DRIVER
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_activity);

        uid = (String)getIntent().getStringExtra("user");
        user = new User(uid);
        user.setDocumentListener();

        profileButton = findViewById(R.id.profile);
        logoutButton = findViewById(R.id.logout);
        riderButton = findViewById(R.id.rider);
        driverButton = findViewById(R.id.driver);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TitleActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        riderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TitleActivity.this, RiderMapActivity.class);
                intent.putExtra("user", uid);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TitleActivity.this, DriverRequestListActivity.class);
                intent.putExtra("Driver-UID", uid);
                startActivity(intent);
            }
        });
    }
}
