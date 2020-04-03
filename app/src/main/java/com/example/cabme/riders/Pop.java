package com.example.cabme.riders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cabme.Driver;
import com.example.cabme.R;
import com.example.cabme.Rating;
import com.example.cabme.User;
import com.example.cabme.qrscanner.QRActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;



/**
 *
 * TODO: Need to remove ride request from database to ridehistory
 *
 *
 * */

public class Pop extends Activity {

    private String TAG = "Firestore";

    public Driver driver;
    private Rating rating;
    public User user;

    TextView username;
    ImageButton thumbsUp;
    ImageButton thumbsDown;

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


        //get driver UID & fare using intent
        String driverUID = getIncomingIntentDriverUID();
        String fare = getIncomingIntentFare();
        String riderUID = getIncomingIntentRiderUID();

        //database references
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");

        //create driver object
        driver = new Driver(driverUID);
        user = new User(riderUID);

        //create rating object
        rating = new Rating();

        //make button click to set driver uid into the database
        thumbsUp = findViewById(R.id.thumb_up_popup);
        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * It adds rating if positive, starts QR scanner activity after rating is done
             * @param v
             */
            public void onClick(View v) {
                //Map<String, Object> data = new HashMap<>();
                //data.put("rating",rating);
                rating.pos_rev();
                Map<String, Object> data = new HashMap<>();
                data.put("rating",rating);
                Log.d(TAG, " driverUID: " + driverUID);

                db.collection("users").document(driverUID).update(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            /**
                             * This logs the success or fail message on positive rating capture
                             * @param task
                             */
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "onComplete: successful");
                                }else{
                                    Log.d(TAG, "onComplete: not successful");
                                }
                            }
                        });
                //after rating is finished we want to start QRActivity
                Intent intent = new Intent(Pop.this, QRActivity.class);

                //need to pass through amount
                intent.putExtra("fare", fare);
                intent.putExtra("user", riderUID);
                startActivity(intent);
            }
        });


        thumbsDown = findViewById(R.id.thumbs_down_popup);
        thumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating.neg_rev();
                Map<String, Object> data = new HashMap<>();
                data.put("rating",rating);
                Log.d(TAG, " driverUID: " + driverUID);

                db.collection("users").document(driverUID).update(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            /**
                             * This logs the success or fail message on negative rating capture
                             * @param task
                             */
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "onComplete: successful");
                                }else{
                                    Log.d(TAG, "onComplete: not successful");
                                }
                            }
                        });
                //after rating is finished we want to start QRActivity
                Intent intent = new Intent(Pop.this, QRActivity.class);

                //need to pass through amount
                intent.putExtra("fare", fare);
                intent.putExtra("user", riderUID);
                startActivity(intent);
            }
        });

  }
    /**
     * This gets the fare amount from intent
     * @return
     */
    private String getIncomingIntentFare(){
        Log.d(TAG, "Ride Fare: " +getIntent().getStringExtra("fare"));
        return getIntent().getStringExtra("fare");
    }

    /**
     * This gets the driver UID from intent
     * @return
     */
    private String getIncomingIntentDriverUID(){

        Log.d(TAG, "Driver UID: " +getIntent().getStringExtra("uid"));
        return getIntent().getStringExtra("uid");

    }

    /**
     * This gets the rider UID from intent
     * @return
     */
    private String getIncomingIntentRiderUID(){

        Log.d(TAG, "Rider UID: " + getIntent().getStringExtra("user"));
        return getIntent().getStringExtra("user");

    }
}
