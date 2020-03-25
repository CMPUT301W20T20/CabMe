package com.example.cabme.riders;

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

public class RideInactiveFragment extends Fragment implements View.OnClickListener {
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
        View view = inflater.inflate(R.layout.home_map_ride_inactive, container, false);
        user = (User) getArguments().getSerializable("user");
        findViewsSetListeners(view);
        setWelcome();
        return view;
    }

    private void findViewsSetListeners(View view){
        rideHistoryBtn = view.findViewById(R.id.ride_history);
        rideNewBtn = view.findViewById(R.id.ride_new);
        helloUser = view.findViewById(R.id.hello_user);
        rideHistoryBtn.setOnClickListener(this);
        rideNewBtn.setOnClickListener(this);
    }

    private void setWelcome(){
        String welcomeText = "Hello " + user.getFirstName()+",";
        helloUser.setText(welcomeText);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.ride_history:
                intent = new Intent(getContext(), RiderHistoryListActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.ride_new:
                intent = new Intent(getContext(), RideRequestSearchActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 1);
                break;
        }
    }

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
