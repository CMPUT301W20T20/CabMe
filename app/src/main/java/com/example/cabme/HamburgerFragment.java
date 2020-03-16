package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.cabme.riders.NewRideInfoActivity;
import com.example.cabme.riders.r_historylist_activity;

public class HamburgerFragment extends Fragment {

    public ImageButton closeImgBtn;
    public TextView fullnameTv;
    public Button profileBtn;
    public Button switchBtn;
    public Button balanceBtn;
    public Button logoutBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hamburger, container, false);
        closeImgBtn = view.findViewById(R.id.button_close);
        fullnameTv = view.findViewById(R.id.full_name);
        profileBtn = view.findViewById(R.id.button_profile);
        switchBtn = view.findViewById(R.id. button_switch_user_type);
        balanceBtn = view.findViewById(R.id. button_balance);
        logoutBtn = view.findViewById(R.id. button_logout);

        closeImageButtonClick();

        return view;
    }

    public void closeImageButtonClick(){
        closeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(HamburgerFragment.this).commit();
            }
        });
    }
}
