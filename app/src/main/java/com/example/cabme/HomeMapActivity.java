package com.example.cabme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentTransaction;

import com.example.cabme.drivers.DriveInactiveFragment;
import com.example.cabme.maps.FetchURL;
import com.example.cabme.maps.TaskLoadedCallback;
import com.example.cabme.riders.RecreateType;
import com.example.cabme.riders.RideInactiveFragment;
import com.example.cabme.riders.RidePendingFragment;
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
import com.google.android.gms.tasks.Task;

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
 *  [ ] finish rider side basic no bug checks
 *  [ ] LOAD FROM FIRE BASE when you log in check if you have any requests in fireBase - Online
 *  [x] opens from shared preference for backup
 *  [ ] COMMENTS :/
 *  [x] fix fragments stacking
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

    private Boolean activeRide;
    private LatLng startLatLng;
    private LatLng destLatLng;

    private boolean offered;
    /* fragments */
    RideInactiveFragment riderInactiveFragment;
    RidePendingFragment riderPendingFragment;
    DriveInactiveFragment driverInactiveFragment;

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

        /* check if there is an active ride saved in SharedPrefs */
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        /* set active ride to true if there is, otherwise false */
        activeRide = sharedPreferences.getBoolean("activeRide", false);


        uid = getIntent().getStringExtra("user");
        user = new User(uid);

        userType = (UserType) getIntent().getSerializableExtra("userType");

        Log.wtf("USERTYPE", userType + "");

        findViewsSetListeners();
        getMapType(sharedPreferences, userType);
        getFragmentType(userType);


    }

    /*----------------------------- SUP. ON CREATE ------------------------------------------------*/

    /**
     * sets the gets the map type depending on what kind of user a person is (rider, driver)
     * @param sharedPreferences the information containing if there was an active ride saved
     * @param userType the typer of user a user is (rider, driver)
     */
    public void getMapType(SharedPreferences sharedPreferences, UserType userType){
        switch (userType){
            case RIDER:
                if(activeRide){
                    startLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString("startLat", "")),
                            Double.parseDouble(sharedPreferences.getString("startLng", "")));
                    destLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString("destLat", "")),
                            Double.parseDouble(sharedPreferences.getString("destLng", "")));
                    activeRideMapSetUp();
                }
                else{
                    getLocationPermission();
                }
                break;
            case DRIVER:
                rid = getIntent().getStringExtra("request");
                request = new RideRequest(rid);
                if(activeRide){
                    startLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString("startLat", "")),
                            Double.parseDouble(sharedPreferences.getString("startLng", "")));
                    destLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString("destLat", "")),
                            Double.parseDouble(sharedPreferences.getString("destLng", "")));
                    activeRideMapSetUp();
                }
                else{
                    recreateActivity(RecreateType.REQUEST_SENT, RESULT_OK, this.getIntent());
                }
                break;
        }
    }

    /**
     * Shows the fragment types shown to either type of user
     * @param userType the type of user a person using the app is
     */
    public void getFragmentType(UserType userType){
        bundle = new Bundle();
        bundle.putSerializable("user", user);
        switch (userType){
            /* cases for is the user is a rider */
            case RIDER:
                Log.wtf("USERTYPE", "rider goes here");
                /* if there is already an active ride and they are a rider */
                if(activeRide){
                    /* show the ride pending offers fragment on screen */
                    riderPendingFragment = new RidePendingFragment();
                    riderPendingFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_container, riderPendingFragment)
                            .commit();
                }
                /* there is no active ride */
                else {
                    /* show the inactive fragment where the user can view history and request a ride */
                    riderInactiveFragment = new RideInactiveFragment();
                    riderInactiveFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_container, riderInactiveFragment)
                            .commit();
                }
                break;
            /* cases for is the user is a driver */
            case DRIVER:
                Log.wtf("USERTYPE", "driver goes here");
                driverInactiveFragment = new DriveInactiveFragment();
                driverInactiveFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, driverInactiveFragment)
                        .commit();
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

    /**
     * handles each time the activity is recreated
     * @param recreateType the type of recreation; what is recreated
     * @param resultCode the result code from onActivityResult
     * @param data the intent
     */
    public void recreateActivity(RecreateType recreateType, int resultCode, Intent data){
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch(recreateType){
            /* recreate on request sent */
            case REQUEST_SENT:
                if(resultCode==RESULT_OK){
                    startLatLng = data.getParcelableExtra("startLatLng");
                    destLatLng = data.getParcelableExtra("destLatLng");
                    activeRide = true;
                    editor.putString("startLat", String.valueOf(startLatLng.latitude));
                    editor.putString("startLng", String.valueOf(startLatLng.longitude));
                    editor.putString("destLat", String.valueOf(destLatLng.latitude));
                    editor.putString("destLng", String.valueOf(destLatLng.longitude));
                    editor.putBoolean("activeRide", activeRide);
                    editor.apply();
                    recreate();
                }
                else { recreate(); }
                break;
            /* recreate when cancelled */
            case REQUEST_CANCELLED: // recreate on request cancelled
                activeRide = false;
                /* remove the lication values */
                editor.remove("startLat");
                editor.remove("startLng");
                editor.remove("destLat");
                editor.remove("destLng");
                /* communicate that the active ride is false */
                editor.putBoolean("activeRide", activeRide);
                editor.apply();
                /* recreate the activity */
                recreate();
                break;
            /* when the profile is updated recreate to show the changes*/
            case PROFILE_UPDATE:
                Log.wtf("RIDER MAP", "Successful ON HERE");
                getSupportFragmentManager().beginTransaction().remove(riderInactiveFragment).commit();
                recreate();
                break;

        }
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
        recreateActivity(RecreateType.REQUEST_SENT, resultCode, data);
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

    /*----------------------------- MAPS/LOCATION/PERMISSION SETUP---------------------------------*/

    public void manageOffer() {
        if (offered) {
            request.removeOffer(uid);
        }
        else {
            request.addOffer(uid);
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
        /* sets the locations on the map */
        markStart = new MarkerOptions().position(startLatLng).title("Start Location");
        markDest = new MarkerOptions().position(destLatLng).title("Destination Location");
        markStart.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_start_30));
        markDest.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_dest_30));
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
        addMarkers();
    }

    /**
     * Adds the markers on the map
     */
    public void addMarkers(){
        mMap.addMarker(markStart);
        mMap.addMarker(markDest);
    }

    /* gets the url of the google maps directions info */
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

    /* when done with the map set up, draw the polylines between locations */
    @Override
    public void onTaskDone(Object... values) {
        if (currPolyline != null)
            currPolyline.remove();
        currPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}