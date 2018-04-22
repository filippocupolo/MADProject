package com.example.andrea.lab11;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import com.example.andrea.lab11.Utilities;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class bookImageAdapter extends BaseAdapter{

    private Context appContext;
    private LinkedList<Bitmap> images;
    private Uri drawableUri;
    private String deBugTag;

    public bookImageAdapter(Context context,LinkedList<Bitmap> images)
    {
        appContext = context;
        this.images = images;
        drawableUri = Uri.parse("android.resource://com.example.andrea.lab11/drawable/ic_add_button_24dp");
        deBugTag = this.getClass().getName();
    }

    @Override
    public int getCount() {
        return images.size() + 1;
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
            convertView=li.inflate(R.layout.activity_add_book_manual, null);

        }

        ImageView imageView = convertView.findViewById(R.id.imageButton);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(180,180));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setVisibility(View.VISIBLE);

        if(position==getCount()-1){
            imageView.setImageURI(drawableUri);
        }else{
            imageView.setImageBitmap(images.get(position));
        }

        imageView.setFocusable(false);
        imageView.setFocusableInTouchMode(false);
        imageView.setClickable(false);

        return imageView;
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
