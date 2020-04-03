package com.example.cabme.riders;
/**
 * the onClick for email is from https://www.youtube.com/watch?v=nj-STGrL7Zc
 * the onClick for calling is from https://www.youtube.com/watch?v=DiIXhdseGgY
 */

import android.content.Intent;
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
import com.google.type.LatLng;

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
    /**
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_offerlist_activity);
        user = (com.example.cabme.User) getIntent().getSerializableExtra("user"); // get intent
        mFirestore = FirebaseFirestore.getInstance(); // starting the database references
        confirmRideButton = findViewById(R.id.confirm_ride);
        setUpRecyclerView();
    }

    /**
     * This sets up the recycler view, its options and so on
     */
    private void setUpRecyclerView(){
        /* getting the offers subcollection in testrequests */
        mFirestore.collection("testrequests")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    /**
                     * This gets the documnet snapshot from the recycler view for offers
                     * @param ddocumentSnapshot
                     */
                    public void onSuccess(DocumentSnapshot ddocumentSnapshot) {
                        offers = (List<String>) ddocumentSnapshot.get("offers");

                        if(offers.size() == 0) {
                            Log.wtf("QUERYCHECK", "check status" + query);
                            return;
                        } else{
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
                                /**
                                 * @param documentSnapshot
                                 * @param position
                                 */
                                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                                    String driverID = documentSnapshot.getId();
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
                                /**
                                 * @param documentSnapshot
                                 * @param position
                                 */
                                public void onDriverSelect(DocumentSnapshot documentSnapshot, int position) {
                                    String driverID = documentSnapshot.getId();
                                    confirmRideButton.setText(String.format("Ride with %s", documentSnapshot.getString("first")));
                                    confirmRideButton.setVisibility(View.VISIBLE);
                                    confirmRideButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        /**
                                         * This changes the fields in requests and also removes the fragment.
                                         * It also starts the HomeMapActivity recreation here
                                         * @param v
                                         */
                                        public void onClick(View v) {  
                                            RideRequest rideRequest = new RideRequest(user.getUid());
                                            rideRequest.updateRideStatus("Rider Ready");
                                            rideRequest.updateDriver(driverID);
//                                            Intent intent = new Intent();
////                                            Log.wtf("WTFWTF", driverID);
////                                            setResult(1, intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
    }

    @Override
    /**
     * This sets the listener on start of activity
     */
    protected void onStart() {
        super.onStart();

    }

    @Override
    /**
     * This stops the listener at end of activity
     */
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }
}
