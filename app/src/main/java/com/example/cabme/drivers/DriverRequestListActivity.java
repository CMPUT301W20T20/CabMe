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

public class DriverRequestListActivity extends FragmentActivity implements LocationListener, View.OnClickListener {
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
        if (checkLocationPermission()) {
            Log.d("D", "Permissions passed");
        }
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
        query = db.collection("testrequests");

        // Recycler options
        FirestoreRecyclerOptions<RiderHistoryListModel> options = new FirestoreRecyclerOptions.Builder<RiderHistoryListModel>()
                .setQuery(query.orderBy("rideCost"), RiderHistoryListModel.class)
                .build();

        // Sample Sort --- REMOVE
        options.getSnapshots().sort(new Comparator<RiderHistoryListModel>() {
            @Override
            public int compare(RiderHistoryListModel o1, RiderHistoryListModel o2) {
                Location dLoc = driver.getLocation();
                GeoPoint dGeo = new GeoPoint(dLoc.getLatitude(), dLoc.getLongitude());
                JsonParser jp1 = new JsonParser(dGeo, o1.getStartLocation(), getString(R.string.google_maps_key));
                JsonParser jp2 = new JsonParser(dGeo, o2.getStartLocation(), getString(R.string.google_maps_key));
                return jp1.getDistanceValue().compareTo(jp2.getDistanceValue());
            }
        });

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

                confirmRideButton.setText(String.format("%s", documentSnapshot.getId()));
                confirmRideButton.setVisibility(View.VISIBLE);
                confirmRideButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RideRequest rideRequest = new RideRequest(documentSnapshot.getId());
                        rideRequest.addOffer(uid);
                        Intent intent = new Intent();
                        setResult(1, intent);
                        finish();
                    }
                });
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


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Get Location")
                        .setMessage("here")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(DriverRequestListActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
        }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    finish();

                }
                return;
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        Log.i("Location info: Lat", lat.toString());
        Log.i("Location info: Lng", lng.toString());

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }



}
