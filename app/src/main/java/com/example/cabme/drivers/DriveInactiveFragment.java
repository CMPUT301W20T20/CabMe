package com.example.cabme.drivers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cabme.HomeMapActivity;
import com.example.cabme.R;
import com.example.cabme.User;
import com.example.cabme.riders.RiderHistoryListActivity;

public class DriveInactiveFragment extends Fragment implements View.OnClickListener {
    private TextView helloUser;
    private Button offer;
    private TextView question;
    private boolean offered;
    public User user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_ride_inactive, container, false);
        user = (User) getArguments().getSerializable("user");
        offered = false;
        findViewsSetListeners(view);
        setWelcome();
        return view;
    }

    private void findViewsSetListeners(View view){
        offer = view.findViewById(R.id.new_offer);
        question = view.findViewById(R.id.question);
        helloUser = view.findViewById(R.id.hello_user);
        offer.setOnClickListener(this);
    }

    private void setWelcome(){
        user.readData(documentSnapshot -> {
            String firstName = documentSnapshot.getString("first");
            String welcomeText = "Hello " + firstName +",";
            helloUser.setText(welcomeText);
        });
    }

    @Override
    public void onClick(View v) {
        if (offered) {
            offer.setText("Offer");
            question.setText("Make Offer?");
        }
        else {
            offer.setText("Remove Offer");
            question.setText("Offered");
        }
        offered = !offered;
        ((HomeMapActivity)getActivity()).manageOffer();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        FragmentManager manager = getActivity().getSupportFragmentManager();
//        FragmentTransaction trans = manager.beginTransaction();
//        trans.remove(RideInactiveFragment.this);
//        trans.commit();
//        manager.popBackStack();
    }
}
