package com.example.cabme.maps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.cabme.R;
import com.example.cabme.qrscanner.ScannerQR;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 *
 * i already forgot about this classes purpose lol
 *
 */
public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback{
    Button AcceptOffer;
    Button AcceptPayment;

    GoogleMap googleMap;

    // Directions
    LongLat destLngLat;
    LongLat startLngLat;
    LatLng startLatLng;
    LatLng destLatLng;

    // Drawing Lines & Markers
    Polyline currPolyline;
    MarkerOptions markStart;
    MarkerOptions markDest;

    Boolean Rider = true;

    /**
     * Checks for the bundle.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_mapview_activity);

        AcceptOffer = findViewById(R.id.AcceptOffer);
        AcceptPayment = findViewById(R.id.AcceptPayment);

        destLngLat = (LongLat) getIntent().getSerializableExtra("destLongLat");
        startLngLat = (LongLat) getIntent().getSerializableExtra("startLongLat");
        Rider = (Boolean) getIntent().getSerializableExtra("isRider");

        startLatLng = new LatLng(startLngLat.getLat(), startLngLat.getLng());
        destLatLng = new LatLng(destLngLat.getLat(), destLngLat.getLng());

        markStart = new MarkerOptions().position(startLatLng).title("Start Location");
        markDest = new MarkerOptions().position(destLatLng).title("Destination Location");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapViewActivity.this);

        new FetchURL(MapViewActivity.this)
                .execute(getUrl(markStart.getPosition(), markDest.getPosition(), "driving"), "driving");

        AcceptOffer.setOnClickListener(v -> {
            Toast.makeText(MapViewActivity.this, "Offer is Accepted", Toast.LENGTH_LONG).show();
            AcceptPayment.setVisibility(View.VISIBLE);
            AcceptOffer.setVisibility(View.GONE);
            //Delete the request from the Data base
        });


        AcceptPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapViewActivity.this, ScannerQR.class));
                Toast.makeText(MapViewActivity.this, "Opening Scanner...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void setRiderFalse(){
        Rider = false;
    }

    /**
     * This is a callback function. It is called when the map is ready.
     *
     * @param map
     */
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap(map);

    }

    public void setUpMap(GoogleMap map){
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startLatLng);
        builder.include(destLatLng);
        addMarkers(map, startLatLng, destLatLng);

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
            }
        });
    }

    public void addMarkers(GoogleMap map, LatLng start, LatLng dest){
        Log.wtf("MAPSLOG-MVA Start", "StartLatLng: "+ startLngLat.getLat() + "\n" + startLngLat.getLng());
        Log.wtf("MAPSLOG-MVA Dest", "DestLatLng: "+ destLngLat.getLat() + "\n" + destLngLat.getLng());
        map.addMarker(markStart);
        map.addMarker(markDest);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currPolyline != null)
            currPolyline.remove();
        currPolyline = googleMap.addPolyline((PolylineOptions) values[0]);
    }

}