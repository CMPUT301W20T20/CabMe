package com.example.cabme.drivers;

import android.app.DownloadManager;
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
import com.example.cabme.riders.RideRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriveActiveFragment extends Fragment implements View.OnClickListener {
    private TextView helloUser;
    private Button button0;
    private Button button1;
    public User user;
    public String docID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_ride_active_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        docID = (String) getArguments().getSerializable("docID");

        Log.wtf("USER", user.getUid());
        findViewsSetListeners(view);

        RideRequest rideRequest = new RideRequest(docID);
        ExecutorService service = Executors.newFixedThreadPool(4);
        service.submit(new Runnable() {
            public void run() {

            }
        });

        return view;
    }

    private void findViewsSetListeners(View view){
        button0 = view.findViewById(R.id.button0);
        button1 = view.findViewById(R.id.button1);
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
        trans.remove(DriveActiveFragment.this);
        trans.commit();
        manager.popBackStack();
    }
}
