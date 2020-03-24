package com.example.cabme.drivers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

// In other folders
import com.example.cabme.maps.LongLat;
import com.example.cabme.maps.MapViewActivity;

public class DriverRequestListActivity extends AppCompatActivity{
    private OnItemClickListener listener;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    Query query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_reqlist_activity);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.request_list);

        // Query
        query = firebaseFirestore.collection("requests");

        // Recycler options
        FirestoreRecyclerOptions<DriverRequestListModel> options = new FirestoreRecyclerOptions.Builder<DriverRequestListModel>()
                .setQuery(query, DriverRequestListModel.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<DriverRequestListModel, RequestsViewHolder>(options) {
            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_reqlist_content, parent, false);
                return new RequestsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull DriverRequestListModel model) {
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        GeoPoint startLoc = snapshot.getGeoPoint("startLocation");
                        GeoPoint destLoc = snapshot.getGeoPoint("endLocation");
                        if(destLoc != null && startLoc != null){
                            Intent intent = new Intent(DriverRequestListActivity.this, MapViewActivity.class);

                            LongLat startLongLat = new LongLat(startLoc.getLongitude(), startLoc.getLatitude());
                            LongLat destLongLat = new LongLat(destLoc.getLongitude(), destLoc.getLatitude());

                            intent.putExtra("startLongLat", startLongLat);
                            intent.putExtra("destLongLat", destLongLat);
                            intent.putExtra("isRider", false);

                            startActivity(intent);

                            Log.wtf("LOG-LATLNG",  startLoc.toString()+" "+destLoc.toString());
                        }
                    }
                });
                String UID = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                firebaseFirestore.collection("users")
                        .document(UID)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Log.d("LOG", "Data Retrieved");
                                String fName = documentSnapshot.getString("first");
                                String lName = documentSnapshot.getString("last");

                                // Case for if the user deleted their profile.
                                // - If the user deleted their profile UID no longer attached to a name.
                                // - Instead show that they are deleted insead of null.
                                if(fName != null || lName != null){
                                    String fullName = fName + " " + lName;
                                    holder.riderName.setText(fullName);
                                } else {
                                    holder.riderName.setText("* This user no longer exists");
                                }
                            }
                        });
                // -> change this to distance from the user in the future!
                holder.distanceAway.setText("Distance Away");
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firestoreRecyclerAdapter);
    }

    private class RequestsViewHolder extends RecyclerView.ViewHolder{
        private TextView riderName;
        private TextView distanceAway;

        public RequestsViewHolder(@NonNull View itemView){
            super(itemView);
            riderName = itemView.findViewById(R.id.rider_name);
            distanceAway = itemView.findViewById(R.id.rider_distance_away);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot ds, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    protected void onStop(){

        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

}