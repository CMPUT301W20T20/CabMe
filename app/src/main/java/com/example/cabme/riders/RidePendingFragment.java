package com.example.cabme.riders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cabme.R;
import com.example.cabme.User;

/**
 * This is the fragment you see when there is a ride pending for driver offers on the HomeMapActivity
 */
public class RidePendingFragment extends Fragment implements View.OnClickListener {
    public User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_ride_pending_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        findViewsSetListeners(view);
        return view;
    }

    private void findViewsSetListeners(View view){
        Button rideOffersBtn = view.findViewById(R.id.ViewOffers);
        Button rideCancelBtn = view.findViewById(R.id.Cancel);
        rideOffersBtn.setOnClickListener(this);
        rideCancelBtn.setOnClickListener(this);
    }

    /**
     * Has a switch case that handles all the button clicks as the fragment implements the onClickListener
     * @param v the vew
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.Cancel:
                /* remove the fragment from the stack */

                /* removes the ride request from the database */
                RideRequest rideRequest = new RideRequest(user.getUid());
                rideRequest.updateRideStatus("Cancelled");
                rideRequest.removeRequest(new RideRequest.requestCallback() {
                    @Override
                    public void onCallback() {
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction trans = manager.beginTransaction();
                        trans.remove(RidePendingFragment.this);
                        trans.commit();
                        manager.popBackStack();
                        getActivity().recreate();
                    }
                });

                break;
            case R.id.ViewOffers:
                /* list of driver offers activity */
                getActivity().getFragmentManager().popBackStack(); /*not sure if we need to close this or not, i dont think so....*/
                intent = new Intent(getActivity(), RideOfferActivity.class);
                intent.putExtra("user", user);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove(RidePendingFragment.this);
                trans.commit();
                manager.popBackStack();
                startActivityForResult(intent, 1);
                break;
        }
    }
}
