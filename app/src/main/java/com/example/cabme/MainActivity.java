package com.example.cabme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

	ArrayList<User> userList;

	String TAG = "Sample";
	Button signupButton;
	Button signupDriverButton;
	EditText firstNameEditText;
	EditText lastNameEditText;
	EditText emailEditText;
	EditText userNameEditText;
	EditText passwordEditText;
	EditText phoneEditText;
	FirebaseFirestore db;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		signupButton = findViewById(R.id.signup);
		signupDriverButton = findViewById(R.id.signup_driver);
		firstNameEditText = findViewById(R.id.signup_firstName);
		lastNameEditText = findViewById(R.id.signup_lastName);
		emailEditText = findViewById(R.id.signup_email);
		userNameEditText = findViewById(R.id.signup_userName);
		phoneEditText = findViewById(R.id.signup_phone);
		passwordEditText = findViewById(R.id.signup_password);

		userList = new ArrayList<>();
		db = FirebaseFirestore.getInstance();

		final CollectionReference collectionReference = db.collection("Users");

		final CollectionReference driverReference = db.collection("Drivers");

		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String firstName = firstNameEditText.getText().toString();
				final String lastName = lastNameEditText.getText().toString();
				final String email = emailEditText.getText().toString();
				final String userName = userNameEditText.getText().toString();
				final String phone = phoneEditText.getText().toString();
				final String password = passwordEditText.getText().toString();

				HashMap <String, String> data = new HashMap<>();

				if (firstName.length()>0 && lastName.length()>0 && email.length()>0 && userName.length()>0 && phone.length()>0 && password.length()>0) {
					data.put("first_name", firstName);
					data.put("last_name",lastName);
					data.put("e_mail",email);
					data.put("user_name",userName);
					data.put("phone_number",phone);
					data.put("pass_word",password);

					collectionReference
						.document(userName)
						.set(data)
						.addOnSuccessListener(new OnSuccessListener<Void>() {
							@Override
							public void onSuccess(Void aVoid) {
								Log.d(TAG, "Data addition successful");
							}
						})
						.addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Log.d(TAG,"Data addition failed" + e.toString());
							}
						});
					firstNameEditText.setText("");
					lastNameEditText.setText("");
					emailEditText.setText("");
					userNameEditText.setText("");
					phoneEditText.setText("");
					passwordEditText.setText("");
				}

			}
		});

		signupDriverButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String firstName = firstNameEditText.getText().toString();
				final String lastName = lastNameEditText.getText().toString();
				final String email = emailEditText.getText().toString();
				final String userName = userNameEditText.getText().toString();
				final String phone = phoneEditText.getText().toString();
				final String password = passwordEditText.getText().toString();

				HashMap<String, String> data = new HashMap<>();

				if (firstName.length() > 0 && lastName.length() > 0 && email.length() > 0 && userName.length() > 0 && phone.length() > 0 && password.length() > 0) {
					data.put("first_name", firstName);
					data.put("last_name", lastName);
					data.put("e_mail", email);
					data.put("user_name", userName);
					data.put("phone_number", phone);
					data.put("pass_word", password);

					driverReference
						.document(userName)
						.set(data)
						.addOnSuccessListener(new OnSuccessListener<Void>() {
							@Override
							public void onSuccess(Void aVoid) {
								Log.d(TAG, "Data addition successful");
							}
						})
						.addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Log.d(TAG, "Data addition failed" + e.toString());
							}
						});
					firstNameEditText.setText("");
					lastNameEditText.setText("");
					emailEditText.setText("");
					userNameEditText.setText("");
					phoneEditText.setText("");
					passwordEditText.setText("");


				}
			}

		});

	}
}

//collectionReference
//	.document(userName)
//		.set(data)
//						.addOnSuccessListener(new OnSuccessListener<Void>() {
//		@Override
//		public void onSuccess(Void aVoid) {
//			Log.d(TAG, "Data addition successful");
//		}
//	})
//		.addOnFailureListener(new OnFailureListener() {
//		@Override
//		public void onFailure(@NonNull Exception e) {
//			Log.d(TAG,"Data addition failed" + e.toString());
//		}
//	});
//					firstNameEditText.setText("");
//					lastNameEditText.setText("");
//					emailEditText.setText("");
//					userNameEditText.setText("");
//					phoneEditText.setText("");
//					passwordEditText.setText("");

//public class MainActivity extends AppCompatActivity {
//
//	// Declare the variables so that you will be able to reference it later.
//	ListView cityList;
//	ArrayAdapter<City> cityAdapter;
//	ArrayList<City> cityDataList;
//
//
//	String TAG = "Sample";
//	Button addCityButton;
//	Button delCityButton;
//	EditText addCityEditText;
//	EditText addProvinceEditText;
//	FirebaseFirestore db;
//	int selectedPosition;
//
//	@Override
//	protected void attachBaseContext(Context base) {
//		super.attachBaseContext(base);
//		MultiDex.install(this);
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//
//
//		addCityButton = findViewById(R.id.add_city_button);
//		delCityButton = findViewById(R.id.del_city_button);
//		addCityEditText = findViewById(R.id.add_city_field);
//		addProvinceEditText = findViewById(R.id.add_province_edit_text);
//
//		cityList = findViewById(R.id.city_list);
//
//
//		cityDataList = new ArrayList<>();
//
//
//		cityAdapter = new CustomList(this, cityDataList);
//
//		cityList.setAdapter(cityAdapter);
//
//		cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				selectedPosition = position;
//				//Toast.makeText(getApplicationContext(), (String) parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
//				view.setSelected(true);
//
//			}
//		});
//
//
//		db = FirebaseFirestore.getInstance();
//
//		// Get a top-level reference to the collection.
//		final CollectionReference collectionReference = db.collection("Cities");
//
//		addCityButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				final String cityName = addCityEditText.getText().toString();
//				final String provinceName = addProvinceEditText.getText().toString();
//
//				HashMap<String, String> data = new HashMap<>();
//
//				if (cityName.length()>0 && provinceName.length()>0) {
//					data.put("province_name", provinceName);
//
//					collectionReference
//						.document(cityName)
//						.set(data)
//						.addOnSuccessListener(new OnSuccessListener<Void>() {
//							@Override
//							public void onSuccess(Void aVoid) {
//								Log.d(TAG, "Data addition successful");
//							}
//						})
//						.addOnFailureListener(new OnFailureListener() {
//							@Override
//							public void onFailure(@NonNull Exception e) {
//								Log.d(TAG,"Data addition failed" + e.toString());
//							}
//						});
//					addCityEditText.setText("");
//					addProvinceEditText.setText("");
//				}
//
//			}
//		});
//
//		delCityButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				collectionReference.document(cityDataList.get(selectedPosition).getCityName())
//					.delete()
//					.addOnSuccessListener(new OnSuccessListener<Void>() {
//						@Override
//						public void onSuccess(Void aVoid) {
//							Log.d(TAG, "DocumentSnapshot successfully deleted!");
//						}
//					})
//					.addOnFailureListener(new OnFailureListener() {
//						@Override
//						public void onFailure(@NonNull Exception e) {
//							Log.w(TAG, "Error deleting document", e);
//						}
//					});
//			}
//		});
//
//		collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//			@Override
//			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//				cityDataList.clear();
//				for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//					Log.d(TAG,String.valueOf(doc.getData().get("province_name")));
//					String city = doc.getId();
//					String province = (String) doc.getData().get("province_name");
//					cityDataList.add(new City (city,province));
//				}
//				cityAdapter.notifyDataSetChanged();
//			}
//		});
//
//	}
//
//
//}
