package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShowBook extends AppCompatActivity {

    private String deBugTag;
    private Context context;
    private TextView title;
    private TextView author;
    private TextView editionYear;
    private TextView publisher;
    private TextView ISBN;
    private TextView owner;
    private TextView cityOwner;
    private BookInfo book;
    private ConstraintLayout container;
    private ImageButton goToProfileButton;
    private ImageButton send_message_button;
    private CopyOnWriteArrayList<Drawable> imagesList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();
        imagesList = new CopyOnWriteArrayList<>();
        context = getApplicationContext();

        //set layout
        setContentView(R.layout.book_result_show_details);

        //get elements
        title = findViewById(R.id.bookTitle);
        author = findViewById(R.id.bookAuthor);
        editionYear = findViewById(R.id.bookEdition);
        publisher = findViewById(R.id.bookPublisher);
        ISBN = findViewById(R.id.bookISBN);
        owner = findViewById(R.id.bookOwner);
        cityOwner = findViewById(R.id.ownerCity);
        container = findViewById(R.id.constrTop);
        goToProfileButton = findViewById(R.id.gotoProfileButton);
        send_message_button = findViewById(R.id.send_message_button);
        GridView gridView = findViewById(R.id.imageBook);

        //set toolbar
        TextView toolbarTitle = findViewById(R.id.back_toolbar_text);
        findViewById(R.id.imageButton).setOnClickListener(v->onBackPressed());
        toolbarTitle.setText(R.string.book_info);

        //hide container until information are got
        container.setVisibility(View.GONE);

        //set gridView adapter
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return imagesList.size();
            }

            @Override
            public Object getItem(int position) {
                return imagesList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null)
                {
                    LayoutInflater li=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView=li.inflate(R.layout.book_image_adapter_layout, parent,false);

                }

                ImageButton bookPhoto = convertView.findViewById(R.id.bookPhoto);
                ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
                deleteButton.setVisibility(View.GONE);
                bookPhoto.setClickable(false);
                bookPhoto.setImageDrawable(imagesList.get(position));
                bookPhoto.setScaleType(ImageButton.ScaleType.FIT_XY);
                return convertView;
            }
        };
        gridView.setAdapter(adapter);

        //get bookId and showProfile from intent
        String bookId = getIntent().getStringExtra("bookId");
        Boolean showProfile = getIntent().getBooleanExtra("showProfile",true);

        //set database Ref
        DatabaseReference dbRef =  FirebaseDatabase.getInstance().getReference();

        //make FireBase request for book
        Query bookQuery = dbRef.child("books").orderByKey().equalTo(bookId);
        bookQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get book
                book = ResultsList.parseDataSnapshotBook(dataSnapshot.getChildren().iterator().next());
                if(book == null){
                    errorMethod(R.string.network_problem);
                }

                //make FireBase request for book Owner
                Query userQuery = dbRef.child("users").orderByKey().equalTo(book.getOwner());
                userQuery.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot == null){
                            errorMethod(R.string.not_existing_user);
                        }
                        if(!dataSnapshot.exists()){
                            errorMethod(R.string.not_existing_user);
                        }

                        dataSnapshot = dataSnapshot.getChildren().iterator().next();

                        String name_surname = null;
                        String city = null;

                        //get owner
                        for(DataSnapshot child : dataSnapshot.getChildren()){

                            switch (child.getKey()){
                                case "name":
                                    name_surname = (String) child.getValue();
                                    break;
                                case "surname":
                                    name_surname = name_surname + " ";
                                    name_surname = name_surname + (String) child.getValue();
                                    break;
                                case "city":
                                    city = (String) child.getValue();
                                    break;
                            }
                        }

                        //set layout with the results
                        container.setVisibility(View.VISIBLE);
                        title.setText(book.getBookTitle());
                        author.setText(book.getAuthor());
                        editionYear.setText(book.getEditionYear());
                        publisher.setText(book.getPublisher());
                        ISBN.setText(book.get_ISBN());
                        owner.setText(name_surname);
                        cityOwner.setText(city);

                        String owner = name_surname;
                        if(!book.getOwner().equals(new MyUser(context).getUserID())){
                            send_message_button.setOnClickListener(v->{
                                Intent chatIntent = new Intent(getApplicationContext(),PersonalChat.class);
                                chatIntent.putExtra("userId",book.getOwner());
                                Log.d(deBugTag,book.getOwner());
                                chatIntent.putExtra("userName",owner);
                                startActivity(chatIntent);
                            });
                        }else{
                            send_message_button.setVisibility(View.GONE);
                        }
                        if(showProfile){
                            goToProfileButton.setOnClickListener(v->{
                                Intent showProfileIntent = new Intent(getApplicationContext(),showProfile.class);
                                showProfileIntent.putExtra("userId",book.getOwner());
                                startActivity(showProfileIntent);
                            });
                        }else{
                            goToProfileButton.setVisibility(View.GONE);
                        }

                        for (int i = 0; i<4; i++){

                            //request for Images
                            StorageReference ref = FirebaseStorage.getInstance().getReference().child("bookImages/"+ bookId + "/" + i);

                            //todo ref.getBytes lancia degli errori cercare di capire cosa sono
                            //todo ridurre la dimensione del file ma per fare questo bisogna comprimere tutte le immagini e forse è meglio sostituite bitmap con drawable per migliorare le prestazioni
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).getBytes(2 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {

                                        @Override
                                        public void onSuccess(byte[] bytes) {

                                            imagesList.add(new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
                                            adapter.notifyDataSetChanged();
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Log.e(deBugTag,e.getMessage());

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.e(deBugTag,e.getMessage());
                                }
                            });
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        errorMethod(R.string.network_problem);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorMethod(R.string.network_problem);
            }
        });

    }

    private void errorMethod(int txt){

        Toast.makeText(context,txt,Toast.LENGTH_SHORT).show();
        Log.e(deBugTag,"dataSnapshot non esiste");
        onBackPressed();
    }
}
