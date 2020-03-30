package com.example.cabme.riders;
/**
 * the onClick for email is from https://www.youtube.com/watch?v=nj-STGrL7Zc
 * the onClick for calling is from https://www.youtube.com/watch?v=DiIXhdseGgY
 *
 *
 *
 *
 * Note to self: Work on todo's
 * secondly need to be able to click on view and accept ride....means move database or whatever
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

public class RideOffer extends AppCompatActivity{

    // Log Tags
    private static final String TAG = "Firelog";

    // Fire Store
    private FirebaseFirestore mFirestore;
    private FirestoreRecyclerAdapter<RideOfferModel, RideOfferHolder> adapter;
    private RecyclerView recyclerView;

    // Query
    Query query;
    //private CollectionReference docref;
    // Key
    private User user;
    private List<String> offers;
    private ArrayList<String> test = new ArrayList<>();

    private Integer REQUEST_PERMISSION = 1;
    private transient CollectionReference collectionReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_offerlist_activity);


        // get user intent
        user = (User) getIntent().getSerializableExtra("User");

        // starting the database references
        mFirestore = FirebaseFirestore.getInstance();

        // setting recycleview
        recyclerView = findViewById(R.id.recycleView);

        //declare arraylist

        //get the "offers" list
        //"499LGKdwIbWFap3nNpe3Huo0Fkj2"
        // need to get user.getUID() working, currently it is not working for some reason.
        collectionReference = mFirestore.collection("testrequests");

        //TODO: need to change document path to user.getUID()
        collectionReference.document("499LGKdwIbWFap3nNpe3Huo0Fkj2").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot == null) {
                    return;
                }
                offers = (ArrayList<String>) documentSnapshot.get("offers");

                test.addAll(offers);

            }
        });

        // Query
        // getting the ridehistory collection in the user's document
        //oyP44B7VSZerbMYGYNw3eCwbcHW2
        //CdeNP3Lb4UhmWlfaIqXnGI2Cq763
        query = mFirestore.collection("users");

        // Recycler Options
        //TODO: need to change this to a list
        FirestoreRecyclerOptions<RideOfferModel> options = new FirestoreRecyclerOptions.Builder<RideOfferModel>()
                .setQuery(query.whereIn("uid",Arrays.asList("CdeNP3Lb4UhmWlfaIqXnGI2Cq763", "oyP44B7VSZerbMYGYNw3eCwbcHW2")), RideOfferModel.class)
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

                //set all the respective fields with text
                holder.name.setText(model.getFirst() + " " + model.getLast());
                holder.username.setText(model.getUsername());
               // holder.rating.setText(String.valueOf(model.getRating()));
                holder.phone.setText(model.getPhone());
                holder.email.setText(model.getEmail());

                //onClick listener to start email intent
                holder.email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String recipientList = holder.email.getText().toString();
                        emailIntent(recipientList);
                    }
                });

                //onClick listener to start phone intent
                holder.phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = holder.phone.getText().toString();
                        phoneIntent(phone);
                    }
                });

                /** setting the onclick for the recycleview */
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        //get snapshot of data
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());

                        Log.d(TAG, "onClick: clicked on " + snapshot.getString("username"));

                        //setting intent
                        Intent intent = new Intent(RideOffer.this, Pop.class);

                        //passing variables through using putExtra
                        intent.putExtra("username", snapshot.getString("username"));
                        intent.putExtra("uid", snapshot.getString("uid"));
                        startActivity(intent);

                    }
                });

            }
        };

        //recyclerview settings
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    /**
     * Purpose: "container" that holds all the information we need to display to the rider
     */
    private class RideOfferHolder extends RecyclerView.ViewHolder {
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

    /**
     * Purpose: first requests permission, then opens the phone app
     * @param phone
     */
    private void phoneIntent(String phone){
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

    /**
     * Purpose: open the email intent, with the recipients name passed through
     * @param recipientList
     */
    private void emailIntent(String recipientList){

        String [] recipient = recipientList.split(",");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.setType("message/rfc822"); //MIME type rcf822
        startActivity(intent);
    }

}
