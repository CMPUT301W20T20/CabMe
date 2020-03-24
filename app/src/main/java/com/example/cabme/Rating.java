package com.example.cabme;

public class Rating {
    private int pos_rev;
    private int neg_rev;

    public Rating() {
        pos_rev = 0;
        neg_rev = 0;
    }

    public boolean isReviewed() {
        return (pos_rev + neg_rev != 0);
    }

    public double percentRating() {
        return (double) pos_rev/ (double) (pos_rev + neg_rev);
    }

    public void pos_rev() {
        pos_rev += pos_rev;
    }

    public void neg_rev() {
        neg_rev += neg_rev;
    }

}
