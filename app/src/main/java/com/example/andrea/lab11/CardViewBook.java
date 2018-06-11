package com.example.andrea.lab11;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.File;

public class CardViewBook extends RecyclerView.ViewHolder {

    private String deBugTag;
    private TextView title;
    private TextView author;
    private TextView ISBN;
    private TextView editionYear;
    private ImageView photo;
    private ImageButton deleteButton;
    private Context context;

    public CardViewBook(View view){
        super(view);

        deBugTag = this.getClass().getName();
        context = itemView.getContext();

        //get elements
        title = itemView.findViewById(R.id.titleResult);
        author = itemView.findViewById(R.id.authorResult);
        ISBN = itemView.findViewById(R.id.ISBNresult);
        editionYear = itemView.findViewById(R.id.yearResult);
        photo = itemView.findViewById(R.id.imageResult);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }

    public void bindData(String title, String author, String ISBN, String editionYear, String bookId, Boolean showProfile, Boolean deleteButtonRequested){

        this.title.setText(title);
        this.author.setText(author);
        this.ISBN.setText(ISBN);
        this.editionYear.setText(editionYear);
        this.photo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_image_black_24dp));

        //open Show Book if card is pressed
        itemView.setClickable(true);
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowBook.class);
            intent.putExtra("bookId",bookId);
            intent.putExtra("showProfile",showProfile);
            context.startActivity(intent);
        });

        //set deleteButton if request
        if(deleteButtonRequested){
            deleteButton.setOnClickListener(v->{

                //ask for confirmation
                new AlertDialog.Builder(context)
                        .setMessage(context.getString(R.string.removeBookMessage))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                                dbRef.child("books").child(bookId).removeValue();
                                dbRef.child("bookRequests").child(bookId).removeValue();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            });
            deleteButton.setVisibility(View.VISIBLE);
        }

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("bookImages/"+ bookId + "/0");

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context).load(uri).into(photo);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.e(deBugTag,e.getMessage());
            }
        });
    }
}
