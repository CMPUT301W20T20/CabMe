package com.example.cabme.riders;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabme.R;
import com.example.cabme.Rating;
import com.example.cabme.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * This adap
 */
public class RideOfferAdapter extends FirestoreRecyclerAdapter<User, RideOfferAdapter.RideOfferHolder>{
    private String driverUID;
    public OnItemClickListener listener;

    public RideOfferAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RideOfferHolder holder, int position, @NonNull User model) {
        holder.itemView.setBackgroundColor(position == position ? Color.WHITE : Color.TRANSPARENT);
        String firstname = getSnapshots().getSnapshot(holder.getAdapterPosition()).getString("first");
        String lastname = getSnapshots().getSnapshot(holder.getAdapterPosition()).getString("last");
        String username = getSnapshots().getSnapshot(holder.getAdapterPosition()).getString("username");
        Rating rating = getSnapshots().getSnapshot(holder.getAdapterPosition()).get("rating", Rating.class);
        holder.name.setText(String.format("%s %s", firstname, lastname));
        holder.username.setText(String.format("@%s", username));
        if (rating.isReviewed()) {
            holder.rating.setText(String.format("%3.0f%% ★  %d+ / %d-", rating.percentRating()*100, rating.getPosRev(), rating.getNegRev()));
        }
        else {
            holder.rating.setText("Not Reviewed ★");
        }

    }

    /**
     * Purpose: "container" that holds all the information we need to display to the rider
     */
    @NonNull
    @Override
    public RideOfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_offerlist_content, parent, false);
        return new RideOfferHolder(view);
    }

    /**
     * Purpose: contains all the respective button and text views
     */
    class RideOfferHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView username;
        private TextView rating;

        /**
         * Purpose: contains all the respective button and text views
         * @param itemView the view of the item in holder
         */
        public RideOfferHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.fullname);
            rating = itemView.findViewById(R.id.rating);
            username = itemView.findViewById(R.id.username);
            username.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
            itemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    listener.onDriverSelect(getSnapshots().getSnapshot(position), position);
                    notifyItemChanged(position);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onDriverSelect(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(RideOfferAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
