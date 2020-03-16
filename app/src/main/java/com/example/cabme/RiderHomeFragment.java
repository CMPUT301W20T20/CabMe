package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cabme.riders.NewRideInfoActivity;
import com.example.cabme.riders.r_historylist_activity;

public class RiderHomeFragment extends Fragment {
    public ImageButton hamburgerMenuBtn;
    public TextView helloUser;
    public Button rideNewBtn;
    public Button rideHistoryBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_home, container, false);
        rideHistoryBtn = view.findViewById(R.id.ride_history);
        rideNewBtn = view.findViewById(R.id.ride_new);
        helloUser = view.findViewById(R.id.hello_user);

        rideNewButtonClick();
        rideHistoryButtonClick();

        return view;
    }

    public void rideNewButtonClick(){
        rideNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewRideInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    public void rideHistoryButtonClick(){
        rideHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), r_historylist_activity.class);
                startActivity(intent);
            }
        });
    }
}
