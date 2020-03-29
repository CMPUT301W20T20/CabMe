package com.example.cabme.riders;
/**
 * the onClick for email is from https://www.youtube.com/watch?v=nj-STGrL7Zc
 * the onClick for calling is from https://www.youtube.com/watch?v=DiIXhdseGgY
 */

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.Driver;
import com.example.cabme.HomeMapActivity;
import com.example.cabme.Rating;
import com.example.cabme.User;
import com.example.cabme.UserProfileActivity;
import com.example.cabme.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class RideOfferActivity extends AppCompatActivity {

    /* Log Tags */
    private static final String TAG = "Firelog";

    /* Fire Store */
    private FirebaseFirestore mFirestore;
    private RideOfferAdapter adapter;
    private RecyclerView recyclerView;
    private Query query;
    private Bundle bundle;
    private List<String> offers;
    private Button confirmRideButton;


    /* key */
    private com.example.cabme.User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_offerlist_activity);
        user = (com.example.cabme.User) getIntent().getSerializableExtra("user"); // get intent
        mFirestore = FirebaseFirestore.getInstance(); // starting the database references
        confirmRideButton = (Button)findViewById(R.id.confirm_ride);
        setUpRecyclerView();
    }

    /**
     * Sets up the recycler view, its options etc
     */
    private void setUpRecyclerView(){
        /* getting the offers subcollection in testrequests */
        mFirestore.collection("testrequests")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot ddocumentSnapshot) {
                        offers = (List<String>) ddocumentSnapshot.get("offers");
                        query = mFirestore
                                .collection("users")
                                .whereIn(FieldPath.documentId(), offers);

                        Log.wtf("Check", "" + user.getUid());
                        Log.wtf("Check", "" + query.toString());


                        /* set options */
                        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                                .setQuery(query, User.class)
                                .build();

                        /* set the adapter */
                        adapter = new RideOfferAdapter(options);

                        /*recyclerview settings*/
                        recyclerView = findViewById(R.id.recycleView); // setting recycleview
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RideOfferActivity.this));
                        recyclerView.setAdapter(adapter);
                        adapter.startListening();
                        /* on click listen for thr adapter opens profile fragment */

                        adapter.setOnItemClickListener(new RideOfferAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                                String driverID = documentSnapshot.getId();
                                Log.wtf("USERNAMECLICK", position+"");
                                Log.wtf("USENAMECLICK", driverID+"");
                                bundle = new Bundle();
                                bundle.putSerializable("uid", driverID);
                                UserProfileActivity userProfileActivity = new UserProfileActivity();
                                userProfileActivity.setArguments(bundle);
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.r_offerlist_activity, userProfileActivity)
                                        .commit();
                            }
                            @Override
                            public void onDriverSelect(DocumentSnapshot documentSnapshot, int position) {
                                String driverID = documentSnapshot.getId();
                                Log.wtf("DRIVERSELECT", position+"");
                                Log.wtf("DRIVERSELECT", driverID+"");
                                confirmRideButton.setText(""+ driverID);
                                confirmRideButton.setVisibility(View.VISIBLE);
                                confirmRideButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // change fields in requests
                                        /* Removes the fragment and starts the HomeMapActivity recreation here*/
                                        RideRequest rideRequest = new RideRequest(user.getUid());
                                        rideRequest.updateRideStatus("Active");
                                        rideRequest.updateDriver(driverID);

                                        // go back to the previous screeen
                                    }
                                });
                            }
                        });
                    }
                });
    }

    /**
     * Purpose: set listener on start of activity
     */
    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * Purpose: stop listener at end of activity
     */
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
