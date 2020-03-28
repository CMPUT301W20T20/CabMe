package com.example.cabme.riders;

import java.util.Observable;

public class RideOfferModel extends Observable {

    private String UID;
    private String username;

    private String first;
    private String last;

    private String phone;
    private String email;
    private Integer rating;

    private RideOfferModel(){}

    private RideOfferModel(String UID){
        setUID(UID);
    }

    /**
     * Purpose: return UID, of requesting driver
     *
     * @return UID
     */
    public String getUID() {
        return UID;
    }

    /**
     * Purpose: set UID, of requesting driver
     *
     * @param UID
     */
    public void setUID(String UID) {
        this.UID = UID;
    }
}
