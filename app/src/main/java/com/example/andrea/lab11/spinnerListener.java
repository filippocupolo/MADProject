package com.example.andrea.lab11;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

public class spinnerListener extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Context appContext;
    private BookInfo book;

    public spinnerListener(Context context, BookInfo book) {
        appContext = context;
        this.book = book;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        book.setConditions(parent.getItemAtPosition(pos).toString());

    }

    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }
}
