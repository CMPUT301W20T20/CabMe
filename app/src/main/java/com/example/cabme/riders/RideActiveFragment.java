package com.example.cabme.riders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cabme.HomeMapActivity;
import com.example.cabme.R;
import com.example.cabme.User;
import com.google.firebase.database.core.view.Change;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.remote.WatchChange;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is the fragment you see when there is a ride pending for driver offers on the HomeMapActivity
 */
public class RideActiveFragment extends Fragment implements View.OnClickListener {
    public User user;
    private Button rideOffersBtn;
    private Button rideCancelBtn;
    private Button rideCompleteBtn;
    private RideRequest rideRequest;
    private TextView fare;
    private TextView to;
    private TextView from;
    private TextView stats;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_ride_active_fragment, container, false);
        user = (User) getArguments().getSerializable("user");

        rideRequest = new RideRequest(user.getUid());
        findViewsSetListeners(view);
        setAll();

        updateStatusThread(rideRequest);

        return view;
    }

    private void setAll(){
        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
            if(status.equals("")){
                stats.setText("Waiting for a driver...");
            }else {
                stats.setText(status);
                rideCancelBtn.setVisibility(View.GONE);
                rideOffersBtn.setVisibility(View.GONE);
                rideCompleteBtn.setVisibility(View.VISIBLE);
            }
            to.setText(startAddress);
            from.setText(endAddress);
            this.fare.setText(String.format("$%s", String.valueOf(fare)));
        });
    }

    public void updateStatusThread(RideRequest rideRequest) {
        Query query = FirebaseFirestore.getInstance().collection("testrequests").whereEqualTo("UIDrider", user.getUid());
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        Log.wtf("CHANGE", "Added");
                        break;
                    case MODIFIED:
                        Log.wtf("CHANGE", "Modified");
                        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
                            switch (status){
                                case "Active":
                                    stats.setText(status);
//                                    rideCancelBtn.setVisibility(View.GONE);
//                                    rideOffersBtn.setVisibility(View.GONE);
//                                    rideCompleteBtn.setVisibility(View.VISIBLE);

                                    if(getActivity() != null){
                                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//                                        LayoutInflater inflater = getLayoutInflater();
//                                        final View dialogView = inflater.inflate(R.layout.r_ride_active_fragment,null);
//                                        dialogBuilder.setView(dialogView);
                                        dialogBuilder.setMessage("Your driver is on the way!")
                                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                                    rideCancelBtn.setVisibility(View.GONE);
                                                    rideOffersBtn.setVisibility(View.GONE);
                                                    rideCompleteBtn.setVisibility(View.VISIBLE);
                                                    dialog.cancel();
                                                    dialog.dismiss();
                                                }).show();
                                    }

                                    break;
                                case "Completed":
                                    if(getActivity() != null) {
                                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//                                        LayoutInflater inflater = getLayoutInflater();
//                                        final View dialogView = inflater.inflate(R.layout.r_ride_active_fragment, null);
//                                        dialogBuilder.setView(dialogView);
                                        dialogBuilder
                                                .setMessage("there is a barcoodde here u can scan")
                                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                                    rideCancelBtn.setVisibility(View.GONE);
                                                    rideOffersBtn.setVisibility(View.GONE);
                                                    rideCompleteBtn.setVisibility(View.VISIBLE);
                                                    dialog.cancel();
                                                    dialog.dismiss();
                                                }).show();
                                    }
                            }
                        });
                        break;
                    case REMOVED:
                        Log.wtf("CHANGE", "Removed");
                        break;
                }
            }
        });
    }


    private void findViewsSetListeners(View view){
        rideOffersBtn = view.findViewById(R.id.ViewOffers);
        rideCancelBtn = view.findViewById(R.id.Cancel);
        rideCompleteBtn = view.findViewById(R.id.CompleteRide);
        to = view.findViewById(R.id.to);
        from = view.findViewById(R.id.from);
        fare = view.findViewById(R.id.money);
        stats = view.findViewById(R.id.status);
        rideOffersBtn.setOnClickListener(this);
        rideCancelBtn.setOnClickListener(this);
        rideCompleteBtn.setOnClickListener(this);
    }

    /**
     * Has a switch case that handles all the button clicks as the fragment implements the onClickListener
     * @param v the vew
     */
    @Override
    public void onClick(View v) {
        RideRequest rideRequest = new RideRequest(user.getUid());
        Intent intent;
        switch(v.getId()) {
            case R.id.Cancel:
                /* removes the ride request from the database */
                rideRequest.updateRideStatus("Cancelled");
                rideRequest.removeRequest(() -> {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = manager.beginTransaction();
                    trans.remove(RideActiveFragment.this);
                    trans.commit();
                    manager.popBackStack();
                    getActivity().recreate();
                });
                break;
            case R.id.ViewOffers:
                /* list of driver offers activity */
                    intent = new Intent(getActivity(), RideOfferActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                break;

            case R.id.CompleteRide:
                rideRequest.updateRideStatus("Completed");
                stats.setText("Completed");

                /* TODO
                 *  - barcode thing goes here.
                 *  - rider presses complete ride, a dialogue pops up
                 *  - could use or could not, doesn't matter
                 *  - could even stop the executor here honestly
                 *  - after barcode scanning,
                 *  - return to home
                 */

//                rideRequest.removeRequest(new RideRequest.requestCallback() {
//                    @Override
//                    public void onCallback() {
//                        FragmentManager manager = getActivity().getSupportFragmentManager();
//                        FragmentTransaction trans = manager.beginTransaction();
//                        trans.remove(RideActiveFragment.this);
//                        trans.commit();
//                        manager.popBackStack();
//                        getActivity().recreate();
//                    }
//                });
                break;
        }
    }
}