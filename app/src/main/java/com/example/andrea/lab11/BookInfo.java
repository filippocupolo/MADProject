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

import com.google.android.gms.maps.UiSettings;
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
    private int status = 0;
    private String borrower = null;
    private String borrowerName = null;
    private LinkedList<Bitmap> imageList;

    public BookInfo()
    {
        imageList = new LinkedList<>();
    }

    public BookInfo(BookInfo another) {
        this.bookID = another.bookID;
        this.ISBN = another.ISBN;
        this.bookTitle = another.bookTitle;
        this.author = another.author;
        this.publisher = another.publisher;
        this.owner = another.owner;
        this.editionYear = another.editionYear;
        this.conditions = another.conditions;
        this.imageList = new LinkedList<>(another.imageList);
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

    public String getBookID() { return bookID; }

    public String getConditions() {
        return conditions;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getOwner() {
        return owner;
    }

    public int getStatus(){return status;}

    public String getBorrower() {
        return borrower;
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

    public void setBookID(String bookID) {this.bookID = bookID; }

    public void setConditions(String conditions)
    {
        this.conditions = conditions;
    }

    public void setStatus(int status){this.status = status;}

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public void setBorrowerName(String borrowerName){this.borrowerName = borrowerName;}

    public LinkedList<Bitmap> getImageList()
    {
        return imageList;
    }

    public Bitmap getFirstPhoto(){return imageList.getFirst();}

    public void loadBook(){

        Timestamp t = new Timestamp(System.currentTimeMillis());
        Long timestamp = t.getTime()/1000;

        bookID = ISBN + owner + timestamp;

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("books");

        dbRef.child(bookID).child("ISBN").setValue(ISBN.replaceAll("\\s+",""));
        dbRef.child(bookID).child("bookTitleSearch").setValue(Utilities.setStringForResearch(bookTitle));
        dbRef.child(bookID).child("authorSearch").setValue(Utilities.setStringForResearch(author));
        dbRef.child(bookID).child("publisherSearch").setValue(Utilities.setStringForResearch(publisher));
        dbRef.child(bookID).child("bookTitle").setValue(bookTitle);
        dbRef.child(bookID).child("author").setValue(author);
        dbRef.child(bookID).child("publisher").setValue(publisher);
        dbRef.child(bookID).child("owner").setValue(owner);
        dbRef.child(bookID).child("editionYear").setValue(editionYear);
        dbRef.child(bookID).child("conditions").setValue(conditions);
        dbRef.child(bookID).child("status").setValue(status);
        dbRef.child(bookID).child("borrower").setValue(borrower);
        dbRef.child(bookID).child("borrowerName").setValue(borrowerName);

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("bookImages/"+bookID);
        int count = 0;

        //load book images
        for(Bitmap image : imageList) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();
            ref.child(count + "").putBytes(data);
            count ++;
        }
    }

}
