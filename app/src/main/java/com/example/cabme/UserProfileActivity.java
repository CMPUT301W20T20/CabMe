package com.example.cabme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class UserProfileActivity extends Fragment implements View.OnClickListener{
    private ImageButton close;
    private TextView fullname;
    private TextView username;
    private TextView driverRating;
    private TextView phoneNumber;
    private TextView emailAddress;
    private User user;
    private final Integer REQUEST_PERMISSION = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.profile_view_activity, container, false);
        String uid = (String) getArguments().getSerializable("uid");
        user = new User(uid);
        findViewsSetListeners(view);
        setInformation(user);

        Log.wtf("USER", user+"");

        return view;
    }

    /**
     * Setting views and listeners
     * @param view the view
     */
    private void findViewsSetListeners(View view){
        fullname = view.findViewById(R.id.fullname);
        username = view.findViewById(R.id.username);
        driverRating = view.findViewById(R.id.driverrating);
        phoneNumber = view.findViewById(R.id.phonenumber);
        emailAddress = view.findViewById(R.id.emailaddress);
        close = view.findViewById(R.id.button_close);
        phoneNumber.setOnClickListener(this);
        emailAddress.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    /**
     * Sets the information of the user from the database
     * @param user the user model used to get data from
     */
    private void setInformation(User user){
        /* necessary to pull data like this bc firebase is async */
        user.readData((email, firstname, lastname, uname, phone, rating) -> {
            String name = firstname + " " + lastname;
            fullname.setText(name);
            username.setText(String.format("@%s", uname));
            phoneNumber.setText(phone);
            emailAddress.setText(email);
            if (rating.isReviewed()) {
                driverRating.setText(String.format("%3.0f%% ★  %d+ / %d-", rating.percentRating()*100, rating.getPosRev(), rating.getNegRev()));
            }
            else {
                driverRating.setText("Not Reviewed ★");
            }
        });

    }

    /**
     * handles all the clicking in the activity (clicking on the phone number & email address)
     * @param v the view
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.phonenumber:
                user.readData((email, firstname, lastname, username, phone, rating) -> {
                    String uri = "tel:" + phone.trim() ;
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                });
                break;
            case R.id.emailaddress:
                user.readData((email, firstname, lastname, username, phone, rating) -> {
                    String eSubj = username + " from CabMe messaged you";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:?to="+ email + "&subject=" + eSubj + "&body=" + "body");
                    intent.setData(data);
                    startActivity(intent);
                });
                break;
            case R.id.button_close:
                getParentFragmentManager().beginTransaction().remove(UserProfileActivity.this).commit();
                break;

        }
    }

    /**
     * Purpose: first requests permission, then opens the phone app
     * @param phone
     */
    private void phoneIntent(String phone){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone));
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            }
        }
    }

    /**
     * Purpose: open the email intent, with the recipients name passed through
     * @param recipientList
     */
    private void emailIntent(String recipientList){

        String [] recipient = recipientList.split(",");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.setType("message/rfc822"); //MIME type rcf822
        startActivity(intent);
    }
}