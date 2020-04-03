package com.example.cabme.qrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cabme.R;
import com.example.cabme.TitleActivity;

import com.example.cabme.riders.RideRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;



public class QRActivity extends AppCompatActivity {

    private static final String TAG = "PLS";

    private String user;
    private String  rider;
    private ImageView imageView;
    private Button button;
    private Button backButton;

    private String fare;

    // https://www.youtube.com/watch?v=0ClcWGX2-n8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.GenBarCode);
        user = getIntent().getStringExtra("driverUID");
        backButton = findViewById(R.id.GoBack);

        fare = getIntent().getStringExtra("fare");
        rider = getIntent().getStringExtra("riderUID");


        Log.d(TAG, "Driver: " + user + "Rider: " + rider);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text =  fare + " QR bucks received";


                if (!text.equals("")){
                    new ImageDownloaderClass(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=" + text );
                    button.setVisibility(View.GONE);
                    backButton.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(QRActivity.this, "Text is Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RideRequest rideRequest = new RideRequest(user);
                FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();

                //get from location
                DocumentReference from = firebaseFirestore.collection("testrequests").document(rider);

                //get to location
                DocumentReference to = firebaseFirestore.collection("users").document(rider).collection("ridehistory").document();

                //remove document from testrequests, to ride history
                rideRequest.moveFirestoreDocument(from,to);

                //go back to title page activity
                Intent intent = new Intent(QRActivity.this, TitleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}
