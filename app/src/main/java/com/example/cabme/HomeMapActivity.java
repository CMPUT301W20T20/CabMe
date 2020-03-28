package com.example.cabme;

import android.Manifest;
import android.content.Context;
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

import com.example.cabme.drivers.DriveInactiveFragment;
import com.example.cabme.maps.FetchURL;
import com.example.cabme.maps.TaskLoadedCallback;
import com.example.cabme.riders.RecreateType;
import com.example.cabme.riders.RideInactiveFragment;
import com.example.cabme.riders.RidePendingFragment;
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
 *
 */
public class HomeMapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, View.OnClickListener {
    private User user;
    private Bundle bundle;

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

    /* fragments */
    RideInactiveFragment riderInactiveFragment;
    RidePendingFragment riderPendingFragment;
    DriveInactiveFragment driverInactiveFragment;

    /**
     * Checks for the bundle.
     *
     * @param savedInstanceState
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_map_activity);

        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        activeRide = sharedPreferences.getBoolean("activeRide", false);

        String uid = getIntent().getStringExtra("user");
        user = new User(uid);

        UserType userType = (UserType) getIntent().getSerializableExtra("userType");
        Log.wtf("USERTYPE", userType + "");

        findViewsSetListeners();
        getMapType(sharedPreferences, userType);
        getFragmentType(userType);


    }

    /*----------------------------- SUP. ON CREATE ------------------------------------------------*/

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
                getLocationPermission();
                break;
        }
    }

    public void getFragmentType(UserType userType){
        bundle = new Bundle();
        bundle.putSerializable("user", user);
        switch (userType){
            case RIDER:
                Log.wtf("USERTYPE", "rider goes here");
                if(activeRide){
                    riderPendingFragment = new RidePendingFragment();
                    riderPendingFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_container, riderPendingFragment)
                            .commit();
                }
                else {
                    riderInactiveFragment = new RideInactiveFragment();
                    riderInactiveFragment.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_container, riderInactiveFragment)
                            .commit();
                }
                break;
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

    public void findViewsSetListeners(){
        ImageButton hamburgerMenuBtn = findViewById(R.id.hamburger);
        hamburgerMenuBtn.setOnClickListener(this);
    }

    public void recreateActivity(RecreateType recreateType, int resultCode, Intent data){
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch(recreateType){
            case REQUEST_SENT: // recreate on request sent
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
            case REQUEST_CANCELLED: // recreate on request cancelled
                activeRide = false;
                editor.remove("startLat");
                editor.remove("startLng");
                editor.remove("destLat");
                editor.remove("destLng");
                editor.putBoolean("activeRide", activeRide);
                editor.apply();
                recreate();
                break;
            case PROFILE_UPDATE: // recreate on profile change
                Log.wtf("RIDER MAP", "Successful ON HERE");
                editor.putBoolean("activeRide", activeRide);
                editor.apply();
                recreate();
                break;

        }
    }

    /*----------------------------- OVERRIDES -----------------------------------------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreateActivity(RecreateType.REQUEST_SENT, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        HamburgerFragment hamburgerFragment = new HamburgerFragment();
        hamburgerFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, hamburgerFragment)
                .commit();
    }

    /*----------------------------- MAPS/LOCATION/PERMISSION SETUP---------------------------------*/

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

    private void getDeviceLocation(){
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();

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

    private void moveCamera(LatLng latLng, float zoom){
        if(activeRide){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startLatLng);
            builder.include(destLatLng);
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , DEFAULT_ZOOM) );
            mMap.setOnMapLoadedCallback(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 80)));
        }else{
            Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeMapActivity.this);
    }

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

    public void activeRideMapSetUp(){
        initMap();
        markStart = new MarkerOptions().position(startLatLng).title("Start Location");
        markDest = new MarkerOptions().position(destLatLng).title("Destination Location");
        markStart.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_start_30));
        markDest.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_dest_30));
        new FetchURL(HomeMapActivity.this)
                .execute(getUrl(markStart.getPosition(), markDest.getPosition(), "driving"), "driving");
    }

    public void activeRideMapOnReady(){
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        moveCamera(startLatLng, DEFAULT_ZOOM);
        addMarkers();
    }

    public void addMarkers(){
        mMap.addMarker(markStart);
        mMap.addMarker(markDest);
    }

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

    @Override
    public void onTaskDone(Object... values) {
        if (currPolyline != null)
            currPolyline.remove();
        currPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}