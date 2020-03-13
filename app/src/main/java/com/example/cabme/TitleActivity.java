package com.example.cabme;

import android.content.Intent;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TitleActivity extends AppCompatActivity {
	private Button profileButton;
	private Button logoutButton;
	private Button riderButton;
	private Button driverButton;
	private User user;
	private String uid;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_activity);
		uid = getIntent().getStringExtra("user");
		//user = new User(uid);

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
				Log.d("T", user.getLastName());
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
				Intent intent = new Intent(TitleActivity.this, NewRideInfoActivity.class);
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
