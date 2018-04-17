package com.example.andrea.lab11;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static android.graphics.drawable.Drawable.createFromPath;

public class editProfile extends AppCompatActivity {

    private MyUser myUser;
    private static final int CAMERA_REQUEST_CODE = 432;
    private static final int PICK_IMAGE = 123;
    private String email;
    private Uri selectedImageUri;
    private Activity activity;
    private ActivityCompat activityCompat;

    //Views
    ImageView profileView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        activityCompat = this.activityCompat;

        //create MyUser
        myUser = new MyUser(getApplicationContext());

        //+++++++++++++set fields//+++++++++++++
        setContentView(R.layout.edit_profile);

        //logout button listener
        findViewById(R.id.sign_out_button).setOnClickListener((View v) ->{
            Utilities.signOut();
            Intent intent = new Intent(
                    getApplicationContext(),
                    login.class
            );
            startActivity(intent);
        });

        //set name
        EditText nameView = findViewById(R.id.nameEdit);
        nameView.setText(myUser.getName(), TextView.BufferType.NORMAL);
        nameView.addTextChangedListener(textWatcher);

        //set surname
        EditText surnameView = findViewById(R.id.surnameEdit);
        surnameView.setText(myUser.getSurname(), TextView.BufferType.NORMAL);
        surnameView.addTextChangedListener(textWatcher);

        //set email
        EditText emailView = findViewById(R.id.emailEdit);
        emailView.setText(myUser.getEmail(), TextView.BufferType.NORMAL);
        email = myUser.getEmail();
        if(Utilities.ValidateEmailAddress(myUser.getEmail())){

            //if email is valid set green check
            Drawable isValidMail = getResources().getDrawable(R.drawable.ic_check_green_24dp);
            isValidMail.setBounds(0, 0, isValidMail.getIntrinsicWidth(), isValidMail.getIntrinsicHeight());
            emailView.setCompoundDrawables(null, null, isValidMail, null);

        }else if(myUser.getEmail() != null && !Utilities.ValidateEmailAddress(myUser.getEmail())){

            //if email is NOT valid set red cross
            Drawable isValidMail = getResources().getDrawable(R.drawable.ic_clear_red_24dp);
            isValidMail.setBounds(0, 0, isValidMail.getIntrinsicWidth(), isValidMail.getIntrinsicHeight());
            emailView.setCompoundDrawables(null, null, isValidMail, null);
        }
        emailView.addTextChangedListener(textWatcher);
        emailView.setOnFocusChangeListener((view, hasFocus) -> {

            //when is not more focused check if the mail is valid or not and put a check or a cross
            if (!hasFocus) {

                Drawable isValidMail;

                if(Utilities.ValidateEmailAddress(email)){
                    isValidMail = getResources().getDrawable(R.drawable.ic_check_green_24dp);
                }else{
                    //put red cross
                    isValidMail = getResources().getDrawable(R.drawable.ic_clear_red_24dp);
                    Toast.makeText(getApplicationContext(), R.string.toast_EditProfile_onFocusChange, Toast.LENGTH_LONG).show();
                }

                isValidMail.setBounds(0, 0, isValidMail.getIntrinsicWidth(), isValidMail.getIntrinsicHeight());
                EditText et = (EditText)view;
                et.setCompoundDrawables(null, null, isValidMail, null);
            }
        });

        //set biography
        EditText biographyView = findViewById(R.id.bioEdit);
        biographyView.setText(myUser.getBiography(), TextView.BufferType.NORMAL);
        biographyView.addTextChangedListener(textWatcher);


        //set city
        EditText cityView = findViewById(R.id.cityEdit);
        cityView.setText(myUser.getCity(), TextView.BufferType.NORMAL);
        cityView.addTextChangedListener(textWatcher);

        //set changeImageButton
        ImageView changeImageButton = findViewById(R.id.imageViewEditButton);
        changeImageButton.setOnClickListener(v -> selectedImageUri = Utilities.requestImage(activityCompat,activity,CAMERA_REQUEST_CODE,PICK_IMAGE));

        //set showProfileIcon
        ImageView showProfileIcon = findViewById(R.id.showProfileIcon);
        showProfileIcon.setClickable(true);
        showProfileIcon.setOnClickListener(v -> {
            String caller = getIntent().getStringExtra("caller");

            if(caller!="showProfile"){
                Intent intent = new Intent(
                        getApplicationContext(),
                        showProfile.class
                );
                intent.putExtra("caller", "editProfile");
                if(Utilities.ValidateEmailAddress(email)){
                    myUser.setEmail(email);
                }
                myUser.commit();
                startActivity(intent);
            }
            else {
                onBackPressed();
            }
        });

        profileView = findViewById(R.id.imageViewEdit);
        if( myUser.getImage() == null){

            //if there is not a profile image load the default one
            profileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_40dp));
        }else{

            Drawable bd = createFromPath(myUser.getImage());
            profileView.setImageDrawable(bd);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(Utilities.ValidateEmailAddress(email)){
            myUser.setEmail(email);
        }
        myUser.commit();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myUser.commit();
    }

    //Get the photo from camera and put it as profileView
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {

                Bitmap modifiedBitmap = Utilities.pictureActivityResult(activity,data,selectedImageUri);

                //set bitmap on imageView and save it on myUser
                profileView.setImageBitmap(modifiedBitmap);
                myUser.setImage(modifiedBitmap);

            }catch (IOException ex){
                Log.e(this.getClass().getName(),ex.toString());
                Toast.makeText(this,R.string.toast_EditProfile_onActivityResult,Toast.LENGTH_LONG).show();
            }
        }
    }

    //Look if the request for the camera are positive or not. If yes send the intent to the camera.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {

            Uri uri = Utilities.cameraRequestPermissionsResult(activity, grantResults, CAMERA_REQUEST_CODE, PICK_IMAGE);

            if(uri != null){
                selectedImageUri = uri;
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {

            if(getCurrentFocus()==null){
                Log.d(this.getClass().getName(),"getCurrentFocus() è null");
                return;
            }

            switch (getCurrentFocus().getId()){
                case R.id.nameEdit:
                    myUser.setName(s.toString());
                    break;
                case R.id.surnameEdit:
                    myUser.setSurname(s.toString());
                    break;
                case R.id.emailEdit:
                    email = s.toString();
                    break;
                case R.id.bioEdit:
                    myUser.setBiography(s.toString());
                    break;
                case R.id.cityEdit:
                    myUser.setCity(s.toString());
                    break;
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };
}
