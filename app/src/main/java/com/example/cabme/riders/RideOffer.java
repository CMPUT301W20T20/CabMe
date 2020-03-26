package com.example.cabme.riders;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.R;
import com.example.cabme.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Observable;

public class RideOffer extends AppCompatActivity {

    // Log Tags
    private static final String TAG = "Firelog";

    // Fire Store
    private FirebaseFirestore mFirestore;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    Query query;

    // key
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState); /*if there are errors, use the simple onCreate method that takes only 1 parameter*/
        setContentView(R.layout.r_offerlist_activity);


        // get user intent
        user = (User) getIntent().getSerializableExtra("user");

        // starting the database references
        mFirestore = FirebaseFirestore.getInstance();

        // setting recycleview
        recyclerView = findViewById(R.id.recycleView);

        // Query
        // getting the ridehistory collection in the user's document
        query = mFirestore
                .collection("requests");


        // Recycler Options
        FirestoreRecyclerOptions<RiderHistoryListModel> options = new FirestoreRecyclerOptions.Builder<RiderHistoryListModel>()
                .setQuery(query, RiderHistoryListModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<RiderHistoryListModel, RideOfferHolder>(options) {
            @NonNull
            @Override
            public RideOfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_offer_layout, parent, false);
                return new RideOfferHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RideOfferHolder holder, int position, @NonNull RiderHistoryListModel model) {
                holder.status.setText(model.getStatus());

            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }
    @Override
    public void update(Observable o, Object arg) {
    }
    /**
     * Purpose: is a "container" that holds all the information we need to display to the rider
     */
    private class RideOfferHolder extends RecyclerView.ViewHolder{
        private TextView first;
        private TextView last;
        private TextView phone;
        private TextView email;
        private TextView rating;

        /**
         * Purpose
         */
        public RideOfferHolder (@NonNull View itemView){
            super(itemView);


        }
    }


}
