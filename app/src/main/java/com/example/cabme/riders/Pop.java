package com.example.cabme.riders;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cabme.Driver;
import com.example.cabme.R;
import com.example.cabme.Rating;
import com.example.cabme.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class Pop extends Activity {

    private String TAG = "Firestore";

    public Driver driver;
    private Rating rating;
    public User user;

    TextView username;
    Button thumbsUp;
    Button thumbsDown;

    private transient FirebaseFirestore db;
    private transient CollectionReference collectionReference;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_popup);

        //getting device screen height and width
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //setting layout settings
        getWindow().setLayout((int) (width * 0.8), (int) (height*0.3));

        //get driver UID using intent
        String driverUID = getIncomingIntent();

        //database references
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");

        //create driver object
        driver = new Driver(driverUID);

        //create rating object
        rating = new Rating();

        //make button click to set driver uid into the database
        thumbsUp = findViewById(R.id.thumb_up_popup);
        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Map<String, Object> data = new HashMap<>();
                //data.put("rating",rating);
                rating.pos_rev();
                Map<String, Object> data = new HashMap<>();
                data.put("rating",rating);
                Log.d(TAG, " test" + driverUID);

                db.collection("users").document(driverUID).update(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "onComplete: successful");
                                }else{
                                    Log.d(TAG, "onComplete: not successful");
                                }
                            }
                        });

            }
        });
        thumbsDown = findViewById(R.id.thumbs_down_popup);

    }

    /**
     * Purpose: getting the intent sent from RiderOffer activity, getting the username
     */
    private String getIncomingIntent(){

        Log.d(TAG, "Driver UID: " +getIntent().getStringExtra("uid"));

        return getIntent().getStringExtra("uid");

    }
}
