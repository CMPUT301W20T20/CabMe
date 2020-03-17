package com.example.cabme.maps;

import java.io.Serializable;

/**
 *
 * Purposes:
 * Stores Lat and Lng as doubles
 *
 * Params:
 * - double:: latitude
 * - double:: longitude
 *
 * QUESTIONS:
 * This isn't needed i just found out about GeoPoints and LatLng objects that basically do the same lol
 *
 * TODO:
 *  [ ] Check if alternatives serializable
 *  [ ] Delete and switch to using the alternatives instead of this class
 *
 */
public class LongLat implements Serializable {
    Double Lat;
    Double Lng;


    public LongLat(double Lng, double Lat){
        this.Lat = Lat;
        this.Lng = Lng;
    }

    public Double getLat(){return this.Lat;}
    public Double getLng(){return this.Lng;}

}
