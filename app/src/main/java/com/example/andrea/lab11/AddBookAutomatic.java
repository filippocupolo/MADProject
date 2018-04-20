package com.example.andrea.lab11;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.net.HttpURLConnection;

public class AddBookAutomatic extends AppCompatActivity {

    private float downX, downY,upY, upX;
    private String deBugTag;
    final private int SCAN_INTENT = 943;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_automatic);
        deBugTag = this.getClass().getName();

    }

    public void goToManualButtonClick(View v){
        Intent intent = new Intent(
                getApplicationContext(),
                insertBook.class);
        startActivity(intent);
    }

    public void scanBarcode(View view){
        Intent intent = new Intent(
                getApplicationContext(),
                ScanActivity.class);
        startActivityForResult(intent, SCAN_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCAN_INTENT && resultCode== Activity.RESULT_OK){
            String ISBN = data.getStringExtra("result");
            HttpURLConnection ape;
        }
    }

    //TODO da eliminare swipe che fa schifo
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;

                // swipe horizontal?
                if (Math.abs(deltaX) > 100) {
                    // left or right
                    if (deltaX < 0) {
                        Log.d(this.getClass().getName(), "swipe");
                        Intent intent = new Intent(
                                getApplicationContext(),
                                showProfile.class);
                        startActivity(intent);

                    }
                    //if(deltaX > 0) { this.onRightToLeftSwipe(v); return super.onTouchEvent(event); }
                } else {
                    Log.i(this.getClass().getName(), "Swipe was only " + Math.abs(deltaX) + " long, need at least 100");
                }

            }

        }
        return super.onTouchEvent(event);
    }

}

