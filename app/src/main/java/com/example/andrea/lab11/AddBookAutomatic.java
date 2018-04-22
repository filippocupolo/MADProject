package com.example.andrea.lab11;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class AddBookAutomatic extends AppCompatActivity {

    private float downX, downY,upY, upX;
    private String deBugTag;
    final private int SCAN_INTENT = 943;
    private Context context;
    private BookInfo book;
    private MyUser user;
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_automatic);
        deBugTag = this.getClass().getName();
        context  = getApplicationContext();
        user = new MyUser(context);
        book = new BookInfo(context);

        EditText editISBN = findViewById(R.id.editISBN);
        editISBN.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            apiRequest(editISBN.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });
    }

    public void goToManualButtonClick(View v){
        Intent intent = new Intent(
                getApplicationContext(),
                insertBook.class);
        startActivity(intent);
    }

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
            apiRequest(ISBN);
        }
    }

    private void apiRequest(String ISBN){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://www.googleapis.com/books/v1/volumes?q=isbn:" + ISBN, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try{
                    JSONObject rootJSON=new JSONObject(new String(response));
                    JSONObject volumeInfo = rootJSON.getJSONArray("items").optJSONObject(0).getJSONObject("volumeInfo");

                    book.setBookTitle(volumeInfo.getString("title"));
                    book.setOwner(user.getUserID());
                    book.setAuthor(volumeInfo.getJSONArray("authors").getString(0));
                    book.set_ISBN(ISBN);
                    book.setEditionYear(volumeInfo.getString("publishedDate"));

                    Intent intent = new Intent(getApplicationContext(),insertBook.class);
                    intent.putExtra("book", book);
                    startActivity(intent);

                }catch (JSONException e){
                    Log.e(deBugTag,"JSONException");
                    Log.e(deBugTag,e.getMessage());

                    //TODO cambia stringa toast
                    Toast.makeText(context,"impossibile trovare libro aggiungi libro manualmente",Toast.LENGTH_SHORT);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(deBugTag,statusCode+"");
                //TODO cambia stringa toast
                Toast.makeText(context,"impossibile connettersi riprova o aggiungi libro manualmente",Toast.LENGTH_SHORT);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}

