package com.example.cabme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/* TO DO
 *
 * [ ] Update the profile names on change of text from the ProfileActivity
 *
 */
public class HamburgerFragment extends Fragment {

    public ImageButton closeImgBtn;
    public TextView fullnameTv;
    public TextView usernameTv;

    public Button profileBtn;
    public Button switchBtn;
    public Button balanceBtn;
    public Button logoutBtn;

    public User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hamburger_fragment, container, false);
        closeImgBtn = view.findViewById(R.id.button_close);
        fullnameTv = view.findViewById(R.id.full_name);
        usernameTv = view.findViewById(R.id.user_name);

        profileBtn = view.findViewById(R.id.button_profile);
        switchBtn = view.findViewById(R.id. button_switch_user_type);
        balanceBtn = view.findViewById(R.id. button_balance);
        logoutBtn = view.findViewById(R.id. button_logout);

        user = (User) getArguments().getSerializable("user");
        setNames();
        onButtonClicks();

        return view;
    }

    public void setNames(){
        String fullname = user.getFirstName() + " " + user.getLastName();
        fullnameTv.setText(fullname);
        usernameTv.setText("@" + user.getUsername());
    }

    public void onButtonClicks(){
        closeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(HamburgerFragment.this).commit();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 1);
            }
        });

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        balanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }
}
