package com.example.cabme;

import java.io.Serializable;

public class LongLat implements Serializable {
    Double Lat;
    Double Lng;

    public LongLat() {

    }

    public LongLat(double Lng, double Lat){
        this.Lat = Lat;
        this.Lng = Lng;
    }

    public Double getLat(){return this.Lat;}
    public Double getLng(){return this.Lng;}

    public void setLat(Double lat) {
        Lat = lat;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }
}
