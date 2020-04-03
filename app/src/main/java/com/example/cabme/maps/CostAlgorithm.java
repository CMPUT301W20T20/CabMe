package com.example.cabme.maps;

/**
 * Calculates the cost of the ride based on distance.
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
        /* floor only truncates positive values so i guess its okay here */
        rideCost = Math.floor(rideCost * 100) / 100;
        return rideCost;
    }
}
