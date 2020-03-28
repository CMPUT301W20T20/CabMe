package com.example.cabme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileViewActivity extends Fragment implements View.OnClickListener{
    private ImageButton close;
    private TextView fullname;
    private TextView username;
    private TextView driverRating;
    private TextView phoneNumber;
    private TextView emailAddress;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.profile_view_activity, container, false);
        String uid = (String) getArguments().getSerializable("uid");
        user = new User(uid);
        Driver driver = new Driver(uid);
        findViewsSetListeners(view);
        setInformation(user, driver);
        return view;
    }

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

    private void setInformation(User user, Driver driver){
        user.readData((email, firstname, lastname, username, phone, rating) -> {
            String fullname = firstname + " " + lastname;
            this.fullname.setText(fullname);
            this.username.setText(String.format("@%s", username));
            this.phoneNumber.setText(phone);
            this.emailAddress.setText(email);
        });
        driver.readData((email, firstname, lastname, username, phone, rating) -> {
            driverRating.setText(String.valueOf(rating));
        });
    }

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
                getParentFragmentManager().beginTransaction().remove(ProfileViewActivity.this).commit();
                break;

        }
    }
}
