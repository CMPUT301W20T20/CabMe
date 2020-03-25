package com.example.cabme.riders;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cabme.User;
import com.example.cabme.maps.CostAlgorithm;
import com.example.cabme.maps.JsonParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
 * TODO:
 *  [ ] Constructor for getting the file and editing it
 *  [ ] Constructor for moving the document to the users ride history
 *
 */
public class RideRequest {

    private String TAG = "LOG";

    /* FireBase things */
    private transient FirebaseFirestore firebaseFirestore;
    private transient CollectionReference collectionReference;
    private transient DocumentReference documentReference;
    private transient DatabaseReference databaseReference;
    private String firebaseCollectionName = "testrequests";

    /* Keys */
    private String API_KEY;
    private String UIDrider;

    /* Variables used in the ride request */
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

    /**
     * This contructs a ride request with specificed rider ID.
     *
     * @param reqUserID the user ID which is also the request ID of the ride request
     */
    public RideRequest(String reqUserID) {
        UIDrider = reqUserID;
        initializeFireBase();
    }

    /**
     * This constructs a ride request with the specified start and end location and rider ID.
     *
     * @param startGeo start location of the ride request
     * @param endGeo   end location of the ride request
     * @param UIDrider ID of the rider
     * @param API_KEY  the Google API key
     */
    public RideRequest(GeoPoint startGeo, GeoPoint endGeo,
                       String UIDrider, String API_KEY, Double rideCost) {
        setGiven(startGeo, endGeo, UIDrider, API_KEY);
        setParsedGeoPoints();
        setRideCost(rideCost);
        initializeFireBase();
        putInFirebaseCollection();
    }

    /**
     * This method sets the given variables.
     * This method is called in the RideRequest() method.
     *
     * @param startGeo start location of the ride request
     * @param endGeo   end location of the ride request
     * @param UIDrider ID of the rider
     * @param API_KEY  Google API key
     */
    public void setGiven(GeoPoint startGeo, GeoPoint endGeo,
                         String UIDrider, String API_KEY) {
        this.API_KEY = API_KEY;
        this.startGeo = startGeo;
        this.endGeo = endGeo;
        this.UIDrider = UIDrider;
    }

    /**
     * This method sets the cost of the ride request.
     * This method is called in the RideRequest method.
     *
     * @param rideCost the cost of the ride
     */
    private void setRideCost(Double rideCost) {
        this.rideCost = rideCost;
    }

    /**
     * This method uses the JsonParser Class to parse the start and end location and sets the
     * JSON parsed information of the ride request to its appropriate variable.
     */
    private void setParsedGeoPoints() {
        jsonParser = new JsonParser(startGeo, endGeo, API_KEY);
        this.distanceText = jsonParser.getDistanceText();
        this.distanceValue = jsonParser.getDistanceValue();
        this.durationText = jsonParser.getDurationText();
        this.durationValue = jsonParser.getDurationValue();
        this.endAddress = jsonParser.getEndAddress();
        this.startAddress = jsonParser.getStartAddress();
    }

    /**
     * This method initializes the FireBase and and the collection and document reference.
     * This method is called in the constructors.
     */
    private void initializeFireBase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(firebaseCollectionName);
        documentReference = firebaseFirestore.collection(firebaseCollectionName).document(UIDrider);
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    /**
     * This method put the ride request information from the variables to a document in the Firebase
     * collection where the drivers can view each riders' ride request.
     */
    private void putInFirebaseCollection() {
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
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Ride request added "))
                .addOnFailureListener(e -> Log.d(TAG, "Ride request unable to be added " + e.toString()));
    }

    /**
     * This method removes a the ride request tied to a user's ID in the Firebase collection where
     * the drivers can view each riders' ride request. .
     */
    public void removeRequest() {
        String DOCID = FirebaseDatabase.getInstance().getReference("ridehistory").push().getKey();
        DocumentReference ridehistoryRef = firebaseFirestore
                .collection("users")
                .document(UIDrider)
                .collection("ridehistory")
                .document(DOCID);

        documentReference
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            ridehistoryRef.set(documentSnapshot.getData())
                                    .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                            documentReference
                                                    .delete()
                                                    .addOnFailureListener(e -> Log.d(TAG, "Ride request unable to be deleted "+ e.toString()))
                                                    .addOnSuccessListener(aVoid1 -> Log.d(TAG, "Ride request deleted "));
                                        }
                                    )
                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                        }
                        else {
                            Log.d(TAG, "No such document");
                        }
                    }
                });
    }

    public void updateRideStatus(String status){
        // GETS PERMISSION ERROR
        databaseReference
                .child(firebaseCollectionName)
                .child(UIDrider)
                .child("rideStatus")
                .setValue(status)
                .addOnSuccessListener(aVoid -> Log.v("Document Update", "Sucessfully updated Document"))
                .addOnFailureListener(e -> Log.v("Document Update", "Something went wrong updating " + e.toString()));
    }
}
