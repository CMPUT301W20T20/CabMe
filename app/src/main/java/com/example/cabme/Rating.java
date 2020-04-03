package com.example.cabme;

import java.io.Serializable;

public class Rating implements Serializable {
    private int posRev;
    private int negRev;

    public Rating() {
        posRev = 0;
        negRev = 0;
    }

    public boolean isReviewed() {
        return (posRev + negRev != 0);
    }

    public double percentRating() {
        return (double) posRev / (double) (posRev + negRev);
    }

    public void posRev() {
        posRev = posRev + 1;
    }

    public void negRev() {
        negRev = negRev + 1;

    }

    public int getNegRev() {
        return negRev;
    }

    public int getPosRev() {
        return posRev;
    }
}
