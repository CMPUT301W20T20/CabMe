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
    private FirebaseAuth mauth;

    @Override
    /**
     * This method creates he login page with email, password, signup and login buttons
     * and authenticates with the database
     * @param savedInstanceState
     */
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signupButton = findViewById(R.id.signup);
        loginButton = findViewById(R.id.login);

        mauth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This captures the login info (email and password) from the user
             * and validates it with the database to allow a user to log in.
             * @param v
             */
            public void onClick(View v) {
                final String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (valid(email, password)) {
                    mauth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                /**
                                 * This method logs in a user with email and password upon
                                 * succesful validation of these details in the database
                                 * and takes from login activity to the title activity
                                 * @param task
                                 */
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        emailEditText.setText("");
                                        passwordEditText.setText("");
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
            /**
             * This takes a user from login activity to signup activity
             * @param v
             */
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }
    // https://emailregex.com/
    /**
     * This does validation checks on email and password so only a user with correct
     * and complete login details can log into the app. If not, it displays the required
     * messages to the user
     * @param email
     * @param password
     * @return
     */

    public boolean valid(String email, String password) {

        String error = "";
        if (email.isEmpty()) {
            error += "Email field is empty \n";
        }
        if (password.isEmpty()) {
            error += "Password field is empty \n";
        }
        if (!error.isEmpty()) {
            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Upon successful login, a user is assigned a unique user id and is taken to
     * the title activity of the app
     * @param uid
     */
    public void startTitleActivity(String uid) {
        Intent intent = new Intent(this, TitleActivity.class);
        intent.putExtra("user", uid);
        startActivity(intent);
    }
}
