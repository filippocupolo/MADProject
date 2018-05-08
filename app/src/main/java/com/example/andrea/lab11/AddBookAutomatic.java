package com.example.andrea.lab11;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
    private String previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_automatic);
        deBugTag = this.getClass().getName();
        context  = getApplicationContext();
        user = new MyUser(context);
        book = new BookInfo();
        previousActivity = getIntent().getStringExtra("caller");

        //set toolbar
        ImageButton backArrow = findViewById(R.id.imageButton);
        backArrow.setOnClickListener((v) -> {onBackPressed();});
        TextView toolbarTitle = findViewById(R.id.back_toolbar_text);
        toolbarTitle.setText(R.string.add_book);

        EditText editISBN = findViewById(R.id.editISBN);
        editISBN.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            String isbnEditText = editISBN.getText().toString();
                            if(isbnEditText.equals(""))
                                return false;
                            apiRequest(isbnEditText);
                            return true;
                        }
                        return false;
                    }
                });

    }

    @Override
    protected void onStart(){
        super.onStart();
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

                    Toast.makeText(context,getString(R.string.book_not_found),Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(deBugTag,statusCode+"");
                Toast.makeText(context,getString(R.string.book_not_found),Toast.LENGTH_SHORT);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.general_menu, popup.getMenu());

        popup.show();

        //click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_logout:
                        Utilities.signOut(context);
                        return true;
                    case R.id.menu_edit_profile:
                        //Log.d("popup", "i:" + getIntent().getStringExtra("caller") + " c:"+this.getClass()+ "a: "+getApplicationContext());
                        Utilities.goToEditProfile(getApplicationContext(), previousActivity,
                                "addBookAutomatic", AddBookAutomatic.this);
                        return true;
                    case R.id.menu_search_book:
                        Intent intent = new Intent(getApplicationContext(),SearchBook.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}

