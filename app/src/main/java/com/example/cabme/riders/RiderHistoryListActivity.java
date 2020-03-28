package com.example.cabme.riders;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.ProfileViewActivity;
import com.example.cabme.R;
import com.example.cabme.User;
import com.example.cabme.drivers.DriveInactiveFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * TODO:
 *  [X] Geocode long & Lat in to from -> TAKE FROM JsonParser
 *  [ ] Status color changes
 */
public class RiderHistoryListActivity extends AppCompatActivity{

    // Log Tags
    private static final String TAG = "Firelog";

    // Fire Store
    private FirebaseFirestore mFirestore;
    private RiderRequestsListAdapter adapter;
    private RecyclerView recyclerView;
    private Query query;
    Bundle bundle;

    // User info
    private User user;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_rider_historylist_activity);
        user = (User)getIntent().getSerializableExtra("user"); // get intent
        mFirestore = FirebaseFirestore.getInstance(); // starting the database references
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        query = mFirestore //getting the ridehistory collection in the user's document
                .collection("users")
                .document(user.getUid())
                .collection("ridehistory");
        FirestoreRecyclerOptions<RiderHistoryListModel> options = new FirestoreRecyclerOptions.Builder<RiderHistoryListModel>()
                .setQuery(query, RiderHistoryListModel.class)
                .build();
        adapter = new RiderRequestsListAdapter(options);

        recyclerView = findViewById(R.id.recycleView); // setting recycleview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            String driverID = documentSnapshot.getString("UIDdriver");
            bundle = new Bundle();
            bundle.putSerializable("uid", driverID);
            Log.wtf("UID", driverID+"");
            ProfileViewActivity profileViewActivity = new ProfileViewActivity();
            profileViewActivity.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.r_historylist_activity, profileViewActivity)
                    .commit();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

