package com.example.cabme.riders;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cabme.User;
import com.example.cabme.maps.CostAlgorithm;
import com.example.cabme.maps.JsonParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

/**
 * Purpose:
 * - Puts parsed stuff into a document in the requests (testrequests) collection
 *
 * FIREBASE RATIONALE:
 *
 * ** RIDER **
 * - Rider is limited to only one active ride request
 * - Rider has sub-collection ridehistory
 * - When a rider sends in a ride request it will go the requests collection for drivers to view
 * - ridehistory subcollection contains all the rides that are not currently ongoing
 *    - marked by status' completed, cancelled, etc
 *    - user will have that rideID connected to it
 *    - if they cancel it the rideID is null again for them
 *    - and the regular map is shown
 *    - new ride button turns into view active ride button
 * - when that ride is finished move the document to the rider's ride history
 *     - mark that ride as completed, cancelled, whatever is appropriate
 *
 * ** DRIVER **
 * - Driver views all the ride requests from every rider in the requests collection
 * - I dunno what happens after this i haven't thought about it yet
 *
 * TODO:
 *  [ ] Constructor for getting the file and editing it
 *  [ ] Constructor for moving the document to the users ride history
 *
 */
public class NewRideRequest {

    // Log tag
    private final String TAG = "NRR - requests";
    private final String TAG2 = "NRR - rideactive ";

    // Firebase things
    private transient FirebaseFirestore firebaseFirestore;
    private transient CollectionReference collectionReference;
    private String firebaseCollectionName = "testrequests";
    private String firebaseCollectionName2 = "rideactive";

    // KEYS
    private String API_KEY;
    private String UIDrider;

    // For document
    private Integer distanceValue;
    private Integer durationValue;

    private String distanceText;
    private String durationText;

    private String endAddress;
    private String startAddress;

    private Double rideCost;

    private GeoPoint startGeo;
    private GeoPoint endGeo;

    private String rideStatus = "";
    private String UIDdriver = "";

    private JsonParser jsonParser;
    private String DOCID;

    /**
     * Driver gets DOCID and changes the value of the doc when rider accepts offer
     * @param DOCID
     */
    public NewRideRequest(String DOCID){

    }

    /**
     *
     * Putting the document in the requests
     * @param startGeo
     * @param endGeo
     * @param UIDrider
     * @param API_KEY
     *
     */
    public NewRideRequest(GeoPoint startGeo, GeoPoint endGeo,
                          String UIDrider, String API_KEY, Double rideCost){
        setGiven(startGeo, endGeo, UIDrider, API_KEY);
        setParsedGeoPoints();
        setRideCost(rideCost);
        initializeFireBase();
        putInFirebaseCollection();
    }

    public void setGiven(GeoPoint startGeo, GeoPoint endGeo,
                         String UIDrider, String API_KEY){
        this.API_KEY = API_KEY;
        this.startGeo = startGeo;
        this.endGeo = endGeo;
        this.UIDrider = UIDrider;
    }

    private void setRideCost(Double rideCost){
//        CostAlgorithm costAlgorithm = new CostAlgorithm(distanceValue, durationValue);
//        this.rideCost = costAlgorithm.RideCost();
        this.rideCost = rideCost;

    }


    private void setParsedGeoPoints(){
        jsonParser = new JsonParser(startGeo, endGeo, API_KEY);
        this.distanceText = jsonParser.getDistanceText();
        this.distanceValue = jsonParser.getDistanceValue();
        this.durationText = jsonParser.getDurationText();
        this.durationValue = jsonParser.getDurationValue();
        this.endAddress = jsonParser.getEndAddress();
        this.startAddress = jsonParser.getStartAddress();

        Log.wtf("newrr", "distext: " + distanceText);
        Log.wtf("newrr", "disvalue: " + distanceValue);
        Log.wtf("newrr", "durtext: " + durationText);
        Log.wtf("newrr", "durvalue: " + durationValue);
        Log.wtf("newrr", "start: " + startAddress);
        Log.wtf("newrr", "end: " + endAddress);
    }

    private void initializeFireBase(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(firebaseCollectionName);
    }

    private void putInFirebaseCollection(){
        HashMap<String, Object> newRideRequest = new HashMap<>();
        newRideRequest.put("UIDdriver", UIDdriver);
        newRideRequest.put("UIDrider", UIDrider);
        newRideRequest.put("distanceText", distanceText);
        newRideRequest.put("distanceValue", distanceValue);
        newRideRequest.put("durationText", durationText);
        newRideRequest.put("durationValue", durationValue);
        newRideRequest.put("endAddress", endAddress);
        newRideRequest.put("startAddress", startAddress);
        newRideRequest.put("endLocation", endGeo);
        newRideRequest.put("startLocation", startGeo);
        newRideRequest.put("rideCost", rideCost);
        newRideRequest.put("rideStatus", rideStatus);


        collectionReference
                .document(UIDrider)
                .set(newRideRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Ride request added ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Ride request unable to be added "+ e.toString());
                    }
                });
    }

    public String getDOCID(){ return this.DOCID; }

}
