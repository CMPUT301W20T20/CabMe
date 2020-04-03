package com.example.cabme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ProfileActivity extends AppCompatActivity implements Observer {
    private String TAG = "PROFILE";
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
    private FirebaseAuth mauth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        user = (User)getIntent().getSerializableExtra("user");
        data = new HashMap<>();
        user.addObserver(this);
        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("users");


        saveButton = findViewById(R.id.saveprofile);
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
                switchButtons(true);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder
                        .setTitle("DELETE PROFILE")
                        .setMessage("Are you sure you wish to delete your profile")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                collectionReference.document(user.getUid()).delete();
                                mauth.getCurrentUser().delete()
                                        .addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Deletion successful",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.wtf(TAG, "delete failed", task.getException());
                                                    Toast.makeText(ProfileActivity.this, "Deletion successful",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        })
                        .show();

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
                Query query = collectionReference.whereEqualTo("username",nusername);
                Log.d(TAG, user.getUsername());

                if (valid(nemail, nusername, nphone, nlname, nfname)) {
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if (!task.getResult().isEmpty() && !user.getUsername().equals(nusername)) {
                                    Toast.makeText(ProfileActivity.this, "Username is taken", Toast.LENGTH_LONG).show();
                                } else {
                                    mauth.getCurrentUser().updateEmail(nemail)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        data.put("email", nemail);
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

                                                        switchButtons(false);

                                                    } else {
                                                        Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void switchButtons(Boolean on) {

        emailEditText.setEnabled(on);
        phoneEditText.setEnabled(on);
        usernameEditText.setEnabled(on);
        lnameEditText.setEnabled(on);
        fnameEditText.setEnabled(on);

        if (on) {
            saveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        }
        else {
            saveButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        }

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

    private boolean valid(String email, String username, String phone, String lastname, String firstname) {
        boolean valid = true;
        String error = "";

        if (email.isEmpty()) {
            error += "Email field is empty \n";
            valid = false;
        } if (username.isEmpty()) {
            error += "Email field is empty \n";
            valid = false;
        } if (phone.isEmpty()) {
            error += "Phone number field is empty \n";
            valid = false;
        }

        if (firstname.isEmpty()) {
            error += "First name field is empty \n";
            valid = false;
        }if (lastname.isEmpty()) {
			error += "Last name field is empty \n";
			valid = false;
		}

		if (email.isEmpty()) {
			error += "Email field is empty \n";
			valid = false;
		}

		if (phone.isEmpty()) {
			error += "Phone number field is empty \n";
			valid = false;
		}

        if (!valid) {
            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.wtf("PROFILE", "Successful Backpress");
        finish();
    }
}
