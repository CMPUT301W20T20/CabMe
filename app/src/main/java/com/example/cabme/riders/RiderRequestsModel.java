package com.example.cabme.riders;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.Observable;

public class RiderRequestsModel extends Observable {

    private String rideStatus;
    private Double rideCost;

    private GeoPoint startLocation;
    private GeoPoint endLocation;

    private String startAddress;
    private String endAddress;

    private String UIDdriver;
    private String UIDrider;

    private String distanceText;
    private Integer distanceValue;

    private String durationText;
    private Integer durationValue;

    private RiderRequestsModel(){}

    private RiderRequestsModel(
            String status,
            GeoPoint startLocation, GeoPoint endLocation,
            String UIDdriver, String UIDrider,
            String startAddress, String endAddress,
            String distanceText, Integer distanceValue,
            String durationText, Integer durationValue,
            String rideStatus, Double rideCost){

      setStatus(status);
      setStartLocation(startLocation);
      setEndLocation(endLocation);
      setUIDdriver(UIDdriver);
      setUIDrider(UIDrider);
      setStartAddress(startAddress);
      setEndAddress(endAddress);
      setDistanceText(distanceText);
      setDistanceValue(distanceValue);
      setDurationText(durationText);
      setDurationValue(durationValue);
      setRideCost(rideCost);
      setRideStatus(rideStatus);
    }

    public String getStatus() {
        return rideStatus;
    }
    public GeoPoint getStartLocation() { return startLocation; }
    public GeoPoint getEndLocation() {
        return endLocation;
    }
    public String getUIDdriver() {
        return UIDdriver;
    }
    public String getUIDrider(){
        return UIDrider;
    }
    public String getStartAddress() {
        return startAddress;
    }
    public String getEndAddress() {
        return endAddress;
    }
    public String getDistanceText() {
        return distanceText;
    }
    public String getDurationText() {
        return durationText;
    }
    public Integer getDistanceValue() {
        return distanceValue;
    }
    public Integer getDurationValue() {
        return durationValue;
    }
    public String getRideStatus() {
        return rideStatus;
    }
    public Double getRideCost() {
        return rideCost;
    }

    public void setStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }
    public void setStartLocation(GeoPoint startLocation) { this.startLocation = startLocation;}
    public void setEndLocation(GeoPoint endLocation) {
        this.endLocation = endLocation;
    }
    public void setUIDdriver(String UIDdriver) {
        this.UIDdriver = UIDdriver;
    }
    public void setUIDrider(String UIDrider) {
        this.UIDrider = UIDrider;
    }
    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }
    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }
    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }
    public void setDistanceValue(Integer distanceValue) {
        this.distanceValue = distanceValue;
    }
    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }
    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }
    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }
    public void setRideCost(Double rideCost) {
        this.rideCost = rideCost;
    }
}
