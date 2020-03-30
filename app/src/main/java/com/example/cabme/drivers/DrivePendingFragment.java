package com.example.cabme.drivers;

import android.app.AlertDialog;
import android.app.DownloadManager;
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

import com.example.cabme.R;
import com.example.cabme.User;
import com.example.cabme.riders.RidePendingFragment;
import com.example.cabme.riders.RideRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DrivePendingFragment extends Fragment implements View.OnClickListener {
    private TextView status;
    private TextView to;
    private TextView from;
    private TextView cost;
    public User user;
    public String docID;

    ScheduledThreadPoolExecutor executor;

    Thread thread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_ride_pending_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        docID = (String) getArguments().getSerializable("docID");

        Log.wtf("USER", user.getUid());
        findViewsSetListeners(view);

        RideRequest rideRequest = new RideRequest(docID);
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> updateStatusThread(rideRequest, view), 0, 1, TimeUnit.SECONDS);

        return view;
    }

    private void findViewsSetListeners(View view){
        status = view.findViewById(R.id.status);
        to = view.findViewById(R.id.to);
        from = view.findViewById(R.id.from);
        cost = view.findViewById(R.id.money);
    }

    private void updateStatusThread(RideRequest rideRequest, View view){
        rideRequest.readData((RideRequest.dataCallBack) (driverID, status) -> {
            if(status.equals("")){
                this.status.setText("Pending Rider Confirmation...");
            }else {
                this.status.setText(status);
            }
            if(status.equals("Active")){
                executor.shutdown();

                new AlertDialog.Builder(getContext())
                        .setMessage("The rider accepted your offer, start the ride!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                FragmentTransaction trans = manager.beginTransaction();
                                trans.remove(DrivePendingFragment.this);
                                trans.commit();
                                manager.popBackStack();
                                getActivity().recreate();                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), DriverRequestListActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(DrivePendingFragment.this);
        trans.commit();
        manager.popBackStack();
    }
}
