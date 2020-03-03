package com.example.cabme;

import java.util.ArrayList;

public abstract class CModel<V extends CView> {
    private ArrayList<V> views;
    public CModel() {
        views = new ArrayList<V>();
    }
    public void addView(V view) {
        if (! views.contains(view)) {
            views.add(view);
        }
    }
    public void deleteView(V view) {
        views.remove( view );
    }
    public void notifyViews() {
        for (V view : views) {
            view.update( this );
        }
    }
}
