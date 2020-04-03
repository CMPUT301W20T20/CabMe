package com.example.cabme.drivers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.Driver;
import com.example.cabme.R;
import com.example.cabme.Rating;
import com.example.cabme.User;
import com.example.cabme.UserProfileActivity;
import com.example.cabme.maps.JsonParser;


import com.example.cabme.HamburgerFragment;
import com.example.cabme.riders.RideRequest;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

// In other folders
import com.example.cabme.riders.RiderHistoryListModel;

import java.util.Comparator;

public class DriverRequestListActivity extends FragmentActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private DriverRequestListAdapter firestoreRecyclerAdapter;
    private LocationManager locationManager;
    private String provider;
    private Driver driver;
    private FusedLocationProviderClient mFusedLocationClient;
    private Bundle bundle;
    Button confirmRideButton;
    String uid;
    Query query;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_reqlist_activity);

        uid = getIntent().getStringExtra("uid");
        confirmRideButton = findViewById(R.id.confirm_ride);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            driver = new Driver(uid, location);
                            driver.setDocumentListener();
                            bundle = new Bundle();
                            bundle.putSerializable("user", driver);
//                            ImageButton hamburgerMenuBtn = findViewById(R.id.hamburger);
//                            hamburgerMenuBtn.setOnClickListener(DriverRequestListActivity.this);
                        }
                    }
                });

        db = FirebaseFirestore.getInstance();

        // Query
        query = db.collection("testrequests").whereEqualTo("rideStatus", "");

        // Recycler options
        FirestoreRecyclerOptions<RiderHistoryListModel> options = new FirestoreRecyclerOptions.Builder<RiderHistoryListModel>()
                .setQuery(query, RiderHistoryListModel.class)
                .build();


        firestoreRecyclerAdapter = new DriverRequestListAdapter(options);

        recyclerView = findViewById(R.id.request_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firestoreRecyclerAdapter);

        firestoreRecyclerAdapter.setOnItemClickListener(new DriverRequestListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Log.wtf("RIDERUID", documentSnapshot.getString("UIDrider"));
                String riderID = documentSnapshot.getString("UIDrider");
                User user = new User(riderID);
                user.readData((email, firstname, lastname, username, phone, rating) ->
                        confirmRideButton.setText(String.format("Ride with %s", firstname)));
                confirmRideButton.setVisibility(View.VISIBLE);
                confirmRideButton.setOnClickListener(v -> {
                    RideRequest rideRequest = new RideRequest(documentSnapshot.getId());
                    rideRequest.addOffer(uid);
                    Intent intent = new Intent();
                    setResult(1, intent);
                    finish();
                });
            }

            @Override
            public void onUsernameClick(DocumentSnapshot documentSnapshot, int position) {
                String riderID = documentSnapshot.getString("UIDrider");
                bundle = new Bundle();
                bundle.putSerializable("uid", riderID);
                UserProfileActivity userProfileActivity = new UserProfileActivity();
                userProfileActivity.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.d_reqlist_activity, userProfileActivity)
                        .commit();
            }
        });
    }

    @Override
    public void onClick(View v) {
        HamburgerFragment hamburgerFragment = new HamburgerFragment();
        hamburgerFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, hamburgerFragment, "driver")
                .commit();
    }

    @Override
    protected void onStop(){

        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }



}
