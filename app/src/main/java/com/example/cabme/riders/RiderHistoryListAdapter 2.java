package com.example.cabme.riders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.Driver;
import com.example.cabme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RiderHistoryListAdapter extends FirestoreRecyclerAdapter<RiderHistoryListModel, RiderHistoryListAdapter.RiderRequestsViewHolder> {
    private String driverUID;
    private OnItemClickListener listener;

    public RiderHistoryListAdapter(@NonNull FirestoreRecyclerOptions<RiderHistoryListModel> options) {
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
            /* set all the data in the holders from driver */
            Driver driver = new Driver(driverUID);
            driver.readData((email, firstname, lastname, username, phone, rating) -> {
//                String driverFullName = "Your ride with " + firstname + " " + lastname"
                String driverFullName = "Your ride with";
                holder.driverName.setText(driverFullName);
                holder.driverUsername.setText(String.format("@%s", username));
            });
        } else {
            /* if there is no driver just set the views to gone */
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

    /**
     * Holds all the textviews
     */
    class RiderRequestsViewHolder extends RecyclerView.ViewHolder{
        private TextView status;
        private TextView to;
        private TextView from;
        private TextView driverName;
        private TextView cost;
        private TextView driverUsername;

        /* find the right view and assign */
        public RiderRequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.status);
            cost = itemView.findViewById(R.id.cost);
            to = itemView.findViewById(R.id.to);
            from = itemView.findViewById(R.id.from);
            driverName = itemView.findViewById(R.id.driver_name);
            driverUsername = itemView.findViewById(R.id.driver_username);
            /* on click of the drivers user name, pass snapshot of it */
            driverUsername.setOnClickListener(v -> {
                if(listener != null){ /* only if the listener is not null */
                    int position = getAdapterPosition();
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
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