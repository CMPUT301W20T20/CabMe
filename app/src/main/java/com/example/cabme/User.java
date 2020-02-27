package com.example.cabme;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

public class User extends CModel implements Serializable {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String uid;
    private String phone;
    private int balance;
    private FirebaseFirestore db;

    User (String email, String password) {
        // ToDo : Add authentication and retrieval of profile info from FireBase and set user data to such

    }

    User (String email, String password, String firstName, String lastName, String username, String phone, int balance) {
        // ToDo : Add authentication and setting of profile info to FireBase
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

    public int getBalance() {
        return balance;
    }
}
