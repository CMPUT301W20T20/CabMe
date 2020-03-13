package com.example.cabme;

public class RiderRequestsModel {

    private String first;
    private String lname;
    private String email;

    private RiderRequestsModel(){}
    private RiderRequestsModel(String first, String lname, String email){
        setFname(first);
        setLname(lname);
        setEmail(email);
    }

    public String getFirst() {
        return first;
    }

    public void setFname(String fname) {
        this.first = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
