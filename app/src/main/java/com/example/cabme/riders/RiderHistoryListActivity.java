package com.example.cabme.riders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.R;
import com.example.cabme.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * TODO:
 *  [X] Geocode long & Lat in to from -> TAKE FROM JsonParser
 *
 * NOTES: renamed file from r_riderhistory_activity (sorry it was making my eye twitch)
 *
 */
public class RiderHistoryListActivity extends AppCompatActivity implements Observer {
    // Log Tags
    private static final String TAG = "Firelog";

    // Fire Store
    private FirebaseFirestore mFirestore;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    Query query;

    // UI Stuff
    private Button newRideButton;

    // key
    private User user;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_rider_historylist_activity);

        // get user intent
        user = (User)getIntent().getSerializableExtra("user");

        // starting the database references
        mFirestore = FirebaseFirestore.getInstance();

        // setting recycleview
        recyclerView = findViewById(R.id.recycleView);

        // Query
        // getting the ridehistory collection in the user's document
        query = mFirestore
                .collection("users")
                .document(user.getUid())
                .collection("ridehistory");

        // Recycler Options
        FirestoreRecyclerOptions<RiderHistoryListModel> options = new FirestoreRecyclerOptions.Builder<RiderHistoryListModel>()
                .setQuery(query, RiderHistoryListModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<RiderHistoryListModel, RiderRequestsViewHolder>(options) {
            @NonNull
            @Override
            public RiderRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_rider_historylist_content, parent, false);
                return new RiderRequestsViewHolder(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RiderRequestsViewHolder holder, int position, @NonNull RiderHistoryListModel model) {
                holder.status.setText(model.getStatus());

                /**
                 * implement later
                 if(holder.status.getText() == "Cancelled" ){
                 holder.status.setText(model.getStatus());
                 holder.status.setTextColor(getResources().getColor(R.color.red));
                 }
                 **/

                holder.status.setText(String.valueOf(model.getStatus()));
                holder.from.setText(String.valueOf(model.getStartAddress()));
                holder.to.setText(String.valueOf(model.getEndAddress()));
                holder.cost.setText("$" + model.getRideCost());

                Log.wtf("DRIVERNM", ""+model.getUIDdriver());

                String driveFullName = "driver name";
                holder.driverName.setText(driveFullName);
                holder.driverUsername.setText("@drivername");
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //View Holder

    }

    @Override
    public void update(Observable o, Object arg) {

    }

    private class RiderRequestsViewHolder extends RecyclerView.ViewHolder{
        private TextView status;
        private TextView to;
        private TextView from;
        private TextView driverName;
        private TextView cost;
        private Button driverUsername;

        public RiderRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.status);
            cost = itemView.findViewById(R.id.cost);
            to = itemView.findViewById(R.id.to);
            from = itemView.findViewById(R.id.from);
            driverName = itemView.findViewById(R.id.driver_name);
            driverUsername = itemView.findViewById(R.id.driver_username);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}