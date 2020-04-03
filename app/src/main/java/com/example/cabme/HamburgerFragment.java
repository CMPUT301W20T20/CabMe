package com.example.cabme;

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

import com.google.firebase.auth.FirebaseAuth;

/**
 *
 * Purpose:
 * - The weird psuedo nav-drawer I made because I was too dumb to learn how to make the real deal
 * - Shows a menu of the persons name, details, balance, option to open look at details, switch
 *   the user type and the log out button
 * - Making the UI kinda cute :)
 *
 * TODO:
 *  [x] Update the profile names on change of text from the ProfileActivity
 *  [x] onChanged information
 *  [x] Can make all button clicks a switch case - maybe that will be less eye jarring
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

    private User user;
    private String TAG;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_map_hamburger_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        TAG = getTag();
        Log.d(TAG, TAG);
        findViewsSetListeners(view);
        setNames();
        return view;
    }

    /**
     * Sets the listeners and finds all the views in the fragment
     * @param view the view
     */
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

    /**
     * Set the information of the user in the side drawer
     */
    private void setNames(){
        user.readData((email, firstname, lastname, username, phone, rating) -> {
            String fullname = firstname + " " + lastname;
            String ATusername = "@" + username;
            fullnameTv.setText(fullname);
            usernameTv.setText(ATusername);
        });
    }

    /**
     * Handles all the button clicks in the fragment
     * @param v the view
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.button_close:
                getParentFragmentManager().beginTransaction().remove(HamburgerFragment.this).commit();
                break;
            case R.id.button_profile:
                intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 1);
                break;
            case R.id.button_switch_user_type:
                intent = new Intent(getActivity(), TitleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.button_balance:
                break;
            case R.id.button_logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

        }
    }

    /**
     * When the next activity calls onFinish() you end up here and removing the fragment from the stack
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf("HAMBURGER", "Successful ON ACTIVITY");
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(HamburgerFragment.this);
        trans.commit();
        manager.popBackStack();
//        ((HomeMapActivity)getActivity()).recreate();
    }
}
