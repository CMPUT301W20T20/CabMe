package com.example.cabme.riders;

import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cabme.HomeMapActivity;
import com.example.cabme.Rating;
import com.example.cabme.User;
import com.example.cabme.maps.CostAlgorithm;
import com.example.cabme.maps.JsonParser;
import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 *  [X] Constructor for getting the file and editing it
 *  [X] Constructor for moving the document to the users ride history
 *
 */
public class RideRequest implements Serializable {

    private String TAG = "LOG";

    /* FireBase things */
    private transient FirebaseFirestore firebaseFirestore;
    private transient CollectionReference collectionReference;
    private transient DocumentReference documentReference;
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
                       String UIDrider, String API_KEY, Double rideCost, requestCallback requestCallback) {
        setGiven(startGeo, endGeo, UIDrider, API_KEY);
        setParsedGeoPoints();
        setRideCost(rideCost);
        initializeFireBase();
        putInFirebaseCollection(requestCallback);
    }

    /**
     * This method sets the given variables.
     * This method is called in the RideRequest() method.
     * Purpose: set the given variables, used in NewRideRequest
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
    }

    /**
     * This method put the ride request information from the variables to a document in the Firebase
     * collection where the drivers can view each riders' ride request.
     */
    public void putInFirebaseCollection(requestCallback requestCallback) {
        HashMap<String, Object> newRideRequest = new HashMap<>();
        newRideRequest.put("UIDdriver", UIDdriver);
        newRideRequest.put("UIDrider", UIDrider);
        newRideRequest.put("offers", new ArrayList());
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
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Ride request added ");
                    requestCallback.onCallback();
                })
                .addOnFailureListener(e -> Log.d(TAG, "Ride request unable to be added " + e.toString()));
    }

    /**
     * This method removes a the ride request tied to a user's ID in the Firebase collection where
     * the drivers can view each riders' ride request. .
     */
    public void removeRequest(requestCallback requestCallback) {
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
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Ride request deleted ");
                                                            requestCallback.onCallback();
                                                        }
                                                    });
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

    public interface requestCallback{
        void onCallback();
    }

    /**
     * This method update the rideStatus field of the ride request
     * @param status the status of the current ride (cancelled, completed)
     */
    public void updateRideStatus(String status){
        Map<String, Object> data = new HashMap<>();
        data.put("rideStatus", status);
        collectionReference
                .document(UIDrider)
                .set(data, SetOptions.merge());
    }

    /**
     * This method update the UIDdriver field of the ride request
     * @param UIDdriver the id of the driver
     */
    public void updateDriver(String UIDdriver){
        Map<String, Object> data = new HashMap<>();
        data.put("UIDdriver", UIDdriver);
        collectionReference
                .document(UIDrider)
                .set(data, SetOptions.merge());
    }

    public void addOffer(String UIDdriver){
        collectionReference
                .document(UIDrider)
                .update("offers", FieldValue.arrayUnion(UIDdriver));
    }

    public void removeOffer(String UIDdriver){
        collectionReference
                .document(UIDrider)
                .update("offers", FieldValue.arrayRemove(UIDdriver));
    }

    public void readData(RideRequest.rideCallBack rideCallBack) {
        collectionReference
                .document(UIDrider)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "Data retrieval successful");

                        GeoPoint start = documentSnapshot.getGeoPoint("startLocation");
                        GeoPoint end = documentSnapshot.getGeoPoint("endLocation");
                        LatLng s = new LatLng(start.getLatitude(), start.getLongitude());
                        LatLng e = new LatLng(end.getLatitude(), end.getLongitude());
                        rideCallBack.onCallback(s, e);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data retrieval failed " + e.toString());
                    }
                });
    }

    public interface rideCallBack{
        void onCallback(LatLng start, LatLng end);
    }

    public void readData(RideRequest.dataCallBack dataCallBack) {
        collectionReference
                .document(UIDrider)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "Data retrieval successful");
                        String UID = documentSnapshot.getString("UIDdriver");
                        String rideStatus = documentSnapshot.getString("rideStatus");
                        String startAddress = documentSnapshot.getString("startAddress");
                        String endAddress = documentSnapshot.getString("endAddress");
                        Double fare = documentSnapshot.getDouble("rideCost");

                        dataCallBack.onCallback(UID, rideStatus, startAddress, endAddress, fare);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data retrieval failed " + e.toString());
                    }
                });
    }

    public interface dataCallBack{
        void onCallback(String driverID, String status, String startAddress, String endAddress, Double fare);
    }

    /**
     * Purpose: to move documents to another collection. This will copy and delete the original document
     *
     * Credits: Stack Overflow: Alex Mamo (user:5246885) and Rafa (user:2619107)
     *          link: https://stackoverflow.com/questions/47244403/how-to-move-a-document-in-cloud-firestore
     *
     * @param fromPath
     * @param toPath
     */
    public void moveFirestoreDocument(DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        fromPath.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
