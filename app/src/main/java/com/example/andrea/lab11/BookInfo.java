package com.example.andrea.lab11;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.String;
import java.sql.Timestamp;
import java.util.LinkedList;

import static android.content.Context.MODE_PRIVATE;

public class BookInfo implements Serializable {


    private String bookID = null;
    private String ISBN = null;
    private String bookTitle = null;
    private String author = null;
    private String publisher = null;
    private String owner = null;
    private String editionYear;
    private String conditions = null;
    private LinkedList<Bitmap> imageList;
    private Context applicationContext = null;

    public BookInfo(Context applicationContext)
    {
        imageList = new LinkedList<>();
    }

    public String get_ISBN() {
        return ISBN;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getEditionYear() { return editionYear; }

    public String getConditions() {
        return conditions;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void set_ISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setBookTitle(String bookTitle)
    {
        this.bookTitle = bookTitle;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public void setEditionYear(String editionYear) {this.editionYear = editionYear; }

    public void setConditions(String conditions)
    {
        this.conditions = conditions;
    }

    public LinkedList<Bitmap> getImageList()
    {
        return imageList;
    }

    public void loadBook(){

        Timestamp t = new Timestamp(System.currentTimeMillis());
        Long timestamp = t.getTime()/1000;

        bookID = ISBN + owner + timestamp;

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("books");
        dbRef.child(bookID).child("ISBN").setValue(ISBN);
        dbRef.child(bookID).child("bookTitle").setValue(bookTitle);
        dbRef.child(bookID).child("author").setValue(author);
        dbRef.child(bookID).child("publisher").setValue(publisher);
        dbRef.child(bookID).child("owner").setValue(owner);
        dbRef.child(bookID).child("editionYear").setValue(editionYear);
        dbRef.child(bookID).child("conditions").setValue(conditions);

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("bookImages/"+bookID);
        int count = 0;

        //load book images
        for(Bitmap image : imageList) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            ref.child(count + "").putBytes(data);
            count ++;
        }
    }

}
