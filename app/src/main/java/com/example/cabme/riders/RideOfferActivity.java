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

    /* key */
    private com.example.cabme.User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_offerlist_activity);
        user = (com.example.cabme.User) getIntent().getSerializableExtra("user"); // get intent
        mFirestore = FirebaseFirestore.getInstance(); // starting the database references
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
