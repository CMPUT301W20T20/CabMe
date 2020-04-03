package com.example.cabme;

import java.io.Serializable;

public class Rating implements Serializable {
    private int posRev;
    private int negRev;

    public Rating() {
        posRev = 0;
        negRev = 0;
    }

    /**
     * This makes a boolean true if a user has been reviewed such that the number
     * of positive and negative reviews are not the same.
     * @return
     */
    public boolean isReviewed() {
        return (posRev + negRev != 0);
    }

    /**
     * This gets the relative percentage rating of the user based on the number of 
     * positive and negative reviews received.
     * @return
     */
    public double percentRating() {
        return (double) posRev / (double) (posRev + negRev);
    }

    public void posRev() {
        posRev = posRev + 1;
    }

    public void negRev() {
        negRev = negRev + 1;

    }

    /**
     * This returns a negative review when a user gets negative review
     * @return
     */
    public int getNeg_rev() {
        return neg_rev;
    }

    /**
     * This returns a positive review when a user gets positive review
     * @return
     */
    public int getPos_rev() {
        return pos_rev;
    }
}
