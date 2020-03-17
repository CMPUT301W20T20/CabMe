package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cabme.riders.NewRideInfoActivity;
import com.example.cabme.riders.RiderHistoryListActivity;

/**
 *
 * Purpose:
 * - UI for rider choosing new ride and stuff
 * - Idk man I think it's pretty looking its like Uber but bad :P
 *
 * TODO:
 *  [ ] Update the intro with users first name to update on edit and back
 *  [ ] Make it not show null :/
 *
 */
public class HomeMapRiderFragment extends Fragment {
    public ImageButton hamburgerMenuBtn;
    public TextView helloUser;
    public Button rideNewBtn;
    public Button rideHistoryBtn;
    public User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_map_rider_fragment, container, false);

        rideHistoryBtn = view.findViewById(R.id.ride_history);
        rideNewBtn = view.findViewById(R.id.ride_new);
        helloUser = view.findViewById(R.id.hello_user);

        user = (User) getArguments().getSerializable("user");

        rideNewButtonClick();
        rideHistoryButtonClick();
        setWelcome();

        return view;
    }

    public void setWelcome(){
        String welcomeText = "Hello " + user.getFirstName()+",";
        helloUser.setText(welcomeText);
    }

    public void rideNewButtonClick(){
        rideNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewRideInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    public void rideHistoryButtonClick(){
        rideHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RiderHistoryListActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }
}
