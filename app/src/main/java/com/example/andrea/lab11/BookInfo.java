package com.example.andrea.lab11;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.String;
import static android.content.Context.MODE_PRIVATE;

public class BookInfo{



    private String ISBN = null;
    private String BookTitle = null;
    private String Author = null;
    private String Publisher = null;
    private String Owner = null;
    private int EditionYear;
    private String Conditions = null;
    private String[] bookPhotos = null;
    private int photosQty;
    private Context applicationContext = null;

    public BookInfo(Context applicationContext)
    {


       bookPhotos = new String[6];
       photosQty = 0;
       this.applicationContext = applicationContext;
       File file;
       for(int i = 0; i < 6; i++)
        {

            file = new File(applicationContext.getFilesDir(),Utilities.BooksImgsPath[i]);
            if(file.exists())
            {
                bookPhotos[i] = file.getPath();

            }
        }


    }

    public int getPhotosQty(){return this.photosQty;}

    public String get_ISBN() {
        return ISBN;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public String getAuthor() {
        return Author;
    }

    public String getPublisher() {
        return Publisher;
    }

    public int getEditionYear() {
        return EditionYear;
    }

    public String getConditions() {
        return Conditions;
    }

    public String[] getBookPhotos() {
        return bookPhotos;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        this.Owner = owner;
    }

    public void set_ISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setBookTitle(String BookTitle)
    {
        this.BookTitle = BookTitle;
    }

    public void setAuthor(String Author)
    {
        this.Author = Author;
    }

    public void setPublisher(String Publisher)
    {
        this.Publisher = Publisher;
    }

    public void setEditionYear(String EditionYear)
    {
        this.EditionYear = Integer.parseInt(EditionYear);
    }

    public void setConditions(String Conditions)
    {
        this.Conditions = Conditions;
    }

    public void setPhoto(Bitmap Photo)
    {
        if(photosQty < 6)
        {
          OutputStream out = null;
          try
          {
              File file = new File(applicationContext.getFilesDir(), Utilities.BooksImgsPath[photosQty]);
              out = new FileOutputStream(file);

              Photo.compress(Bitmap.CompressFormat.JPEG,60,out);
              photosQty++;
          }
          catch(FileNotFoundException ex)
          {
              Toast.makeText(applicationContext, R.string.toast_MyUser_setImage,Toast.LENGTH_LONG).show();
          }
          finally
          {
              try
              {
                  out.close();
              }
              catch(NullPointerException NPex)
              {

              }
              catch(IOException IOex)
              {

              }

          }
        }
        else
            Toast.makeText(applicationContext,"Maximum number of photos reached(6)",Toast.LENGTH_LONG).show();
    }
}
