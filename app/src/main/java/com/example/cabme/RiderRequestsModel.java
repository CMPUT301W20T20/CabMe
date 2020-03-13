package com.example.cabme;

public class RiderRequestsModel {

    private String fname;
    private String lname;
    private String email;

    private RiderRequestsModel(){}
    private RiderRequestsModel(String fname, String lname, String email){
        setFname(fname);
        setLname(lname);
        setEmail(email);
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
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
