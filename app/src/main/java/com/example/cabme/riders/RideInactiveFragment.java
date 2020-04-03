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

/**
 * This is the fragment for when there is not active ride - displays on MapViewActivity
 */
public class RideInactiveFragment extends Fragment implements View.OnClickListener {
    private TextView helloUser;
    public User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_ride_inactive_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        findViewsSetListeners(view);
        setWelcome();
        return view;
    }

    /**
     * Setting the listeners and finding views here
     * @param view the view bro lol
     */
    private void findViewsSetListeners(View view){
        Button rideHistoryBtn = view.findViewById(R.id.ride_history);
        Button rideNewBtn = view.findViewById(R.id.new_offer);
        helloUser = view.findViewById(R.id.hello_user);
        rideHistoryBtn.setOnClickListener(this);
        rideNewBtn.setOnClickListener(this);
    }

    /**
     * Sets the welcome message of the user
     */
    private void setWelcome(){
        user.readData((email, firstname, lastname, username, phone, rating) -> {
            String welcomeText = "Hey " + firstname +",";
            helloUser.setText(welcomeText);
        });
    }

    /**
     * overrides the onclick and has the buttons for requesting a new ride and viewing the ride history
     * @param v the view
     */
    @Override
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


    /**
     * On finish of the last activity go hereto remove this fragment from the stack
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(RideInactiveFragment.this);
        trans.commit();
        manager.popBackStack();
    }
}
