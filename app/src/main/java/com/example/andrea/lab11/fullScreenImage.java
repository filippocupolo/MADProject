package com.example.andrea.lab11;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class fullScreenImage extends AppCompatActivity {

    String deBugTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();
        Drawable drw = null;

        setContentView(R.layout.activity_full_screen_image);
        ImageView image = findViewById(R.id.full_screen_image);
        String path = getIntent().getStringExtra("path");

        if(path!=null)
            drw = Drawable.createFromPath(path);
        else
            onBackPressed();

        image.setImageDrawable(drw);
    }
}
