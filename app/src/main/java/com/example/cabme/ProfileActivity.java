package com.example.cabme;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private Button saveButton;
    private Button backButton;
    private Button editButton;
    private Button deleteButton;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText usernameEditText;
    private EditText fnameEditText;
    private EditText lnameEditText;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        user = (User)getIntent().getSerializableExtra("user");

        saveButton = findViewById(R.id.saveprofile);
        //backButton = findViewById(R.id.logout);
        editButton = findViewById(R.id.editprofile);
        deleteButton = findViewById(R.id.deletedprofile);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        usernameEditText = findViewById(R.id.username);
        fnameEditText = findViewById(R.id.firstname);
        lnameEditText = findViewById(R.id.lastname);

        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhone());
        usernameEditText.setText(user.getUsername());
        lnameEditText.setText(user.getLastName());
        fnameEditText.setText(user.getFirstName());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEditText.setEnabled(true);
                phoneEditText.setEnabled(true);
                usernameEditText.setEnabled(true);
                lnameEditText.setEnabled(true);
                fnameEditText.setEnabled(true);

                saveButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEditText.setEnabled(false);
                phoneEditText.setEnabled(false);
                usernameEditText.setEnabled(false);
                lnameEditText.setEnabled(false);
                fnameEditText.setEnabled(false);

                saveButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
