package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertController;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EventListener;

public class r_historylist_activity extends AppCompatActivity {

    private static final String TAG = "Firelog";

    FirebaseFirestore mFirestore;

    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Button newRideButton;


    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_historylist_activity);

        //this is where the youtube tutorial starts----------------------------------------

        //starting the database references
        mFirestore = FirebaseFirestore.getInstance();

        //setting recycleview
        recyclerView = findViewById(R.id.recycleView);

        //Query
        Query query = mFirestore.collection("users");
        //Recycler Options
        FirestoreRecyclerOptions<RiderRequestsModel> options = new FirestoreRecyclerOptions.Builder<RiderRequestsModel>().setQuery(query, RiderRequestsModel.class).build();

        adapter = new FirestoreRecyclerAdapter<RiderRequestsModel, RiderRequestsViewHolder>(options) {
            @NonNull
            @Override
            public RiderRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_layout, parent, false);
                return new RiderRequestsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RiderRequestsViewHolder holder, int position, @NonNull RiderRequestsModel model) {
                holder.fname.setText(model.getFname());
                holder.lname.setText(model.getLname());
                holder.email.setText(model.getEmail());

            }


        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //View Holder
        //this is where the youtube tutorial ends----------------------------------------


        //onclick listener for newRideButton to start r_newrideinfo_activity
        newRideButton = findViewById(R.id.newRideButton);

        //Button click will start new activity
        newRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(r_historylist_activity.this, r_newrideinfo_activity.class);
                startActivity(intent);
            }
        });

    }

    private class RiderRequestsViewHolder extends RecyclerView.ViewHolder{
        private TextView fname;
        private TextView lname;
        private TextView email;

        public RiderRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            fname = itemView.findViewById(R.id.fname);
            lname = itemView.findViewById(R.id.lname);
            email = itemView.findViewById(R.id.email);

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
