package com.example.andrea.lab11;

import android.content.Context;
import android.net.Uri;
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
    private int imageNumber;
    private Uri[] ImgIDs;

    public bookImageAdapter(Context context)
    {
        appContext = context;
        ImgIDs = new Uri[6];
        for(int i = 0; i <6 ; i++)
        {
            ImgIDs[i] = Uri.parse(Utilities.BooksImgsPath[i]);
        }
        imageNumber = ImgIDs.length;
    }

    @Override
    public int getCount() {
        return imageNumber;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if(convertView == null)
        {
            imageView = new ImageView(appContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setVisibility(View.VISIBLE);
            if(position < 6)
               imageView.setImageResource(R.drawable.ic_add_button_24dp);
        }
        else
        {
            imageView = (ImageView) convertView;
        }



        imageView.setImageURI(ImgIDs[position]);

        return imageView;
    }



}
