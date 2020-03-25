package com.example.cabme.drivers;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.GeoPoint;

/**
 *
 * request list model for driver side requests
 *
 */
public class RiderRequestsModel {
    private GeoPoint startLoc;
    private GeoPoint destLoc;
    private String UID;

    private transient CollectionReference collectionReference;

    public RiderRequestsModel(){}

    public RiderRequestsModel(String UID, GeoPoint startLoc, GeoPoint destLoc){
        this.UID = UID;
        this.startLoc = startLoc;
        this.destLoc = destLoc;
    }

    public String getUID(){
        Log.wtf("XX", UID+"");
        return UID;
    }

    public Double getStartLat(){
        return startLoc.getLatitude();
    }

    public Double getStartLng(){
        return startLoc.getLongitude();
    }

    public Double getDestLat(){
        return destLoc.getLatitude();
    }

    public Double getDestLng(){
        return destLoc.getLongitude();
    }
}