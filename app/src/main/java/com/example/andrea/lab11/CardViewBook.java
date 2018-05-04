package com.example.andrea.lab11;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CardViewBook extends RecyclerView.ViewHolder {

    private String deBugTag;
    private TextView title;
    private TextView author;
    private TextView ISBN;
    private TextView editionYear;
    private ImageView photo;

    public CardViewBook(View view){
        super(view);

        deBugTag = this.getClass().getName();

        //get elements
        title = itemView.findViewById(R.id.titleResult);
        author = itemView.findViewById(R.id.authorResult);
        ISBN = itemView.findViewById(R.id.ISBNresult);
        editionYear = itemView.findViewById(R.id.yearResult);
        photo = itemView.findViewById(R.id.imageResult);
    }

    public void bindData(String title, String author, String ISBN, String editionYear, String bookId){

        this.title.setText(title);
        this.author.setText(author);
        this.ISBN.setText(ISBN);
        this.editionYear.setText(editionYear);

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("bookImages/"+ bookId + "/0");

        Log.d(deBugTag,"bookImages/"+ bookId + "/0");

        ref.getBytes(4 * 1024 * 1024).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()){
                    byte[] bitmapData = task.getResult();
                    photo.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length)));
                }else{
                    //todo gestisci quando non ci sono foto
                }
            }
        });
    }


}
