package com.example.cabme.riders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cabme.Driver;
import com.example.cabme.ProfileActivity;
import com.example.cabme.R;
import com.example.cabme.User;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 *
 * Purpose:
 * - The weird psuedo nav-drawer I made because I was too dumb to learn how to make the real deal
 * - Shows a menu of the persons name, details, balance, option to open look at details, switch
 *   the user type and the log out button
 * - Making the UI kinda cute :)
 *
 * TODO:
 *  [ ] Update the profile names on change of text from the ProfileActivity
 *  [ ] onChanged information
 *  [?] Can make all button clicks a switch case - maybe that will be less eye jarring
 *
 */
public class HamburgerFragment extends Fragment implements View.OnClickListener {

    private ImageButton closeImgBtn;
    private TextView fullnameTv;
    private TextView usernameTv;

    private Button profileBtn;
    private Button switchBtn;
    private Button balanceBtn;
    private Button logoutBtn;

    public User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_hamburger_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        findViewsSetListeners(view);
        setNames();
        return view;
    }

    public void findViewsSetListeners(View view){
        closeImgBtn = view.findViewById(R.id.button_close);
        fullnameTv = view.findViewById(R.id.full_name);
        usernameTv = view.findViewById(R.id.user_name);

        profileBtn = view.findViewById(R.id.button_profile);
        switchBtn = view.findViewById(R.id. button_switch_user_type);
        balanceBtn = view.findViewById(R.id. button_balance);
        logoutBtn = view.findViewById(R.id. button_logout);

        closeImgBtn.setOnClickListener(this);
        profileBtn.setOnClickListener(this);
        switchBtn.setOnClickListener(this);
        balanceBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

    }
    private void setNames(){
        user.readData(new User.userCallback() {
            @Override
            public void onCallback(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.getString("username");
                String first = documentSnapshot.getString("first");
                String last = documentSnapshot.getString("last");
                String fullname = first + " " + last;
                String ATusername = "@" + username;
                fullnameTv.setText(fullname);
                usernameTv.setText(ATusername);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.button_close:
                getFragmentManager().beginTransaction().remove(HamburgerFragment.this).commit();
                break;
            case R.id.button_profile:
                intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 1);
                break;
            case R.id.button_switch_user_type:
                getActivity().onBackPressed();
                break;
            case R.id.button_balance:
                break;
            case R.id.button_logout:
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf("HAMBURGER", "Successful ON ACTIVITY");
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(HamburgerFragment.this);
        trans.commit();
        manager.popBackStack();
        ((RiderMapActivity)getActivity()).recreateActivity(RecreateType.PROFILE_UPDATE, 0, null);
    }
}
