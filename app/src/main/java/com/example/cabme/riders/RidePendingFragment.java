package com.example.cabme.riders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cabme.R;
import com.example.cabme.User;

public class RidePendingFragment extends Fragment implements View.OnClickListener {
    public User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_map_ride_searching, container, false);
        user = (User) getArguments().getSerializable("user");
        findViewsSetListeners(view);
        return view;
    }

    private void findViewsSetListeners(View view){
        Button rideOffersBtn = view.findViewById(R.id.ride_offers);
        Button rideCancelBtn = view.findViewById(R.id.ride_cancel);
        rideOffersBtn.setOnClickListener(this);
        rideCancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.ride_cancel:
                RideRequest rideRequest = new RideRequest(user.getUid());
                rideRequest.removeRequest();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove(RidePendingFragment.this);
                trans.commit();
                manager.popBackStack();
                ((RiderMapActivity)getActivity()).recreateActivity(2, 0, null);
                break;
            case R.id.ride_offers:
                // list of driver offers activity
                break;
        }
    }
}
