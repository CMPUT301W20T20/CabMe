package com.example.cabme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.cabme.drivers.DriveInactiveFragment;
import com.example.cabme.drivers.DriveActiveFragment;
import com.example.cabme.drivers.DriverRequestListActivity;
import com.example.cabme.maps.FetchURL;
import com.example.cabme.maps.TaskLoadedCallback;
import com.example.cabme.riders.RideActiveFragment;
import com.example.cabme.riders.RideInactiveFragment;
import com.example.cabme.riders.RideRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 *
 * This this the 'Home Screen' if they are a rider.
 *
 * - Shows a button to view ride history
 * - Shows a button to request a new ride
 * - A map menu that shows current location
 * - Profile button thing to view profile, change the 'User type' and balance
 *
 * Used sources:
 * (1) https://www.tutorialspoint.com/how-to-show-current-location-on-a-google-map-on-android
 * (2) https://github.com/mitchtabian/Google-Maps-Google-Places
 *
 * TODO:
 *  [x] finish rider side basic no bug checks
 *  [x] LOAD FROM FIRE BASE when you log in check if you have any requests in fireBase - Online
 *  [ ] opens from shared preference for backup
 *  [ ] COMMENTS :/
 *  [x] fix fragments stacking
 *  [x] why do i have so many global variables this is confusing
 *
 *
 */
public class HomeMapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, View.OnClickListener {
    private User user;
    private Bundle bundle;
    private UserType userType;
    private String rid;
    private String uid;
    private RideRequest request;

    private static final String TAG = "HomeMapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Polyline currPolyline;
    private MarkerOptions markStart;
    private MarkerOptions markDest;

    private LatLng startLatLng;
    private LatLng destLatLng;

    private boolean offered;

    /* fragments */
    RideInactiveFragment riderInactiveFragment;
    RideActiveFragment riderPendingFragment;
    DriveInactiveFragment driverInactiveFragment;
    DriveActiveFragment driveActiveFragment;

    private transient FirebaseFirestore firebaseFirestore;
    private transient CollectionReference collectionReference;

    private Boolean onStart;
    private Boolean activeRide;

    private Boolean loadFromFirebase = false;
    private GetFireBaseRide getFireBaseRide;

    public enum GetFireBaseRide{
        RIDE_PENDING,
        RIDE_INPROGRESS,
        NO_RIDE
    }

    /**
     * Checks for the bundle.
     *
     * @param savedInstanceState savedInstanceState
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_map_activity);
        offered = false;

        /* get the type of user driver/rider - Shared Pref*/
        userType = (UserType) getIntent().getSerializableExtra("userType");
        uid = getIntent().getStringExtra("uid");
        user = new User(uid);
        Log.wtf("STATS1", userType + "");
        findViewsSetListeners();

