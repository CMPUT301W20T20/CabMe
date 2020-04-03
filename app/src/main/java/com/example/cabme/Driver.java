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

    public Driver(String uid){
        super(uid);
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        this.uid = uid;
    }

    @Override
    public void readData(userCallback userCallback) {
        collectionReference
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
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


    public Location getLocation() {
        return location;
    }

    public Rating getRating() {
        return rating;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

}
