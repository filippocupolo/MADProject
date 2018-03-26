package com.example.andrea.lab11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;


public class showProfile extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile);
        Toolbar spTB = (Toolbar) findViewById(R.id.show_profile_toolbar);
        setSupportActionBar(spTB);
    }
}
