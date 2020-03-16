package com.example.cabme.maps;

import java.io.Serializable;

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
