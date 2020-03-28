package com.example.cabme.riders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.Driver;
import com.example.cabme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RiderRequestsListAdapter extends FirestoreRecyclerAdapter<RiderHistoryListModel, RiderRequestsListAdapter.RiderRequestsViewHolder> {
    private String driverUID;
    private OnItemClickListener listener;

    public RiderRequestsListAdapter(@NonNull FirestoreRecyclerOptions<RiderHistoryListModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RiderRequestsViewHolder holder, int position, @NonNull RiderHistoryListModel model) {
        driverUID = model.getUIDdriver();
        String dollarSign = "$" + model.getRideCost();
        holder.status.setText(model.getStatus());
        holder.status.setText(String.valueOf(model.getStatus()));
        holder.from.setText(String.valueOf(model.getStartAddress()));
        holder.to.setText(String.valueOf(model.getEndAddress()));
        holder.cost.setText(dollarSign);
        if(!driverUID.equals(""))
        {
            Driver driver = new Driver(driverUID);
            driver.readData((email, firstname, lastname, username, phone, rating) -> {
                String driverFullName = firstname + " " + lastname;
                String ATusername = "@" + username;
                holder.driverName.setText(driverFullName);
                holder.driverUsername.setText(ATusername);
            });
        } else {
            holder.driverName.setVisibility(View.GONE);
            holder.driverUsername.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public RiderRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_rider_historylist_content, parent, false);
        return new RiderRequestsViewHolder(view);
    }

    class RiderRequestsViewHolder extends RecyclerView.ViewHolder{
        private TextView status;
        private TextView to;
        private TextView from;
        private TextView driverName;
        private TextView cost;
        private TextView driverUsername;

        public RiderRequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.status);
            cost = itemView.findViewById(R.id.cost);
            to = itemView.findViewById(R.id.to);
            from = itemView.findViewById(R.id.from);
            driverName = itemView.findViewById(R.id.driver_name);
            driverUsername = itemView.findViewById(R.id.driver_username);
            driverUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}