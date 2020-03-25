package com.example.cabme.riders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabme.R;
import com.example.cabme.User;
import com.example.cabme.maps.CostAlgorithm;
import com.example.cabme.maps.JsonParser;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;

/**
 *
 *
 * Purposes:
 * - When a rider makes a new ride request the places/maps/directions API used to parse JSON files from
 *   the request made then use the information extracted from class to store ride requests from the
 *   Firebase DB.
 * - Literally just parses information (if there is any - some long&lats may be null - UNHANDLED CASE)
 *   from a start and end location via google. You can use this to get the actual address of a place
 *
 * Params
 * - Geopoint:: start location
 * - Geopoint:: end location
 * - String:: API key ==> *NEEDED* to access information from request. SEE URL
 *
 */
public class RideRequestSearchActivity extends AppCompatActivity implements View.OnClickListener {

    public PlacesClient placesClient;
    public User user;
    private LatLng destLngLat;
    private LatLng startLngLat;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_newrideinfo_activity);

        user = (User)getIntent().getSerializableExtra("user");

        initializePlacesClient();
        findViewsSetListeners();
        startingLocationSearch();
        destinationLocationSearch();
    }

    private void initializePlacesClient(){
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);
    }

    private void findViewsSetListeners(){
        Button searchRideButton = (Button) findViewById(R.id.search_ride_button);
        EditText rideCostEditText = (EditText) findViewById(R.id.pay_edit_text);
        searchRideButton.setOnClickListener(this);
    }

    /**
     * Purpose:
     *
     * has auto complete functionality and sets start location latitude and longitude
     */
    public void startingLocationSearch(){
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autosearch_from);

        autocompleteSupportFragment.setHint("Starting Location");

        autocompleteSupportFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                startLngLat = place.getLatLng();
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.d("Error", "Error");
            }
        });
    }

    /**
     * Purpose:
     *
     * has autocomplete functionality and sets the destination latitude and longitude
     */
    public void destinationLocationSearch(){
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autosearch_to);

        autocompleteSupportFragment.setHint("Destination Location");

        autocompleteSupportFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destLngLat = place.getLatLng();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("MAPSLOG", "Error onPlaceSelected");
            }
        });
    }

    public void addNewRideRequest(){
        GeoPoint destGeo = new GeoPoint(destLngLat.latitude, destLngLat.longitude);
        GeoPoint startGeo = new GeoPoint(startLngLat.latitude, startLngLat.longitude);

    /**
     * Purpose:
     *
     * set startLocation, endLocation, cost in the database
     */
    public void addNewRideRequest(){
        GeoPoint destGeo = new GeoPoint(destLngLat.getLat(), destLngLat.getLng());
        GeoPoint startGeo = new GeoPoint(startLngLat.getLat(), startLngLat.getLng());
        JsonParser jsonParser = new JsonParser(startGeo, destGeo, getString(R.string.google_maps_key));
        CostAlgorithm costAlgorithm = new CostAlgorithm(jsonParser.getDistanceValue(), jsonParser.getDurationValue());
        Double rideCost = costAlgorithm.RideCost();

        new RideRequest(startGeo, destGeo, user.getUid(), getString(R.string.google_maps_key), rideCost);
    }

    @Override
    public void onClick(View v) {
        addNewRideRequest();
        Intent intent = new Intent();
        intent.putExtra("startLatLng", startLngLat);
        intent.putExtra("destLatLng", destLngLat);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
