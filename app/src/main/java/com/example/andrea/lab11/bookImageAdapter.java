package com.example.andrea.lab11;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import com.example.andrea.lab11.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class bookImageAdapter extends BaseAdapter{

    private Context appContext;
    private LinkedList<Bitmap> images;
    private Uri drawableUri;
    private String deBugTag;
    private Boolean allImages;
    private Uri selectedImageUri;
    final private int MAX_IMAGES = 4;
    private static final int CAMERA_REQUEST_CODE = 666;
    private static final int PICK_IMAGE = 999;
    private ActivityCompat activityCompat;

    public bookImageAdapter(Context context, LinkedList<Bitmap> images)
    {
        allImages = false;
        appContext = context;
        this.images = images;
        this.activityCompat = activityCompat;
        drawableUri = Uri.parse("android.resource://com.example.andrea.lab11/drawable/ic_add_button_24dp");
        deBugTag = this.getClass().getName();
    }

    @Override
    public int getCount() {

        int imagesSize = images.size();
        if(imagesSize == MAX_IMAGES){
            allImages = true;
            return imagesSize;
        }else{
            allImages = false;
            return images.size() + 1;
        }

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            LayoutInflater li=(LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=li.inflate(R.layout.book_image_adapter_layout, parent,false);

        }

        ImageButton bookPhoto = convertView.findViewById(R.id.bookPhoto);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        if(position==getCount()-1 && !allImages){
            deleteButton.setVisibility(View.GONE);
            bookPhoto.setImageURI(drawableUri);
        }else{
            deleteButton.setVisibility(View.VISIBLE);
            bookPhoto.setImageBitmap(images.get(position));
        }

        bookPhoto.setFocusable(false);
        bookPhoto.setFocusableInTouchMode(false);
        bookPhoto.setClickable(false);

        bookPhoto.setScaleType(ImageButton.ScaleType.FIT_XY);

        deleteButton.setImageResource(R.drawable.ic_delete_black_24dp);
        deleteButton.setOnClickListener(e->{
            removeImage(position);
        });

        return convertView;
    }

    public void addImage(Bitmap bitmap){
        //add element
        images.push(bitmap);
        notifyDataSetChanged();

    }

    public void removeImage(int position){
        //remove element
        images.remove(position);
        notifyDataSetChanged();
    }

}