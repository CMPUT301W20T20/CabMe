package com.example.cabme.riders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cabme.R;
import com.example.cabme.User;

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
    private ScheduledThreadPoolExecutor executor;
    private TextView fare;
    private TextView to;
    private TextView from;
    private TextView status;


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
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() ->
                updateStatusThread(rideRequest), 0, 1, TimeUnit.SECONDS);

        return view;
    }

    private void setAll(){
        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
            if(status.equals("")){
                this.status.setText("Waiting for a driver...");
            }else {
                this.status.setText(status);
                rideCancelBtn.setVisibility(View.GONE);
                rideOffersBtn.setVisibility(View.GONE);
                rideCompleteBtn.setVisibility(View.VISIBLE);
            }
            to.setText(startAddress);
            from.setText(endAddress);
            this.fare.setText(String.format("QR$%s", String.valueOf(fare)));
        });
    }

    private void updateStatusThread(RideRequest rideRequest){
        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
            if(status.equals("Active")){
                executor.shutdownNow();
                this.status.setText(status);
                new AlertDialog.Builder(getContext())
                        .setMessage("Your driver is on the way!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                rideCancelBtn.setVisibility(View.GONE);
                                rideOffersBtn.setVisibility(View.GONE);
                                rideCompleteBtn.setVisibility(View.VISIBLE);
                            }
                        }).show();
            }
        });
    }

    /* On dialogue */
    private void updateOnDriverReady(RideRequest rideRequest){
        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
            if(status.equals("Driver Ready Pickup")){
                executor.shutdownNow();
                this.status.setText("Active");
                new AlertDialog.Builder(getContext())
                        .setMessage("The rider accepted your offer, start the ride!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

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
        status = view.findViewById(R.id.status);
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
                executor.shutdown();
                rideRequest.updateRideStatus("Completed");
                this.status.setText("Completed");

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
