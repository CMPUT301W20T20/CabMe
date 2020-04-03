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

/**
 * Using ImageDownloaderClass it Generates the QR Code.
 * https://www.youtube.com/watch?v=0ClcWGX2-n8
 */


public class QRActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    Button GoBackButton;
    private User user;
    private String fare;

    /**
     * Using ImageDownloaderClass it Generates the QR Code.
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.GenBarCode);
        GoBackButton = findViewById(R.id.GoBack);
        user = (User)getIntent().getSerializableExtra("user");
        fare = getIntent().getStringExtra("fare");

        /**
         * button is linked to "pay now", when pressed, the QR is Generated.
         */

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String currentRider = (user.getFirstName());
//                String currentFair = fare;

                String text = ( " Payment Received" ); // This can be changed to be a bit more particular


                if (!text.equals("")){
                    new ImageDownloaderClass(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=" + text);
                    button.setVisibility(View.GONE);
                    GoBackButton.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(QRActivity.this, "QR is Null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * Once the back button is pressed after paying, the rider is taken back to the title
         * activity where they can log out, become driver or request another ride.
         */

        GoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRActivity.this, TitleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}
