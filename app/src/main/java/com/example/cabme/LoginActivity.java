package com.example.cabme;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private Button loginButton;
    private FirebaseFirestore db;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signupButton = findViewById(R.id.signup);
        loginButton = findViewById(R.id.login);

        db = FirebaseFirestore.getInstance();
        mauth = FirebaseAuth.getInstance();

        final CollectionReference collectionReference = db.collection("Users");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                final String msg;

                if (valid(email, password)) {
                    mauth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startTitleActivity(mauth.getCurrentUser().getUid());
                                    } else {
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(this, SignUpActivity.class));
            }
        });
    }
    // https://emailregex.com/
    public boolean valid(String email, String password) {
        String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
                 "*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|" +
                "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]" +
                "*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]" +
                "|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*" +
                "[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\" +
                "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        String passRegex = "^[\\S]+$";
        String error = "";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern passPattern = Pattern.compile(passRegex);

        Matcher emailMatcher = emailPattern.matcher(email);
        Matcher passMatcher = passPattern.matcher(password);

        if (!emailMatcher.matches() || !passMatcher.matches()) {
            if (!emailMatcher.matches() && !email.isEmpty()) {
                error += "Invalid characters used in the email \n";
            } if (!passMatcher.matches() && !password.isEmpty()) {
                error += "Invalid characters used in the password \n";
            } if (email.isEmpty()) {
                error += "Email field is empty \n";
            } if (password.isEmpty()) {
                error += "Password field is empty \n";
            }
            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    
    public void startTitleActivity(String uid) {
        Intent intent = new Intent(this, TitleActivity.class);
        intent.putExtra("user", uid);
        startActivity(intent);
    }
}
