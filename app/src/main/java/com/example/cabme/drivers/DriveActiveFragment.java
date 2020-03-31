package com.example.cabme.drivers;

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
import com.example.cabme.qrscanner.ScannerQR;
import com.example.cabme.riders.RideRequest;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DriveActiveFragment extends Fragment implements View.OnClickListener {
    private TextView status;
    private Button cancel;
    private Button qrScan;
    private TextView to;
    private TextView from;
    private TextView cost;
    public User user;
    private String docID;
    private RideRequest rideRequest;

    ScheduledThreadPoolExecutor executor;
    ScheduledThreadPoolExecutor executor2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_ride_active_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        docID = (String) getArguments().getSerializable("docID");

        rideRequest = new RideRequest(docID);
        findViewsSetListeners(view);
        setAll();
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> updateStatusActive(rideRequest, view), 0, 1, TimeUnit.SECONDS);
        executor2 = new ScheduledThreadPoolExecutor(1);
        executor2.scheduleAtFixedRate(() -> updateStatusCompleted(rideRequest, view), 0, 1, TimeUnit.SECONDS);

        return view;
    }

    private void setAll(){
        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
            if(status.equals("")){
                this.status.setText("Waiting for a driver...");
            }else {
                this.status.setText(status);
                cancel.setVisibility(View.GONE);
                qrScan.setVisibility(View.VISIBLE);
            }
            to.setText(startAddress);
            from.setText(endAddress);
            this.cost.setText(String.format("QR$%s", String.valueOf(fare)));
        });
    }


    private void updateStatusActive(RideRequest rideRequest, View view){
        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
            if(status.equals("Active")){
                executor.shutdownNow();
                this.status.setText(status);
                new AlertDialog.Builder(getContext())
                        .setMessage("The rider accepted your offer, start the ride!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                cancel.setVisibility(View.GONE);
                                qrScan.setVisibility(View.VISIBLE);
                          }
                        }).show();

            }
        });
    }

    private void updateStatusCompleted(RideRequest rideRequest, View view){
        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
            if(status.equals("Completed")){
                executor2.shutdownNow();
                this.status.setText(status);
                new AlertDialog.Builder(getContext())
                        .setMessage("QRSCAN?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                cancel.setVisibility(View.GONE);
                                qrScan.setVisibility(View.VISIBLE);
                            }
                        }).show();
            }
        });
    }

    private void findViewsSetListeners(View view){
        cancel = view.findViewById(R.id.cancel);
        qrScan = view.findViewById(R.id.qr_scan);
        status = view.findViewById(R.id.status);
        to = view.findViewById(R.id.to);
        from = view.findViewById(R.id.from);
        cost = view.findViewById(R.id.money);
        cancel.setOnClickListener(this);
        qrScan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        RideRequest rideRequest = new RideRequest(docID);
        switch(v.getId()){
            case R.id.cancel:
                /* call to remove the request */
                /* remove fragments from backstack */
                /* finish */
                executor.shutdownNow();
                executor2.shutdownNow();
                rideRequest.updateRideStatus("Cancelled");
                rideRequest.removeRequest(() -> {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = manager.beginTransaction();
                    trans.remove(DriveActiveFragment.this);
                    trans.commit();
                    manager.popBackStack();
                    getActivity().recreate();
                });
                break;
            case R.id.qr_scan:
                /* on completions when the status is completed */
                /* instantiate th qr thing how ever */

//                Intent intent = new Intent(DriveActiveFragment.this, ScannerQR.class);
//                startActivity(intent);


                break;
        }
    }
}
