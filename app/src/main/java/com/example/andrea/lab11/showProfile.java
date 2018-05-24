package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import static android.graphics.drawable.Drawable.createFromPath;


public class showProfile extends AppCompatActivity{

    private Context context;
    private String deBugTag;
    private AppCompatActivity activity;
    private FirebaseRecyclerAdapter<BookInfo, CardViewBook> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();
        activity = this;
        context = getApplicationContext();

        //+++++++++++++set fields+++++++++++++
        setContentView(R.layout.show_profile);

        String userId = getIntent().getStringExtra("userId");

        //get elements
        TextView nameSurnameView = findViewById(R.id.nameSurnameShow);
        TextView emailView = findViewById(R.id.emailShow);
        TextView biographyView = findViewById(R.id.showProfileBio);
        TextView cityView = findViewById(R.id.showProfileCity);
        TextView noBookMessage = findViewById(R.id.noBookMessage);
        ImageView profileView = findViewById(R.id.imageViewShow);
        RecyclerView list = findViewById(R.id.published_books_rv);

        //set toolbar
        TextView toolbarTitle = findViewById(R.id.back_toolbar_text);
        findViewById(R.id.imageButton).setOnClickListener(v -> onBackPressed());
        toolbarTitle.setText(R.string.user_info);

        //set FireBaseReference
        DatabaseReference fireBaseRef = FirebaseDatabase.getInstance().getReference();

        //Get information from FireBase
        fireBaseRef.child("users").orderByKey().equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot = dataSnapshot.getChildren().iterator().next();

                String name = null;
                String surname = null;
                String email = null;
                String biography = null;
                String city = null;

                for(DataSnapshot child : dataSnapshot.getChildren()){

                    switch (child.getKey()){
                        case "name":
                            name = (String)child.getValue();
                            break;
                        case "surname":
                            surname = (String)child.getValue();
                            break;
                        case "email":
                            email = (String)child.getValue();
                            break;
                        case "biography":
                            biography = child.getValue().toString();
                            break;
                        case "city":
                            city = (String) child.getValue();
                            break;
                    }
                }
                nameSurnameView.setText(getString(R.string.nameSurname,name,surname));
                emailView.setText(email);
                biographyView.setText(biography);
                cityView.setText(city);

                //set default image
                profileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_40dp));

                //set container visible
                findViewById(R.id.show_form_wrapper).setVisibility(View.VISIBLE);

                //check if the profile image is available and load it
                StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImages/"+ userId);

                //todo ref.getBytes lancia degli errori cercare di capire cosa sono
                //todo ridurre la dimensione del file ma per fare questo bisogna comprimere tutte le immagini e forse è meglio sostituite bitmap con drawable per migliorare le prestazioni
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //create File
                        File file = new File(getFilesDir(), "profile.jpg");

                        FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                profileView.setImageDrawable(Drawable.createFromPath(file.getPath()));
                                profileView.setOnClickListener(v -> {
                                    Intent fullImageIntent = new Intent(
                                            getApplicationContext(),
                                            fullScreenImage.class
                                    );
                                    fullImageIntent.putExtra("path", file.getPath());
                                    startActivity(fullImageIntent);

                                });
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                networkProblem();
            }
        });


        //fill up the published book list
        Log.d(deBugTag,"userId: "+ userId);
        Query publishedBookQuery = fireBaseRef.child("books").orderByChild("owner").equalTo(userId);

        FirebaseRecyclerOptions<BookInfo> options = new FirebaseRecyclerOptions.Builder<BookInfo>()
                .setQuery(publishedBookQuery, new SnapshotParser<BookInfo>() {
                    @NonNull
                    @Override
                    public BookInfo parseSnapshot(@NonNull DataSnapshot snapshot) {

                        if(snapshot==null){
                            Log.d(deBugTag,"snapshot null");
                        }
                        if(!snapshot.exists()){
                            Log.d(deBugTag,"snapshot non esiste");
                        }else{
                            Log.d(deBugTag,"snapshot: " + snapshot.toString());
                        }

                        return ResultsList.parseDataSnapshotBook(snapshot);

                    }
                })
                .setLifecycleOwner(this)
                .build();

        adapter = new FirebaseRecyclerAdapter<BookInfo, CardViewBook>(options) {

            @NonNull
            @Override
            public CardViewBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_search_results_list, parent, false);

                return new CardViewBook(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CardViewBook holder, int position, @NonNull BookInfo model) {

                holder.bindData(model.getBookTitle(),model.getAuthor(),model.get_ISBN(), model.getEditionYear(), model.getBookID(),false);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount()==0)
                    noBookMessage.setVisibility(View.VISIBLE);
                else
                    noBookMessage.setVisibility(View.GONE);
                Log.d(deBugTag,"ondatachanged");
            }
        };
        list.setItemAnimator(new DefaultItemAnimator());
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    private void networkProblem(){
        Toast.makeText(getApplicationContext(),R.string.network_problem,Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
