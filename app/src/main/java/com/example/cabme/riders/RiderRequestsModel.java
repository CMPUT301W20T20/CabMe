package com.example.cabme.riders;

public class RiderRequestsModel {

    private String status;
    private com.google.firebase.firestore.GeoPoint startLocation;
    //private String startLocation;
    private com.google.firebase.firestore.GeoPoint endLocation;
    private String driverID;

    private RiderRequestsModel(){}
    private RiderRequestsModel(String status, com.google.firebase.firestore.GeoPoint startLocation, com.google.firebase.firestore.GeoPoint endLocation, String driverID){
      setStatus(status);
      setStartLocation(startLocation);
      setEndLocation(endLocation);
      setDriverID(driverID);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public com.google.firebase.firestore.GeoPoint getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(com.google.firebase.firestore.GeoPoint startLocation) {
        this.startLocation = startLocation;
    }

    public com.google.firebase.firestore.GeoPoint getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(com.google.firebase.firestore.GeoPoint endLocation) {
        this.endLocation = endLocation;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }
}
