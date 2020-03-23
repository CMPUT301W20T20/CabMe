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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

// Other classes
import com.example.cabme.maps.LongLat;
import com.example.cabme.maps.MapViewActivity;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
public class NewRideInfoActivity extends AppCompatActivity {

    Button SearchRideButton;
    EditText rideCostEditText;
    PlacesClient placesClient;
    User user;
    LongLat destLngLat;
    LongLat startLngLat;
    Double rideCost;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_newrideinfo_activity);
        SearchRideButton = (Button)findViewById(R.id.search_ride_button);
        rideCostEditText = (EditText) findViewById(R.id.pay_edit_text);

        // get user intent
        user = (User)getIntent().getSerializableExtra("user");

        // Initialize places client
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);

        startingLocationSearch();
        destinationLocationSearch();
        setSearchRideButton();
    }

    /**
     * Purpose:
     *
     * has auto complete functionality and sets start location latitude and longitude
     */
    public void startingLocationSearch(){
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autosearch_from);

        // Set Biased to Edmonton - Change to current location later on

        autocompleteSupportFragment.setHint("Starting Location");

        autocompleteSupportFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLing = place.getLatLng();
                startLngLat = new LongLat(latLing.longitude, latLing.latitude);
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

        // Set Biased to Edmonton - Change to current location later on

        autocompleteSupportFragment.setHint("Destination Location");

        autocompleteSupportFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLing = place.getLatLng();
                destLngLat = new LongLat(latLing.longitude, latLing.latitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("MAPSLOG", "Error onPlaceSelected");
            }
        });
    }

    public void setSearchRideButton(){
        SearchRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRideRequest();
//                Intent intent = new Intent(NewRideInfoActivity.this, MapViewActivity.class);
//                intent.putExtra("destLongLat", destLngLat);
//                intent.putExtra("startLongLat", startLngLat);
//                startActivity(intent);
            }
        });
    }

    /**
     * Purpose:
     *
     * set startLocation, endLocation, cost in the database
     */
    public  void addNewRideRequest(){
        GeoPoint destGeo = new GeoPoint(destLngLat.getLat(), destLngLat.getLng());
        GeoPoint startGeo = new GeoPoint(startLngLat.getLat(), startLngLat.getLng());
        JsonParser jsonParser = new JsonParser(startGeo, destGeo, getString(R.string.google_maps_key));
        CostAlgorithm costAlgorithm = new CostAlgorithm(jsonParser.getDistanceValue(), jsonParser.getDurationValue());
        rideCost = costAlgorithm.RideCost();
        new NewRideRequest(startGeo, destGeo, user.getUid(), getString(R.string.google_maps_key), rideCost);
    }
}
