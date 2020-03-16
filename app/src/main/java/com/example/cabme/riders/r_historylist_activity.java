package com.example.cabme.riders;

import android.content.Intent;
import android.os.Bundle;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;

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



        //starting the database references
        mFirestore = FirebaseFirestore.getInstance();

        //setting recycleview
        recyclerView = findViewById(R.id.recycleView);

        //Query
        Query query = mFirestore.collection("requests");
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
                holder.status.setText(model.getStatus());

                /**
                 * implement later
                 if(holder.status.getText() == "Cancelled" ){
                 holder.status.setText(model.getStatus());
                 holder.status.setTextColor(getResources().getColor(R.color.red));
                 }
                 **/

                holder.toFrom.setText("FROM: " + String.valueOf(model.getStartLocation()) + "| TO:" + String.valueOf(model.getEndLocation()));

                //holder.driverName.setText("Driver ID: " + model.getDriverID());
            }

        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //View Holder



        //onclick listener for newRideButton to start r_newrideinfo_activity
        newRideButton = findViewById(R.id.newRideButton);

        //Button click will start new activity
        newRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(r_historylist_activity.this, NewRideInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    private class RiderRequestsViewHolder extends RecyclerView.ViewHolder{
        private TextView status;
        private TextView toFrom;
        private TextView driverName;

        public RiderRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.status);
            toFrom= itemView.findViewById(R.id.toFrom);
            driverName = itemView.findViewById(R.id.driverName);

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
