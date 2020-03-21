package com.example.cabme;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 *  User class meant to pull and provide information to the FireBase database about the users
 *  involved in the CabMe application
 */
public class User extends Observable implements Serializable {
    final private String TAG = "User";
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String uid;
    private String phone;
    private Rating rating;
    private Location location;
    //private int balance;
    private transient FirebaseFirestore db;
    private transient CollectionReference collectionReference;

    /**
     * This constructor is for users that are logging in or getting information of other
     * user involved in the current request
     *
     * @param uid
     */
    public User (String uid) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        this.uid = uid;
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
                        location = (Location) documentSnapshot.get("location");
                        rating = (Rating) documentSnapshot.get("rating");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data retrieval failed " + e.toString());
                    }
                });
        setDocumentListener();
    }

    /**
     * This constructor is for users that are signing up for the CabMe application
     *
     * @param uid
     * @param email
     * @param firstName
     * @param lastName
     * @param username
     * @param phone
     */
    public User (String uid, String email, String firstName, String lastName, String username, String phone) {

        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        this.uid = uid;

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("first", firstName);
        userData.put("last", lastName);
        userData.put("username", username);
        userData.put("phone", phone);
        userData.put("rating", rating);
        userData.put("location", location);

        collectionReference
                .document(uid)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data addition successful");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data addition failed "+ e.toString());
                    }
                });

        //setDocumentListener(uid);

    }

    /**
     * This method sets a listener to the user's document in the database to retrieve real-time
     * updates from the database
     *
     *
     */
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
                location = (Location) documentSnapshot.get("location");
                rating = (Rating) documentSnapshot.get("rating");
                Log.d("BIG", "UPDATE");
                notifyObservers();
            }
        });

    }
	public User() {
		// Default constructor required for calls to DataSnapshot.getValue(User.class)
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

    public String getUid() {
        return uid;
    }

    public Location getLocation() {
        return location;
    }

    public Rating getRating() {
        return rating;
    }

    public void updateData(Map<String, Object> data) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        collectionReference
                .document(uid)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data update successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data update failed "+ e.toString());
                    }
                });
    }



}
