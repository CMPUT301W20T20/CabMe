package com.example.cabme.riders;

import android.content.Intent;
import android.os.Bundle;
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

public class RideInactiveFragment extends Fragment implements View.OnClickListener {
    private TextView helloUser;
    public User user;
    @Override
    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_ride_inactive_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        findViewsSetListeners(view);
        setWelcome();
        return view;
    }

    /**
     * This sets the listeners and finding views here
     * @param view
     */
    private void findViewsSetListeners(View view){
        Button rideHistoryBtn = view.findViewById(R.id.ride_history);
        Button rideNewBtn = view.findViewById(R.id.new_offer);
        helloUser = view.findViewById(R.id.hello_user);
        rideHistoryBtn.setOnClickListener(this);
        rideNewBtn.setOnClickListener(this);
    }

    /**
     * This sets the welcome message for the user
     */
    private void setWelcome(){
        user.readData((email, firstname, lastname, username, phone, rating) -> {
            String welcomeText = "Hey " + firstname +",";
            helloUser.setText(welcomeText);
        });
    }

    @Override
    /**
     * This overrides the onclick and has buttons for requesting a new ride and 
     * viewing the ride history
     * @param v
     */
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Intent intent;
        switch(v.getId()){
            case R.id.ride_history:
                intent = new Intent(getContext(), RiderHistoryListActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;

            case R.id.new_offer:
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                /*
                 * new ride request starts for a result -> onActivityResult
                 *  takes you back there after onFinish() in the next activity
                 */
                intent = new Intent(getContext(), RideRequestSearchActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    /**
     * This removes this fragment from the stack upon the finishing of the last activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(RideInactiveFragment.this);
        trans.commit();
        manager.popBackStack();
    }
}
