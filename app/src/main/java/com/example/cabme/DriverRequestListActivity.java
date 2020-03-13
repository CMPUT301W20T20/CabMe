package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DriverRequestListActivity extends AppCompatActivity{
    private OnItemClickListener listener;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;
    private CollectionReference collectionReference;
    Query query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_reqlist_activity);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.request_list);

        //Query
        query = firebaseFirestore.collection("requests");

        //recycler options
        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Request, RequestsViewHolder>(options) {
            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_reqlist_content, parent, false);
                return new RequestsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull Request model) {
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
                holder.riderName.setText(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId());
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