package com.example.andrea.lab11;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AddBookAutomatic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_automatic);

    }

    public void goToManualButtonClick(View v){
        Intent intent = new Intent(
                getApplicationContext(),
                insertBook.class);
        startActivity(intent);
    }
}

