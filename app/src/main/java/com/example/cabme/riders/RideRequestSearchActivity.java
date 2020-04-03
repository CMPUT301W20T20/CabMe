package com.example.cabme.riders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabme.HomeMapActivity;
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

import java.text.DecimalFormat;
import java.util.Arrays;

import static android.view.View.GONE;

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
 * TODO
 *  [ ] Text Watcher - err.ch. valid input
 *  [ ] OnClick - else
 *  [ ] Handle case where there is NO DRIVABLE ROUTE between locations coz app goes nuts and crashes
 *  [ ] comments
 *
 */
public class RideRequestSearchActivity extends AppCompatActivity implements View.OnClickListener {

    public PlacesClient placesClient;
    public User user;
    private TextView rideCostEditText;
    private Button searchRideButton;
    private LatLng destLngLat;
    private LatLng startLngLat;
    private GeoPoint startGeo;
    private GeoPoint destGeo;

    private Boolean textChanged  = false;

    private Button addTip;
    private Button confirmTip;
    private Button changeTip;

    private TextView faretext;
    private EditText amtTip;
    private Double tip = 0.00 ;
    private Double rideCost;
    private Double customCost;


    private  AutocompleteSupportFragment asfStart;
    private AutocompleteSupportFragment asfDest;

    String rideCostPreview;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_ride_request_search_activity);
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
        searchRideButton = (Button) findViewById(R.id.search_ride_button);
        rideCostEditText = (TextView) findViewById(R.id.pay_edit_text);
        addTip = (Button) findViewById(R.id.AddTip);
        confirmTip = (Button) findViewById(R.id.ConfirmTip);
        changeTip = (Button) findViewById(R.id.ChangeTip);
        amtTip = (EditText) findViewById(R.id.tipAmt);
        faretext = (TextView) findViewById(R.id.faretext) ;

        searchRideButton.setOnClickListener(this);
        amtTip.addTextChangedListener(costWatcher);
        addTip.setOnClickListener(this);
        confirmTip.setOnClickListener(this);
        changeTip.setOnClickListener(this);
    }

    public void startingLocationSearch(){
        asfStart = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autosearch_from);
        asfStart.setHint("Starting Location");
        asfStart.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME));
        asfStart.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                startLngLat = place.getLatLng();
                if(destLngLat!= null){
                    costEstimator();
                }
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.d("onPlaceSelected", "Error onPlaceSelected start location");
            }
        });
    }

    public void destinationLocationSearch(){
        asfDest = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autosearch_to);
        asfDest.setHint("Destination Location");
        asfDest.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME));
        asfDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                destLngLat = place.getLatLng();
                if(startLngLat != null){
                    costEstimator();
                }
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.d("onPlaceSelected", "Error onPlaceSelected destination location");
            }
        });
    }

    public void costEstimator(){
        destGeo = new GeoPoint(destLngLat.latitude, destLngLat.longitude);
        startGeo = new GeoPoint(startLngLat.latitude, startLngLat.longitude);

        JsonParser jsonParser = new JsonParser(startGeo, destGeo, getString(R.string.google_maps_key));
        CostAlgorithm costAlgorithm = new CostAlgorithm(jsonParser.getDistanceValue(), jsonParser.getDurationValue());
        rideCost = costAlgorithm.RideCost();

        rideCostPreview = ""+ rideCost;
        rideCostEditText.setText("$" + rideCostPreview);
    }

    public void addNewRideRequest(){
        if(customCost == null){
            new RideRequest(startGeo, destGeo, user.getUid(), getString(R.string.google_maps_key), rideCost, new RideRequest.requestCallback() {
                @Override
                public void onCallback() {
                    Intent intent = new Intent();
                    intent.putExtra("startLatLng", startLngLat);
                    intent.putExtra("destLatLng", destLngLat);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            new RideRequest(startGeo, destGeo, user.getUid(), getString(R.string.google_maps_key), customCost, new RideRequest.requestCallback() {
                @Override
                public void onCallback() {
                    Intent intent = new Intent();
                    intent.putExtra("startLatLng", startLngLat);
                    intent.putExtra("destLatLng", destLngLat);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    /* Correct input in the cost field - do this later */
    private TextWatcher costWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textChanged = true;
        }
        @Override
        public void afterTextChanged(Editable s) {
            textChanged = true;
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.search_ride_button:
                if(destLngLat != null && startLngLat != null)
                {
                    addNewRideRequest();
                } else {
                    Toast.makeText(RideRequestSearchActivity.this,"Empty Field(s) not valid", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.AddTip:
                addTip.setVisibility(View.GONE);
                confirmTip.setVisibility(View.VISIBLE);
                amtTip.setVisibility(View.VISIBLE);
                break;
            case R.id.ConfirmTip:
                if(textChanged){
                    String smt = amtTip.getText().toString();
                    if(!smt.equals("")){
                        double amt = Double.parseDouble(amtTip.getText().toString());
                        recalcRideCost(amt);
                    }else { addTip.setVisibility(View.VISIBLE); }
                }else {
                    addTip.setVisibility(View.VISIBLE);
                }
                confirmTip.setVisibility(View.GONE);
                amtTip.setVisibility(View.GONE);
                break;
            case R.id.ChangeTip:
                changeTip.setVisibility(GONE);
                confirmTip.setVisibility(View.VISIBLE);
                amtTip.setVisibility(View.VISIBLE);

                break;
        }
    }

    public void recalcRideCost(Double tip){
        if(rideCost != null){
            tip = Math.floor(tip * 100) / 100;
            customCost  = rideCost + tip;
            DecimalFormat dec = new DecimalFormat("#.00");
            rideCostEditText.setText("$"+ dec.format(customCost));
            faretext.setText("Custom Fare");
            changeTip.setVisibility(View.VISIBLE);
        }
        else{
            amtTip.setText("");
            addTip.setVisibility(View.VISIBLE);
            Toast toast= Toast.makeText(getApplicationContext(),"You cant tip yet!",Toast. LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}