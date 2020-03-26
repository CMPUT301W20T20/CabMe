package com.example.cabme.riders;

import java.util.Observable;

public class RideOfferModel extends Observable {

    private String driverID;
    private String username;

    private String first;
    private String last;

    private String phone;
    private String email;
    private Integer rating;

    private RideOfferModel(){}

    private RideOfferModel(
            String driverID,
            String username,
            String email,
            String first,
            String last,
            String phone,
            Integer rating){
        setDriverID(driverID);
        setUsername(username);
        setEmail(email);
        setFirst(first);
        setLast(last);
        setPhone(phone);
        setRating(rating);
    }


    /**
     * Purpose: return driverID, of requesting driver
     *
     * @return driverID
     */
    public String getDriverID() {
        return driverID;
    }

    /**
     * Purpose: set driverID, of requesting driver
     *
     * @param driverID
     */
    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    /**
     * Purpose: return username, of requesting driver
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Purpose: set username, of requesting driver
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Purpose: return email of requesting driver
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Purpose: set email, of requesting driver
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *Purpose: return first name of requesting driver
     * @return
     */
    public String getFirst() {
        return first;
    }

    /**
     * Purpose: set first name of requesting driver
     * @param first
     */
    public void setFirst(String first) {
        this.first = first;
    }

    /**
     * Purpose: return last name of requesting driver
     * @return last
     */
    public String getLast() {
        return last;
    }

    /**
     * Purpose: set last name of requesting driver
     * @param last
     */
    public void setLast(String last) {
        this.last = last;
    }

    /**
     * Purpose: return phone number of requesting driver
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Purpose: set phone number of requesting driver
     *
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Purpose: return rating of requesting driver
     *
     * @return rating
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Purpose: set rating of requesting driver
     *
     * @param rating
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
