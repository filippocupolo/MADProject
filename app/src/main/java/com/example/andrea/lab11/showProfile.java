package com.example.andrea.lab11;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class showProfile extends AppCompatActivity {

    private MyUser myUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //+++++++++++++set fields+++++++++++++
        setContentView(R.layout.show_profile);

        //set editButton
        ImageView editButton = findViewById(R.id.imageViewEditButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        editProfile.class
                );
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //create MyUser
        myUser = new MyUser(getApplicationContext());

        //TODO change value if myUser's elements are null (to delete in final release of app)
        String name;
        String surname;
        String email;
        String biography;
        if(myUser.getName() ==null){
            name = "Name";
        }else{
            name = myUser.getName();
        }
        if(myUser.getSurname()==null){
            surname = "Surname";
        }else{
            surname = myUser.getSurname();
        }
        if(myUser.getEmail()==null){
                email = "Email";
            }else{
            email = myUser.getEmail();
        }if(myUser.getBiography()==null){
            biography = "Biography";
        }else{
            biography = myUser.getBiography();
        }

        //set name and surname
        TextView nameSurnameView = findViewById(R.id.nameSurnameShow);
        nameSurnameView.setText(getString(R.string.nameSurname,name,surname));

        //set email
        TextView emailView = findViewById(R.id.emailShow);
        emailView.setText(email);

        //set biography
        TextView biographyView = findViewById(R.id.showProfileBio);
        biographyView.setText(biography);

        //set image
        ImageView profileView = findViewById(R.id.imageViewShow);
        if( myUser.getImage() == null){

            //if there is not a profile image load the default one
            profileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_40dp));
        }else{

            //load the profile image
            Bitmap bitmap = BitmapFactory.decodeFile(myUser.getImage());
            Drawable bd = new BitmapDrawable(getResources(), bitmap);
            profileView.setImageDrawable(bd);
        }
    }
}
