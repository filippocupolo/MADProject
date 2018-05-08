package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.graphics.drawable.Drawable.createFromPath;


public class showProfile extends AppCompatActivity{

    private Context context;
    private String deBugTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();

        context = getApplicationContext();

        //+++++++++++++set fields+++++++++++++
        setContentView(R.layout.show_profile);

        String userId = getIntent().getStringExtra("userId");

        //get elements
        TextView nameSurnameView = findViewById(R.id.nameSurnameShow);
        TextView emailView = findViewById(R.id.emailShow);
        TextView biographyView = findViewById(R.id.showProfileBio);
        ImageView profileView = findViewById(R.id.imageViewShow);

        //Get information from FireBase
        FirebaseDatabase.getInstance().getReference().child("users").orderByKey().equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                //todo put city

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
                        FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).getBytes(10 * 1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {

                            @Override
                            public void onSuccess(byte[] bytes) {

                                profileView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                //todo gestire se il file non esiste non fare nulla
                                Log.e(deBugTag,e.getMessage());

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //todo gestire se il file non esiste non fare nulla
                        Log.e(deBugTag,e.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
