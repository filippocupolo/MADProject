package com.example.andrea.lab11;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

public class CardViewComment extends RecyclerView.ViewHolder {

    private String deBugTag;
    private TextView commentTime;
    private TextView commentAuthor;
    private RatingBar commentStars;
    private TextView commentText;
    private Context context;

    public CardViewComment(View v){
        super(v);

        deBugTag = this.getClass().getName();
        context = itemView.getContext();

        //get elements
        commentAuthor = v.findViewById(R.id.comment_user);
        commentStars = v.findViewById(R.id.comment_stars);
        commentText = v.findViewById(R.id.comment_text);
        commentTime = v.findViewById(R.id.comment_time);
    }

    public void bindData(String author, Integer stars, String text, String time){

        this.commentTime.setText(time);
        this.commentText.setText(text);
        this.commentStars.setNumStars(stars);
        this.commentAuthor.setText(author);

    }
}
