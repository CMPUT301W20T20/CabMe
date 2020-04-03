package com.example.cabme.riders;

import com.google.firebase.firestore.GeoPoint;

import java.util.Observable;

public class RiderHistoryListModel extends Observable {

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

    private RiderHistoryListModel(){}

    private RiderHistoryListModel(
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

     /**
      * This returns the rideStatus (confirmed, cancelled, requested) as a string from the database 
      * @return
      */
    public String getStatus() {
        return rideStatus;
    }

    /**
     * This returns the startLocation of a geopoint from the database
     * @return
     */
    public GeoPoint getStartLocation() { 
        return startLocation; }

    /**
     * This returns the endLocation of a geopoint from the database
     * @return
     */
    public GeoPoint getEndLocation() {
        return endLocation;
    }
    
    /**
     * This returns UIDdriver, which is the UID of the driver
     * @return
     */
    public String getUIDdriver() {
        return UIDdriver;
    }

    /**
     * This returns UIDrider, which is the UID of the rider
     * @return
     */
    public String getUIDrider(){
        return UIDrider;
    }

    /**
     * This returns startAddress, which is the pickup location specified by the rider
     * @return
     */
    public String getStartAddress() {
        return startAddress;
    }

    /**
     * This returns endAddress, which is the drop off location specified by the rider
     * @return
     */
    public String getEndAddress() {
        return endAddress;
    }

    /**
     * @return distanceText
     */
    public String getDistanceText() {
        return distanceText;
    }

    /**
     * @return durationText,
     */
    public String getDurationText() {
        return durationText;
    }

    /**
     * @return distanceValue
     */
    public Integer getDistanceValue() {
        return distanceValue;
    }

    /**
     * @return durationValue
     */
    public Integer getDurationValue() {
        return durationValue;
    }

    /**
     * @return rideStatus
     */
    public String getRideStatus() {
        return rideStatus;
    }

    /**
     * This generates ride cost for the ride based on the cost algorithm
     * @return rideCost
     */
    public Double getRideCost() {
        return rideCost;
    }

    /**
     * This sets the ride status of the ride
     * @param rideStatus
     */
    public void setStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    /**
     * This sets the start location specified by the rider
     * @param startLocation 
     */
    public void setStartLocation(GeoPoint startLocation) { 
        this.startLocation = startLocation;
    }

    /**
     * This sets the end location specified by the rider
     * @param endLocation
     */
    public void setEndLocation(GeoPoint endLocation) {
        this.endLocation = endLocation;
    }

    /**
     * This sets the UID of the driver
     * @param UIDdriver
     */
    public void setUIDdriver(String UIDdriver) {
        this.UIDdriver = UIDdriver;
    }

    /**
     * This sets the UID of the rider
     * @param UIDrider 
     */
    public void setUIDrider(String UIDrider) {
        this.UIDrider = UIDrider;
    }

    /**
     * This sets the startAdress for the ride
     * @param startAddress 
     */
    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    /**
     * This sets the rider specified end location
     * @param endAddress 
     */
    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    /**
     * @param distanceText
     */
    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    /**
     * @param distanceValue
     */
    public void setDistanceValue(Integer distanceValue) {
        this.distanceValue = distanceValue;
    }

    /**
     * @param durationText
     */
    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    /**
     * @param durationValue
     */
    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    /**
     * @param rideStatus
     */
    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    /**
     * @param rideCost
     */
    public void setRideCost(Double rideCost) {
        this.rideCost = rideCost;
    }
}
