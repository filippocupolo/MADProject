package com.example.andrea.lab11;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
        Uri uri =null;
        if(path == null){
            uri = Uri.parse(getIntent().getStringExtra("uri"));
        }

        if(path!=null){
            drw = Drawable.createFromPath(path);
            image.setImageDrawable(drw);
        }
        else if(uri!=null){
            Glide.with(this).load(uri).into(image);
        }
        else
            onBackPressed();


    }
}
