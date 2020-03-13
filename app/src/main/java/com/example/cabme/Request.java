package com.example.cabme;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.GeoPoint;

public class Request {
    private GeoPoint startLoc;
    private GeoPoint destLoc;
    private String UID;

    private transient CollectionReference collectionReference;

    public Request(){}

    public Request(String UID, GeoPoint startLoc, GeoPoint destLoc){
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
