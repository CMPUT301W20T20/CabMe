package com.example.cabme.drivers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cabme.R;
import com.example.cabme.User;
import com.example.cabme.qrscanner.ScannerQR;
import com.example.cabme.riders.RideRequest;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DriveActiveFragment extends Fragment implements View.OnClickListener {
    private TextView stats;
    private Button cancel;
    private Button qrScan;
    private TextView to;
    private TextView from;
    private TextView cost;
    public User user;
    private String docID;
    private RideRequest rideRequest;



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
        updateStatusThread(rideRequest);

        return view;
    }

    private void setAll(){
        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
            if(status.equals("")){
                stats.setText("Waiting for the rider...");
            }else {
                stats.setText(status);
                cancel.setVisibility(View.GONE);
                qrScan.setVisibility(View.VISIBLE);
            }
            to.setText(startAddress);
            from.setText(endAddress);
            this.cost.setText(String.format("$%s", String.valueOf(fare)));
        });
    }

    public void updateStatusThread(RideRequest rideRequest) {
        Query query = FirebaseFirestore.getInstance().collection("testrequests").whereEqualTo("UIDdriver", user.getUid());
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        Log.wtf("CHANGE", "Added");
                    case MODIFIED:
                        Log.wtf("CHANGE", "Modified");
                        rideRequest.readData((driverID, status, startAddress, endAddress, fare) -> {
                            switch (status) {
                                case "Rider Ready":
                                    stats.setText(status);
                                    if(getActivity() != null) {
                                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                        dialogBuilder
                                                .setMessage("The rider accepted your offer, start the ride!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        cancel.setVisibility(View.GONE);
                                                        qrScan.setVisibility(View.VISIBLE);
                                                        rideRequest.updateRideStatus("Active");
                                                        stats.setText("Active");
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    }
                                    break;
                                case "Completed":
                                    stats.setText(status);
                                    if(getActivity() != null) {
                                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                        dialogBuilder
                                                .setMessage("This is when the scanner comes up")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        cancel.setVisibility(View.GONE);
                                                        qrScan.setVisibility(View.VISIBLE);
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    }

                                    break;
                            }
                        });
                        break;
                    case REMOVED:
                        Log.wtf("CHANGE", "Removed");
                        if(getActivity() != null) {

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            dialogBuilder
                                    .setMessage("The rider cancelled the ride!")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            cancel.setVisibility(View.GONE);
                                            qrScan.setVisibility(View.VISIBLE);
                                            FragmentManager manager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction trans = manager.beginTransaction();
                                            trans.remove(DriveActiveFragment.this);
                                            trans.commit();
                                            manager.popBackStack();
                                            dialog.dismiss();

                                            getActivity().recreate();

                                        }
                                    }).show();
                        }
                        break;
                }
            }
        });
    }

    private void findViewsSetListeners(View view){
        cancel = view.findViewById(R.id.cancel);
        qrScan = view.findViewById(R.id.qr_scan);
        stats = view.findViewById(R.id.status);
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
                rideRequest.removeOffer(user.getUid());
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove(DriveActiveFragment.this);
                trans.commit();
                manager.popBackStack();
                getActivity().recreate();
                break;
            case R.id.qr_scan:
                /* on completions when the status is completed */
                /* instantiate th qr thing how ever */

                Intent intent = new Intent(getActivity(), ScannerQR.class);
                startActivity(intent);

                break;
        }
    }
}