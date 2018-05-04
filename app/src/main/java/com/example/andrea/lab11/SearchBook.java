package com.example.andrea.lab11;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

public class SearchBook extends AppCompatActivity {

    private String deBugTag;
    private ImageButton searchButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();

        setContentView(R.layout.search_book);

        //set toolbar
        ImageButton backArrow = findViewById(R.id.imageButton);
        backArrow.setOnClickListener((v) -> {onBackPressed();});
        TextView toolbarTitle = findViewById(R.id.back_toolbar_text);
        toolbarTitle.setText(R.string.search_book);

        searchButton = findViewById(R.id.authorSearchButton);
        searchButton.setOnClickListener(v -> {
            Log.d(deBugTag,"cliccato");
            Intent intent = new Intent(getApplicationContext(),ResultsList.class);
            startActivity(intent);
        });
    }
}