        FragmentManager fm = HomeMapActivity.this.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        switch (userType) {
            case RIDER:
                checkFireBaseRide(uid);
                break;
            case DRIVER:
                checkFireBaseDrive(uid);
                break;
        }
    }

    public void checkFireBaseRide(String UID){
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("testrequests");
        collectionReference
                .document(UID)
                .get()
                .addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    activeRide = true;

                    Log.d(TAG, "Document exists!");
                    Log.d(TAG, "Starting from Firebase");

                    GeoPoint start = document.getGeoPoint("startLocation");
                    GeoPoint end = document.getGeoPoint("endLocation");
                    String status = document.getString("rideStatus");

                    Log.d(TAG, "start "+ start);
                    Log.d(TAG, "end "+ end);
                    Log.d(TAG, "STATUSU "+ status);

                    startLatLng = new LatLng(start.getLatitude(), start.getLongitude());
                    destLatLng = new LatLng(end.getLatitude(), end.getLongitude());

                    /* there is a ride in progress but no confirmed driver */
                    if(status == null || status.equals("")){
                        getFireBaseRide = GetFireBaseRide.RIDE_PENDING;
                        getMapType();
                        getFragmentType(UID);
                    }
                    /* there is a confirmed driver driving the ride */
                    else {
                        getFireBaseRide = GetFireBaseRide.RIDE_INPROGRESS;
                        getMapType();
                        getFragmentType(UID);
                    }
                } else {
                    /* you have no requests in the firebase db */
                    Log.d(TAG, "Document does not exist!");
                    getFireBaseRide = GetFireBaseRide.NO_RIDE;
                    activeRide = false;
                    getMapType();
                    getFragmentType(UID);
                }
            } else { Log.wtf(TAG, "Failed with: ", task.getException()); }
        });
    }

    public void checkFireBaseDrive(String UID){
        // check firebase if you are in an active ride
        Log.wtf("8888888888", "0000000000");

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("testrequests");
        /* really bad */
        collectionReference.whereEqualTo("UIDdriver", UID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                /* should only be 1 here */
                                Log.wtf("8888888888", ""+documentSnapshots.exists());
                                if(documentSnapshots.exists()){
                                    Log.wtf("4444444",documentSnapshots.toString());
                                    String docID = documentSnapshots.getId();
                                    checkFireBaseRide(docID);
                                    return;
                                }
                            }
                        } else {
                            Log.wtf("4444444","nothing");
                            // task not successful
                            //error
                        }
                    }
                });
        collectionReference.whereArrayContains("offers", UID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                /* should only be 1 doc here */
                                Log.wtf("8888888888", ""+documentSnapshots.exists());
                                if(documentSnapshots.exists()){
                                    Log.wtf("4444444",documentSnapshots.toString());
                                    String docID = documentSnapshots.getId();
                                    checkFireBaseRide(docID);
                                    break;
                                }
                            }
                        } else {
                            Log.wtf("4444444","nothing");
                            // task not successful
                            //error
                        }
                    }
                });
        Log.d(TAG, "Document does not exist!");
        getFireBaseRide = GetFireBaseRide.NO_RIDE;
        activeRide = false;
        getMapType();
        getFragmentType(UID);
    }

    /**
     * sets the gets the map type depending on what kind of user a person is (rider, driver)
     */
    public void getMapType(){
        switch (userType){
            case RIDER:
            case DRIVER:
                /* there is an active ride in firebase */
                if(activeRide){
                    Log.wtf("22222", "isActive");
                    activeRideMapSetUp();
                }
                else {
                    Log.wtf("22222", "!isActive");
                    getLocationPermission();
                }
                break;
        }
    }

    /**
     * Shows the fragment types shown to either type of user
     */
    public void getFragmentType(String docID) {
        bundle = new Bundle();
        bundle.putSerializable("user", user);
        switch (userType) {
            /* cases for is the user is a rider */
            case RIDER:
                /* if there is no ride */
                switch (getFireBaseRide) {
                    case RIDE_INPROGRESS:
                    case RIDE_PENDING:
                        Log.wtf("000000", "in pending");
                        riderPendingFragment = new RideActiveFragment();
                        riderPendingFragment.setArguments(bundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, riderPendingFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    case NO_RIDE:
                        Log.wtf("000000", "in noride");
                        riderInactiveFragment = new RideInactiveFragment();
                        riderInactiveFragment.setArguments(bundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, riderInactiveFragment)
                                .addToBackStack(null)
                                .commit();
                        break;

                }
                break;
            /* cases for is the user is a driver */
            case DRIVER:
                /* if there is no ride */
                switch (getFireBaseRide) {
                    case RIDE_INPROGRESS:
                    case RIDE_PENDING:
                        Log.wtf("111111", "in pending");
                        driveActiveFragment = new DriveActiveFragment();
                        bundle.putSerializable("docID", docID);
                        driveActiveFragment.setArguments(bundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, driveActiveFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    case NO_RIDE: // TODO -- SLATED FOR REMOVAL START DRIVER AT LIST
                        Log.wtf("111111", "in noride");
                        driverInactiveFragment = new DriveInactiveFragment();
                        driverInactiveFragment.setArguments(bundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, driverInactiveFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                }
                break;
        }
    }

    /**
     * I mean, it finds the views and sets the listeners
     */
    public void findViewsSetListeners(){
        ImageButton hamburgerMenuBtn = findViewById(R.id.hamburger);
        hamburgerMenuBtn.setOnClickListener(this);
    }

    /*----------------------------- OVERRIDES -----------------------------------------------------*/

    /**
     * When an activity is started for a result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = this.getSupportFragmentManager();
        Log.wtf("ONRESULT", "ONRESULT");

        recreate();

    }

    /**
     * Handle the clicking in the acitvity
     * @param v
     */
    @Override
    public void onClick(View v) {
        HamburgerFragment hamburgerFragment = new HamburgerFragment();
        hamburgerFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, hamburgerFragment, userType.toString())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (offered) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeMapActivity.this);
            builder
                    .setTitle("Offer standing")
                    .setMessage("To leave return, you must resend your offer. Do you wish to remove your offer?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            manageOffer();
                            finish();
                        }
                    })
                    .show();
        }
        else {
            finish();
        }
    }

    public void manageOffer() {
        if (offered) {
            request.removeOffer(uid);
        }
        else {
            Intent intent = new Intent(this, DriverRequestListActivity.class);
            intent.putExtra("uid", user.getUid());
            startActivityForResult(intent, 1);
        }
        offered = !offered;
    }

    /**
     * This is a callback function. It is called when the map is ready.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(activeRide){
            activeRideMapOnReady();
        }
        else {
            inactiveMapRideOnReady(mMap);
        }
    }

    /**
     * Method to set up the map when there is no current active rides
     * @param mMap the google map to set up
     */
    private  void inactiveMapRideOnReady(GoogleMap mMap){
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    /**
     * Gets the user's device location and checks for permissions
     */
    private void getDeviceLocation(){
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){
                /* get the last location of device */
                final Task location = mFusedLocationProviderClient.getLastLocation();
                /* listen to if there is a location found */
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();
                        /* this method will move the view on the map  to you current location*/
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM);

                    }else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(HomeMapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    /**
     * Moves the camera of the maps view
     * @param latLng the longitude and latitude to start the view of the camera
     * @param zoom the zoom amount, how zoomed in the view will be
     */
    private void moveCamera(LatLng latLng, float zoom){
        /* if there is an active ride */
        if(activeRide){
            /* show the camera with two location points */
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startLatLng);
            builder.include(destLatLng);
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , DEFAULT_ZOOM) );
            mMap.setOnMapLoadedCallback(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 80)));
        }else{
            /* otherwise, just set at the current location */
            Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    /**
     * Initialize the map fragment
     */
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeMapActivity.this);
    }

    /**
     * Method to get the phone permissions of the user
     * - This is optional.
     * - If they deny long,late will be 0, 0
     * - If they accept it will show their current location
     */
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    /**
     * The map set up for if there is an active ride up for the user either in the database or from
     * the shared preferences
     */
    public void activeRideMapSetUp(){
        initMap();
        Log.wtf("22222", "ActiveMapReady1");
        /* sets the locations on the map */
        markStart = new MarkerOptions().position(startLatLng).title("Start Location");
        markDest = new MarkerOptions().position(destLatLng).title("Destination Location");
        markDest.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_dest_30));
        markStart.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_start_30));
        /* fetches the url */
        new FetchURL(HomeMapActivity.this)
                .execute(getUrl(markStart.getPosition(), markDest.getPosition(), "driving"), "driving");
    }

    /**
     * Just a method to set up the map
     */
    public void activeRideMapOnReady(){
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        moveCamera(startLatLng, DEFAULT_ZOOM);
        Log.wtf("22222", "ActiveMapReady2");
        addMarkers();
    }

    /**
     * Adds the markers on the map
     */
    public void addMarkers(){
        Log.wtf("22222", "ActiveMapReady3");
        mMap.addMarker(markStart);
        mMap.addMarker(markDest);
    }

    /**
     *  gets the url of the google maps directions info
     */
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output
                + "?"
                + parameters
                + "&key="
                + getString(R.string.google_maps_key);
        return url;
    }

    /**
     * when done with the map set up, draw the polylines between locations
     */
    @Override
    public void onTaskDone(Object... values) {
        if (currPolyline != null)
            currPolyline.remove();
        currPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}