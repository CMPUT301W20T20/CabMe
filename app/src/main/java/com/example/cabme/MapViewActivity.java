package com.example.cabme;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback{
    private MapView mapView;
    private GoogleMap googleMap;
    Button driverButton;

    /**
     * Checks for the bundle.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_mapview_activity);
    }

    /**
     * Making a callback function for when the map object is ready.
     * As the map is read,
     * The onMapReady function is called to go throught the given MoodEvent.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_mapview_activity, container, false);
        driverButton = findViewById(R.id.driver_name);
        mapView = findViewById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
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
//        LatLng startLatLng = new LatLng(53.55014, -113.46871);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.clear();
    }


}
