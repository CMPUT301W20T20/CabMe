package com.example.cabme.qrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cabme.HomeMapActivity;
import com.example.cabme.R;
import com.example.cabme.TitleActivity;
import com.example.cabme.User;
import com.example.cabme.UserType;


public class QRActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    Button GoBackButton;
    private User user;
    private String fare;

    // https://www.youtube.com/watch?v=0ClcWGX2-n8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.GenBarCode);
        GoBackButton = findViewById(R.id.GoBack);
        user = (User)getIntent().getSerializableExtra("user");
        fare = getIntent().getStringExtra("fare");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String currentRider = (user.getFirstName());
                String currentFair = fare;

                String text = ("$" + currentFair + " was received");
                if (!text.equals("")){
                    new ImageDownloaderClass(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=" + text);
                    button.setVisibility(View.GONE);
                    GoBackButton.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(QRActivity.this, "Text is Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRActivity.this, HomeMapActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("userType", UserType.RIDER);
                startActivity(intent);
            }
        });
    }
}
