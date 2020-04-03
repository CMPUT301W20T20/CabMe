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
import com.google.firebase.auth.FirebaseAuth;

public class TitleActivity extends AppCompatActivity {
    private Button profileButton;
    private Button logoutButton;
    private ImageButton riderButton;
    private ImageButton driverButton;
    private User user;
    private String uid;

    @Override
    /**
     * This is for the title activity to have buttons and attached functionalities
     * so a user can logout, sign in as a sriver or a rider
     * @param savedInstanceState
     */
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_activity);
        uid = getIntent().getStringExtra("user");

        user = new User(uid);
        user.setDocumentListener();


        profileButton = findViewById(R.id.profile);
        logoutButton = findViewById(R.id.logout);
        riderButton = findViewById(R.id.rider);
        driverButton = findViewById(R.id.driver);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This adds the current user
             * @param v
             */
            public void onClick(View v) {
                Intent intent = new Intent(TitleActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This logs out the current user from the database
             * @param v
             */
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        riderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This logs in the user as a rider
             * @param v
             */
            public void onClick(View v) {
                Intent intent = new Intent(TitleActivity.this, HomeMapActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("userType", UserType.RIDER);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This logs in the user as a driver
             * @param v
             */
            public void onClick(View v) {
                Intent intent = new Intent(TitleActivity.this, HomeMapActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("userType", UserType.DRIVER);
                startActivity(intent);
            }
        });


    }
    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
