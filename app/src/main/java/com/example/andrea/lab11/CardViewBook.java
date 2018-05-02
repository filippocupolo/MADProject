package com.example.andrea.lab11;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CardViewBook extends RecyclerView.ViewHolder {

    private String deBugTag;
    private TextView title;
    private TextView author;
    private TextView ISBN;
    private TextView editionYear;

    public CardViewBook(View view){
        super(view);

        deBugTag = this.getClass().getName();

        //get elements
        title = itemView.findViewById(R.id.titleResult);
        author = itemView.findViewById(R.id.authorResult);
        ISBN = itemView.findViewById(R.id.ISBNresult);
        editionYear = itemView.findViewById(R.id.yearResult);
    }

    public void bindData(String title, String author, String ISBN, String editionYear){
        this.title.setText(title);
        this.author.setText(author);
        this.ISBN.setText(ISBN);
        this.editionYear.setText(editionYear);
    }
}
