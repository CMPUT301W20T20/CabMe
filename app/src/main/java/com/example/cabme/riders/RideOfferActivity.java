package com.example.cabme.riders;
/**
 * the onClick for email is from https://www.youtube.com/watch?v=nj-STGrL7Zc
 * the onClick for calling is from https://www.youtube.com/watch?v=DiIXhdseGgY
 */

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.UserProfileActivity;
import com.example.cabme.R;
import com.example.cabme.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RideOfferActivity extends AppCompatActivity {

    /* Log Tags */
    private static final String TAG = "Firelog";

    /* Fire Store */
    private FirebaseFirestore mFirestore;
    private RideOfferAdapter adapter;
    private RecyclerView recyclerView;
    private Query query;
    private Bundle bundle;

    /* key */
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_offerlist_activity);
        user = (User) getIntent().getSerializableExtra("user"); // get intent
        mFirestore = FirebaseFirestore.getInstance(); // starting the database references
        setUpRecyclerView();
    }

    /**
     * Sets up the recycler view, its options etc
     */
    private void setUpRecyclerView(){
        /* getting the offers subcollection in testrequests */
        query = mFirestore
                .collection("testrequests")
                .document(user.getUid())
                .collection("offers");

        Log.wtf("Check", "" + user.getUid());

        /* set options */
        FirestoreRecyclerOptions<RideOfferModel> options = new FirestoreRecyclerOptions.Builder<RideOfferModel>()
                .setQuery(query, RideOfferModel.class)
                .build();

        /* set the adapter */
        adapter = new RideOfferAdapter(options);

        /*recyclerview settings*/
        recyclerView = findViewById(R.id.recycleView); // setting recycleview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        /* on click listen for thr adapter opens profile fragment */
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            String driverID = documentSnapshot.getString("UID");
            bundle = new Bundle();
            bundle.putSerializable("uid", driverID);
            Log.wtf("UID", driverID+"");
            UserProfileActivity userProfileActivity = new UserProfileActivity();
            userProfileActivity.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.r_offerlist_activity, userProfileActivity)
                    .commit();
        });
    }

    /**
     * Purpose: set listener on start of activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
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
