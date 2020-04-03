package com.example.cabme.riders;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.UserProfileActivity;
import com.example.cabme.R;
import com.example.cabme.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * TODO:
 *  [X] Geocode long & Lat in to from -> TAKE FROM JsonParser
 *  [ ] Status color changes
 */
public class RiderHistoryListActivity extends AppCompatActivity{

    /* Log Tags */
    private static final String TAG = "Firelog";

    /* Fire Store */
    private FirebaseFirestore mFirestore;
    private RiderHistoryListAdapter adapter;
    private RecyclerView recyclerView;
    private Query query;
    private Bundle bundle;

    /* key */
    private User user;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_rider_historylist_activity);
        user = (User)getIntent().getSerializableExtra("user"); // get intent
        mFirestore = FirebaseFirestore.getInstance(); // starting the database references
        setUpRecyclerView();
    }

    /**
     * Sets up the recycler view, its options etc
     */
    private void setUpRecyclerView(){
        query = mFirestore //getting the ridehistory collection in the user's document
                .collection("users")
                .document(user.getUid())
                .collection("ridehistory");

        /* set options */
        FirestoreRecyclerOptions<RiderHistoryListModel> options = new FirestoreRecyclerOptions.Builder<RiderHistoryListModel>()
                .setQuery(query, RiderHistoryListModel.class)
                .build();

        /* set the adapter */
        adapter = new RiderHistoryListAdapter(options);

        /* set up recycler view */
        recyclerView = findViewById(R.id.recycleView); // setting recycleview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        /* on click listen for thr adapter opens profile fragment */
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            String driverID = documentSnapshot.getString("UIDdriver");
            bundle = new Bundle();
            bundle.putSerializable("uid", driverID);
            Log.wtf("UID", driverID+"");
            UserProfileActivity userProfileActivity = new UserProfileActivity();
            userProfileActivity.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.r_historylist_activity, userProfileActivity)
                    .commit();
        });
    }

    /**
     * Stop listener at end of activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    /**
     * Set listener on start of activity
     */
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
