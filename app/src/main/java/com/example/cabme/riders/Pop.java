package com.example.cabme.riders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.example.cabme.Driver;
import com.example.cabme.R;
import com.example.cabme.Rating;
import com.example.cabme.qrscanner.QRActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



/**
 *
 * TODO: Need to remove ride request from database to ridehistory
 *
 *
 * */

public class Pop extends Activity {

    private final String TAG = "Firestore";
    private Rating review;

    private ImageButton thumbsUp;
    private ImageButton thumbsDown;

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
        String driverUID = getIntent().getStringExtra("driverUID");
        String fare = getIntent().getStringExtra("fare");
        String riderUID = getIntent().getStringExtra("riderUID");

        //database references
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");

        //create rating object
        Driver driver = new Driver(driverUID);
        driver.readData((email, firstname, lastname, username, phone, rating) -> {
            review = rating;
        });

        //make button click to set driver uid into the database
        thumbsUp = findViewById(R.id.thumb_up_popup);
        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review.posRev();
                finishReview(driverUID, riderUID, fare);
            }
        });


        thumbsDown = findViewById(R.id.thumbs_down_popup);
        thumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                review.negRev();
                finishReview(driverUID, riderUID, fare);
            }
        });

  }

    private void finishReview(String driverUID, String riderUID, String fare) {
        Map<String, Object> data = new HashMap<>();
        data.put("rating",review);
        Log.d(TAG, " driverUID: " + driverUID);

        collectionReference.document(driverUID).update(data)
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
        //after rating is finished we want to start QRActivity
        Intent intent = new Intent(Pop.this, QRActivity.class);

        //need to pass through amount
        intent.putExtra("fare", fare);
        intent.putExtra("riderUID", riderUID);
        intent.putExtra("driverUID", driverUID);
        startActivity(intent);

    }

}
