package com.example.andrea.lab11;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {

    final int CAMERA_REQUEST_CODE = 734;
    private String deBugTag;
    CameraSource cameraSource;
    SurfaceView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        deBugTag = this.getClass().getName();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();

        cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector).setAutoFocusEnabled(true).build();

        cameraView = findViewById(R.id.cameraView);
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override public void surfaceCreated(SurfaceHolder holder) {
                Log.d(deBugTag,"surfaceCreated");
                if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED)){
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                }else{
                    try {
                        cameraSource.start(cameraView.getHolder());
                    }catch (IOException e){
                        Log.e(deBugTag,e.getMessage());
                    }

                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(deBugTag,"surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(deBugTag,"surfaceDestroyed");
                cameraSource.stop();
            } });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0){

                    Log.d(deBugTag,barcodes.valueAt(0).displayValue);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",barcodes.valueAt(0).displayValue);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }});
    }

    public void exitButtonPressed(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            try {
                cameraSource.start(cameraView.getHolder());
            }catch (IOException e){
                Log.e(deBugTag,e.getMessage());
            }catch (SecurityException e){
                Log.e(deBugTag,e.getMessage());
            }

        } else {
            Toast.makeText(this, getString(R.string.camera_permission), Toast.LENGTH_SHORT);
            onBackPressed();
        }
    }
}
