package com.example.cabme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class QRActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;

    // https://www.youtube.com/watch?v=0ClcWGX2-n8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.GenBarCode);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentRider = "Rider"; // Change this to current rider //THIS IS DUMMY
                String currentFair = "70"; // Change this to fair amount       //THIS IS DUMMY

                String text = (currentRider + " paid you $" + currentFair);
                if (!text.equals("")){
                    new ImageDownloaderClass(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=" + text);
                }
                else {
                    Toast.makeText(QRActivity.this, "Text is Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
