package com.example.cabme;

import java.io.Serializable;

public class Rating implements Serializable {
    private int pos_rev;
    private int neg_rev;

    public Rating() {
        pos_rev = 0;
        neg_rev = 0;
    }

    /**
     * This makes a boolean true if a user has been reviewed such that the number
     * of positive and negative reviews are not the same.
     * @return
     */
    public boolean isReviewed() {
        return (pos_rev + neg_rev != 0);
    }

    /**
     * This gets the relative percentage rating of the user based on the number of 
     * positive and negative reviews received.
     * @return
     */
    public double percentRating() {
        return (double) pos_rev/ (double) (pos_rev + neg_rev);
    }

    public void pos_rev() {

        pos_rev = pos_rev + 1;
    }

    public void neg_rev() {
        neg_rev = neg_rev + 1;

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
