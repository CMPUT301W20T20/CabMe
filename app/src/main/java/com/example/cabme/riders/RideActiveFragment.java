package com.example.cabme.riders;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;

        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentTransaction;

        import com.example.cabme.HomeMapActivity;
        import com.example.cabme.R;
        import com.example.cabme.User;

/**
 * This is the fragment you see when there is a ride pending for driver offers on the HomeMapActivity
 */
public class RideActiveFragment extends Fragment implements View.OnClickListener {
    public User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_ride_active_fragment, container, false);
        user = (User) getArguments().getSerializable("user");
        findViewsSetListeners(view);
        return view;
    }

    private void findViewsSetListeners(View view){
        Button rideCancelBtn = view.findViewById(R.id.complete_ride);
        rideCancelBtn.setOnClickListener(this);
    }

    /**
     * Has a switch case that handles all the button clicks as the fragment implements the onClickListener
     * @param v the vew
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.complete_ride:
                RideRequest rideRequest = new RideRequest(user.getUid());
                rideRequest.updateRideStatus("Completed");
                // show barcode stuff
                break;
        }
    }
}
