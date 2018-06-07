package com.example.andrea.lab11;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookRequest extends RecyclerView.ViewHolder {

    private String userId;
    private int status;
    private String myUserId;
    private String bookId;
    private TextView nameSurname;
    private ImageButton acceptRequest;
    private ImageButton refuseRequest;

    public BookRequest(View view){
        super(view);
        nameSurname = itemView.findViewById(R.id.user_name_surname);
        acceptRequest = itemView.findViewById(R.id.acceptRequest);
        refuseRequest = itemView.findViewById(R.id.refuseRequest);
    }

    public void bindData(String userId, String bookId, String myUserId, String nameSurname, int statusPar, String borrower) {
        this.userId = userId;
        this.nameSurname.setText(nameSurname);
        this.status = statusPar;

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        acceptRequest.setOnClickListener( v ->{
            if(status==0){

                status = 1;

                dbRef.child("books").child(bookId).child("status").setValue(status);
                dbRef.child("books").child(bookId).child("borrower").setValue(userId);
                dbRef.child("books").child(bookId).child("borrowerName").setValue(nameSurname);

                dbRef.child("bookRequests").child(bookId).child(userId).removeValue();

                //set bookAccepted
                dbRef.child("bookAccepted").child(userId).child("userId").setValue(myUserId);

                //send message box to ask if someone want to comment
                //Utilities.showDialogForComment(itemView.getContext(),"LENDER_COMMENT",userId);

            }else{
                //todo fai stringa
                Toast.makeText(itemView.getContext(),"libro già in prestito",Toast.LENGTH_SHORT).show();
            }
        });

        refuseRequest.setOnClickListener( v ->{
            dbRef.child("bookRequests").child(bookId).child(userId).removeValue();
        });

        this.nameSurname.setOnClickListener( v ->{
            Intent showProfileIntent = new Intent(itemView.getContext(),showProfile.class);
            showProfileIntent.putExtra("userId",userId);
            itemView.getContext().startActivity(showProfileIntent);
        });
    }

}
