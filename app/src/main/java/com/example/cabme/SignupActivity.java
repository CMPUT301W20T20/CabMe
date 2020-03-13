package com.example.cabme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SignupActivity extends AppCompatActivity {

	String TAG = "Sample";

	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText repasswordEditText;
	private EditText phoneEditText;
	FirebaseFirestore db;
	FirebaseAuth mauth;
	private String fname, lname, email, uname, phone, pass, repass;
	Button signupButton;
	private User user;
	private String uid;
	//private DatabaseReference mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_activity);

		firstNameEditText = findViewById(R.id.SignupFirstName);
		lastNameEditText = findViewById(R.id.SignupLastName);
		emailEditText = findViewById(R.id.SignupEmail);
		userNameEditText = findViewById(R.id.SignupUserName);
		phoneEditText = findViewById(R.id.SignupPhone);
		passwordEditText = findViewById(R.id.SignupPassword);
		repasswordEditText = findViewById(R.id.SignupREpassword);
		signupButton = findViewById(R.id.signUpButton);
		//mDatabase = FirebaseDatabase.getInstance().getReference();

		db = FirebaseFirestore.getInstance();
		mauth = FirebaseAuth.getInstance();


		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fname = firstNameEditText.getText().toString().trim();
				lname = lastNameEditText.getText().toString().trim();
				email = emailEditText.getText().toString().trim();
				phone = phoneEditText.getText().toString().trim();
				uname = userNameEditText.getText().toString().trim();
				pass = passwordEditText.getText().toString();
				repass = repasswordEditText.getText().toString();
				final CollectionReference collectionReference = db.collection("users");

				Query query = collectionReference.whereEqualTo("username",uname);
				query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if(task.isSuccessful()){
							for(DocumentSnapshot documentSnapshot: task.getResult()){
								String user = documentSnapshot.getString("username");
								if(user.equals(uname)){
									Log.d(TAG,"User Exists");
									Toast.makeText(SignupActivity.this, "Please choose a unique username", Toast.LENGTH_LONG).show();
								}
							}
						}
						if(task.getResult().size()==0){
							Log.d(TAG,"User does not Exist");
							if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || uname.isEmpty() || pass.isEmpty() || repass.isEmpty() || phone.isEmpty()) {
								Toast.makeText(SignupActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
							} else {
								final String sfname = firstNameEditText.getText().toString().trim();
								final String slname = lastNameEditText.getText().toString().trim();
								final String semail = emailEditText.getText().toString().trim();
								final String sphone = phoneEditText.getText().toString().trim();
								final String suname = userNameEditText.getText().toString().trim();
								final String spass = passwordEditText.getText().toString();
								final String srepass = repasswordEditText.getText().toString();

								mauth.createUserWithEmailAndPassword(semail,spass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
									@Override
									public void onComplete(@NonNull Task<AuthResult> task) {

										if (task.isSuccessful()) {
											Toast.makeText(SignupActivity.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
											String uid = mauth.getCurrentUser().getUid();
											user = new User(uid,semail, sfname, slname, suname, sphone);
											finish();
											//startActivity(new Intent(MainActivity.this, TitleActivity.class));
										} else {
											FirebaseAuthException e = (FirebaseAuthException) task.getException();
											String s = "Sign up Failed" + task.getException();
											Toast.makeText(SignupActivity.this, s, Toast.LENGTH_LONG).show();
											return;
										}
									}
								});
							}
						}
					}
				});
			}
		});
	}
}
