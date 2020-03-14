package com.example.cabme;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ProfileActivity extends AppCompatActivity implements Observer {
    private Button saveButton;
    private Button backButton;
    private Button editButton;
    private Button deleteButton;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText usernameEditText;
    private EditText fnameEditText;
    private EditText lnameEditText;
    private String nemail;
    private String nphone;
    private String nusername;
    private String nlname;
    private String nfname;
    private Map<String, Object> data;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        user = (User)getIntent().getSerializableExtra("user");
        data = new HashMap<>();
        user.addObserver(this);

        saveButton = findViewById(R.id.saveprofile);
        backButton = findViewById(R.id.back);
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nemail = emailEditText.getText().toString();
                nphone = phoneEditText.getText().toString();
                nusername = usernameEditText.getText().toString();
                nlname = lnameEditText.getText().toString();
                nfname = fnameEditText.getText().toString();
                if (true) { //valid(nemail, nphone, nusername, nlname, nfname)

                    if (!user.getEmail().equals(nemail)) {
                        data.put("email", nemail);
                    }
                    if (!user.getPhone().equals(nphone)) {
                        data.put("phone", nphone);
                    }
                    if (!user.getUsername().equals(nusername)) {
                        data.put("username", nusername);
                    }
                    if (!user.getLastName().equals(nlname)) {
                        data.put("last", nlname);
                    }
                    if (!user.getFirstName().equals(nfname)) {
                        data.put("first", nfname);
                    }


                    user.updateData(data);


                    emailEditText.setEnabled(false);
                    phoneEditText.setEnabled(false);
                    usernameEditText.setEnabled(false);
                    lnameEditText.setEnabled(false);
                    fnameEditText.setEnabled(false);

                    saveButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                    editButton.setVisibility(View.VISIBLE);
                }



            }
        });

    }

    public void update(Observable observable, Object o) {
        if (observable instanceof User) {
            User user = (User) observable;
            emailEditText.setText(user.getEmail());
            phoneEditText.setText(user.getPhone());
            usernameEditText.setText(user.getUsername());
            lnameEditText.setText(user.getLastName());
            fnameEditText.setText(user.getFirstName());
        }

    }

}
