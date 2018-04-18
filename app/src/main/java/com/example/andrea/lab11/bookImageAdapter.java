package com.example.andrea.lab11;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import com.example.andrea.lab11.Utilities;
import java.net.URI;

public class bookImageAdapter extends BaseAdapter{

    private Context appContext;
    private Drawable[] images;

    public bookImageAdapter(Context context, Drawable[] images)
    {
        appContext = context;
        if (images==null){
            this.images = new Drawable[]{};
        }else{
            this.images = images;
        }
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
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
            convertView=li.inflate(R.layout.back_button_toolbar, null);
            /*
            imageView = new ImageView(appContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setVisibility(View.VISIBLE);
            if(position < 6)
               imageView.setImageResource(R.drawable.ic_add_button_24dp);
              */
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageButton);

        imageView.setImageDrawable(images[position]);

        return imageView;
    }



}
