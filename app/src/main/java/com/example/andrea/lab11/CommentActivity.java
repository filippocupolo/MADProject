package com.example.andrea.lab11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    private float ratingCounter;
    private String debugTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ratingCounter = -1;
        debugTag = this.getClass().getName();

        //set toolbar
        TextView toolbarTitle = findViewById(R.id.back_toolbar_text);
        findViewById(R.id.imageButton).setOnClickListener(v -> onBackPressed());
        //todo fai stringa
        toolbarTitle.setText("Scrivi commento");

        //get userId from intent
        String userId = getIntent().getStringExtra("userId");
        if(userId == null){
            Log.e(debugTag, "userId cannot be null");
            onBackPressed();
        }

        //get elements
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText comment = findViewById(R.id.commentEditText);
        Button uploadComment = findViewById(R.id.uploadCommentButton);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
               ratingCounter = rating;
            }
        });

        uploadComment.setOnClickListener(v->{

            //todo make stringa toast
            if(ratingCounter == -1){
                Toast.makeText(this, "devi prima dare un rating", Toast.LENGTH_SHORT).show();
                return;
            }

            String text = comment.getText().toString();

            if(text.trim().length() == 0)
                text = null;

            Date date = new Date();
            MyUser myUser = new MyUser(this);

            CommentModel commentModel = new CommentModel(myUser.getUserID(),myUser.getName() +" "+myUser.getSurname(),ratingCounter,text,date.getYear(),date.getMonth(),date.getDay());

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("commentsDB").child(userId);
            dbRef.child("comments").push().setValue(commentModel);
            dbRef.child("can_comment").child(myUser.getUserID()).removeValue();

            onBackPressed();

        });

    }
}
