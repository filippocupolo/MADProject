package com.example.andrea.lab11;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SearchBook extends AppCompatActivity {

    private String deBugTag;
    private ImageButton authorSearchButton;
    private ImageButton titleSearchButton;
    private ImageButton publisherSearchButton;
    private ImageButton ISBNSearchButton;
    private EditText authorEditText;
    private EditText titleEditText;
    private EditText publisherEditText;
    private EditText ISBNEditText;

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

        //get elements
        authorSearchButton = findViewById(R.id.authorSearchButton);
        titleSearchButton = findViewById(R.id.titleSearchButton);
        publisherSearchButton = findViewById(R.id.publisherSearchButton);
        ISBNSearchButton = findViewById(R.id.ISBNSearchButton);
        authorEditText = findViewById(R.id.searchAuthor);
        titleEditText = findViewById(R.id.searchTitle);
        publisherEditText = findViewById(R.id.searchPublisher);
        ISBNEditText = findViewById(R.id.searchISBN);

        authorSearchButton.setOnClickListener(v -> {
            if(authorEditText.getText().toString().equals("")){
                //todo gestire il caso in cui il text è vuoto
            }else{
                Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                intent.putExtra("author",authorEditText.getText().toString());
                startActivity(intent);
            }
        });

        titleSearchButton.setOnClickListener(v -> {
            if(titleEditText.getText().toString().equals("")){
                //todo gestire il caso in cui il text è vuoto
            }else{
                Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                intent.putExtra("bookTitle",titleEditText.getText().toString());
                startActivity(intent);
            }
        });

        publisherSearchButton.setOnClickListener(v -> {
            if(publisherEditText.getText().toString().equals("")){
                //todo gestire il caso in cui il text è vuoto
            }else{
                Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                intent.putExtra("publisher",publisherEditText.getText().toString());
                startActivity(intent);
            }
        });

        ISBNSearchButton.setOnClickListener(v -> {
            if(ISBNEditText.getText().toString().equals("")){
                //todo gestire il caso in cui il text è vuoto
            }else{
                Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                intent.putExtra("ISBN",ISBNEditText.getText().toString());
                startActivity(intent);
            }
        });
    }
}
