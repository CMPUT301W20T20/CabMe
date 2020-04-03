package com.example.cabme.qrscanner;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cabme.R;
import com.example.cabme.TitleActivity;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * This Class used phone camera and scans the QR Genereated by the Customer
 * // https://www.youtube.com/watch?v=MegowI4T_L8
 */

public class ScannerQR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView txtResult;
    private Vibrator vibrate;

    /**
     * This Class used phone camera and scans the QR Genereated by the Customer
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);

        scannerView = (ZXingScannerView)findViewById(R.id.zxscan);
        txtResult = (TextView) findViewById(R.id.txt_result);

        /**
         * This Class used phone camera and scans the QR generated by the Customer
         * Uses the Manifest Permission and Opens Camera based on the Permissions
         * The User is forced to give the permission to use the Scanner
         */

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(ScannerQR.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ScannerQR.this,"You must accept to scan the QR code", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    /**
     * The rawResult is the Test string that is passed.
     * On Success, the user gets a vibrational feedback and then taken to the title activity.
     * From the title activity they can logout, become a rider or offer another ride.
     * @param rawResult
     */
    @Override
    public void handleResult(Result rawResult) {

        txtResult.setText(rawResult.getText());
        Toast.makeText(ScannerQR.this, rawResult.getText(), Toast.LENGTH_LONG).show();
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrate.vibrate(400);

        Intent intent = new Intent(ScannerQR.this, TitleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
