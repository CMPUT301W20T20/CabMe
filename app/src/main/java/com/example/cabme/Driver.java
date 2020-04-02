package com.example.cabme;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This creates a sublass driver from the user class and stores their info 
 * including location in the database
 */
public class Driver extends User implements Serializable {
    final private String TAG = "Driver";
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String uid;
    private String phone;
    private Rating rating;
    private transient Location location;
    private transient FirebaseFirestore db;
    private transient CollectionReference collectionReference;

    /**
     * This takes driver's user id and location to fetch driver info from the database
     * and locate driver on the map.
     * @param uid
     * @param loc
     */
    public Driver(String uid, Location loc) {
        super(uid);
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        this.uid = uid;
        location = loc;
        Map<String, Object> data = new HashMap<>();
        data.put("location", loc);
        collectionReference
                .document(uid)
                .set(data, SetOptions.merge());
        // readData();
    }

    /**
     * This getches driver from the databse using driver's user id
     * drom the suers collection in the database
     * @param uid
     */
    public Driver(String uid){
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        this.uid = uid;
//        readData();
    }

    @Override
    /**
     * This method reads the driver info from the database using driver's uid
     * Info includes email, name, username, phone and rating of the driver
     * @param userCallback
     */
    public void readData(userCallback userCallback) {
        collectionReference
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    /**
                     * This gets the document snpshot of the driver's info when data is read
                     * @param documentSnapshot
                     */
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "Data retrieval successful");

                        email = documentSnapshot.getString("email");
                        firstName = documentSnapshot.getString("first");
                        lastName = documentSnapshot.getString("last");
                        username = documentSnapshot.getString("username");
                        phone = documentSnapshot.getString("phone");
                        rating = documentSnapshot.get("rating", Rating.class);
                        userCallback.onCallback(email, firstName, lastName, username, phone, rating);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    /**
                     * This raises an exception when the driver data is not read from the database
                     * @param e
                     */
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data retrieval failed " + e.toString());
                    }
                });
    }

    @Override
    public void setDocumentListener() {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        collectionReference.document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            /**
             * This allows to get driver info in case an empty snapshot of driver info is obtained
             * @param documentSnapshot
             * @param e
             */
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot == null) {
                    return;
                }
                username = documentSnapshot.getString("username");
                firstName = documentSnapshot.getString("first");
                lastName = documentSnapshot.getString("last");
                email = documentSnapshot.getString("email");
                phone = documentSnapshot.getString("phone");
                rating = documentSnapshot.get("rating", Rating.class);
                Log.d("BIG", "UPDATE");
                notifyObservers();
            }
        });
    }

    /**
     * Returns the location of the driver
     * @return
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Returns the rating of the driver
     * @return
     */
    public Rating getRating() {
        return rating;
    }

    /**
     * Returns the email of the driver
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the first name of the driver
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the driver
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the username of the driver
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the phone number of the driver
     * @return
     */
    public String getPhone() {
        return phone;
    }

    /** Returns the unique user id of the driver */
    public String getUid() {
        return uid;
    }
}
