package com.example.cabme.drivers;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.R;
import com.example.cabme.riders.RiderHistoryListAdapter;
import com.example.cabme.riders.RiderHistoryListModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DriverRequestListAdapter extends FirestoreRecyclerAdapter<RiderHistoryListModel, DriverRequestListAdapter.RequestsViewHolder> {
    private DriverRequestListAdapter.OnItemClickListener listener;

    public DriverRequestListAdapter(@NonNull FirestoreRecyclerOptions<RiderHistoryListModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull RiderHistoryListModel model) {
        holder.itemView.setBackgroundColor(position == position ? Color.WHITE : Color.TRANSPARENT);
        String UID = getSnapshots().getSnapshot(holder.getAdapterPosition()).getString("UIDrider");
        String address = getSnapshots().getSnapshot(holder.getAdapterPosition()).getString("startAddress");
        Double cost = (Double) getSnapshots().getSnapshot(holder.getAdapterPosition()).get("rideCost");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
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
                            holder.name.setText(fullName);
                        } else {
                            holder.name.setText("* This user no longer exists");
                        }
                    }
                });

        // -> change this to distance from the user in the future!
        holder.fare.setText(cost.toString());
        holder.sLocation.setText(address);
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_reqlist_content, parent, false);
        return new RequestsViewHolder(view);
    }

    class RequestsViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView fare;
        private TextView sLocation;

        public RequestsViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.name);
            fare = itemView.findViewById(R.id.fare);
            sLocation = itemView.findViewById(R.id.slocation);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    notifyItemChanged(position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(DriverRequestListAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
