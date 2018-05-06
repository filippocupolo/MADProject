package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    }

    public void bindData(String title, String author, String ISBN, String editionYear, String bookId){

        this.title.setText(title);
        this.author.setText(author);
        this.ISBN.setText(ISBN);
        this.editionYear.setText(editionYear);

        //open Show Book if card is pressed
        itemView.setClickable(true);
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowBook.class);
            intent.putExtra("bookId",bookId);
            context.startActivity(intent);
        });

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("bookImages/"+ bookId + "/0");

        //todo ref.getBytes lancia degli errori cercare di capire cosa sono
        //todo ridurre la dimensione del file ma per fare questo bisogna comprimere tutte le immagini e forse è meglio sostituite bitmap con drawable per migliorare le prestazioni
        ref.getBytes(10 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {
                photo.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //todo gestire
                Log.e(deBugTag,e.getMessage());
            }
        });
    }


}
