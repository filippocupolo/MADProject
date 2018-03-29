package com.example.andrea.lab11;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        //creare MyUser
        myUser = new MyUser(getApplicationContext());

        //+++++++++++++set fields+++++++++++++
        setContentView(R.layout.show_profile);

        //set name and surname
        TextView nameSurnameView = findViewById(R.id.nameSurnameShow);
        nameSurnameView.setText(myUser.getName() + " " + myUser.getSurname());

        //set city
        TextView cityView = findViewById(R.id.cityShow);
        cityView.setText(myUser.getCity());

        //set image
        ImageView profileView = findViewById(R.id.imageViewShow);
        if( myUser.getImage() == null){

            //if there is not a profile image load the default one
            Log.d(this.getClass().getName(),"loading default image");
            profileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
        }else{

            //load the profile image
            Bitmap bitmap = BitmapFactory.decodeFile(myUser.getImage());
            profileView.setImageBitmap(bitmap);
        }

        //set editButton
        ImageView editButton = findViewById(R.id.editProfileIcon);
        editButton.setClickable(true);
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
}
