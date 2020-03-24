package com.example.cabme.maps;

/**
 * Purpose:
 * - calculate the ride cost based on duration and distance
 * - literally can be anything change if it u wanna
 *
 */
public class CostAlgorithm {
    private Integer distanceValue; // Int metres
    private Integer durationValue; // Int seconds
    private Double rideCost; // Double dollars

    public CostAlgorithm(Integer distanceValue, Integer durationValue) {
        this.distanceValue = distanceValue;
        this.durationValue = durationValue;
    }

    public Double RideCost(){
        double flatFee = 3.50;
        double baseFee = 1.00;
        Double costPerMile = 1.25;
        Double distanceMiles = (double) distanceValue/1000;
        rideCost = (distanceMiles * costPerMile) + flatFee + baseFee;
        return rideCost;
    }
}
