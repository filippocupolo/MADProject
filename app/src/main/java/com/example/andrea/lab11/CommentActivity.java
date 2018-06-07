package com.example.andrea.lab11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Calendar;
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
        toolbarTitle.setText(R.string.comment);

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
                if(rating<0.5f)
                    ratingBar.setRating(0.5f);
               ratingCounter = rating;
            }
        });

        uploadComment.setOnClickListener(v->{

            if(ratingCounter == -1){
                Toast.makeText(this, R.string.give_rate, Toast.LENGTH_SHORT).show();
                return;
            }

            String text = comment.getText().toString();

            if(text.trim().length() == 0)
                text = null;

            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            MyUser myUser = new MyUser(this);

            CommentModel commentModel = new CommentModel(myUser.getUserID(),myUser.getName() +" "+myUser.getSurname(),ratingCounter,text,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("commentsDB").child(userId);
            dbRef.child("comments").push().setValue(commentModel);
            dbRef.child("can_comment").child(myUser.getUserID()).removeValue();
            FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("avgVotes").runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if(mutableData.getValue() == null){
                        mutableData.child("numVotes").setValue(1);
                        mutableData.child("vote").setValue(ratingCounter);
                    }else{
                        int numOfVotes = Integer.parseInt(mutableData.child("numVotes").getValue().toString());
                        float avgVote = Float.parseFloat(mutableData.child("vote").getValue().toString());
                        avgVote = ((avgVote *numOfVotes)+ratingCounter)/(numOfVotes+1);
                        mutableData.child("numVotes").setValue(numOfVotes+1);
                        mutableData.child("vote").setValue(avgVote);
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if(databaseError!=null)
                        Log.e(debugTag,databaseError.getMessage());
                    else
                        Log.d(debugTag,dataSnapshot.toString());
                    //todo gestire
                }
            });


            onBackPressed();

        });

    }
}
