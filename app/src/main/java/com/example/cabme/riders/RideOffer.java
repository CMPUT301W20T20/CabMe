package com.example.cabme.riders;
/**
 * the onClick for email is from https://www.youtube.com/watch?v=nj-STGrL7Zc
 * the onClick for calling is from https://www.youtube.com/watch?v=DiIXhdseGgY
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.R;
import com.example.cabme.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Observable;

public class RideOffer extends AppCompatActivity {

    // Log Tags
    private static final String TAG = "Firelog";

    // Fire Store
    private FirebaseFirestore mFirestore;
    private FirestoreRecyclerAdapter<RideOfferModel, RideOfferHolder> adapter;
    private RecyclerView recyclerView;
    Query query;

    // key
    private User user;
    private Integer REQUEST_PERMISSION = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        FirestoreRecyclerOptions<RideOfferModel> options = new FirestoreRecyclerOptions.Builder<RideOfferModel>()
                .setQuery(query, RideOfferModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<RideOfferModel, RideOfferHolder>(options) {
            @NonNull
            @Override
            public RideOfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_offer_layout, parent, false);
                return new RideOfferHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RideOfferHolder holder, int position, @NonNull RideOfferModel model) {
                holder.name.setText(model.getFirst() + " " + model.getLast());
                holder.username.setText(model.getUsername());
                holder.rating.setText(String.valueOf(model.getRating()));
                holder.phone.setText(model.getPhone());
                holder.email.setText(model.getEmail());

                holder.email.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Purpose: this is the onClick to open the email intent, with the recipients name passed through
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        String recipientList = holder.email.getText().toString();
                        String [] recipient = recipientList.split(",");
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
                        intent.setType("message/rfc822"); //MIME type rcf822
                        startActivity(intent);
                    }
                });


                holder.phone.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Purpose: onClick that first requests permission for the phone app, then calls the phone number
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        String phone = holder.phone.getText().toString();

                        if (ActivityCompat.shouldShowRequestPermissionRationale(RideOffer.this, Manifest.permission.CALL_PHONE)) {
                            ActivityCompat.requestPermissions(RideOffer.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION);
                        } else {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            if (ActivityCompat.checkSelfPermission(RideOffer.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                startActivity(callIntent);
                            }
                        }
                    }
                });
            }
        };

        /*recycleview settings*/
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    /**
     * Purpose: "container" that holds all the information we need to display to the rider
     */
    private class RideOfferHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView username;
        private TextView rating;
        private Button phone;
        private Button email;

        /**
         * Purpose: contains all the respective button and text views
         */
        public RideOfferHolder (@NonNull View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.fullname);
            username = itemView.findViewById(R.id.username);
            rating = itemView.findViewById(R.id.rating);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);

        }
    }
    /**
     * Purpose: stop listener at end of activity
     */
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /**
     * purpose: set listener on start of activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

}
