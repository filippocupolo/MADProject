package com.example.andrea.lab11;

//todo controllare che per tutti i toast venga chiamato il .show()

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;

import static android.graphics.drawable.Drawable.createFromPath;

public class editProfile extends AppCompatActivity {

    private MyUser myUser;
    private static final int CAMERA_REQUEST_CODE = 432;
    private static final int PICK_IMAGE = 123;
    private String deBugTag;
    private Uri selectedImageUri;
    private Activity activity;
    private ActivityCompat activityCompat;
    private String previousActivity;
    private boolean error;
    private boolean doubleBackToExitPressedOnce;

    //Views
    private EditText nameView;
    private ImageView profileView;
    private EditText surnameView;
    private EditText cityView;
    private EditText biographyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        activityCompat = this.activityCompat;
        deBugTag = this.getClass().getName();
        Log.d(deBugTag,deBugTag);
        error = false;
        doubleBackToExitPressedOnce = false;

        previousActivity = getIntent().getStringExtra("caller");

        //create MyUser
        myUser = new MyUser(getApplicationContext());

        //+++++++++++++set fields//+++++++++++++
        setContentView(R.layout.edit_profile);

        //set focus listener
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && error){
                    nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border));
                    surnameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border));
                    cityView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border));
                    error = false;
                }
            }
        };

        //set name
        nameView = findViewById(R.id.nameEdit);
        nameView.setText(myUser.getName(), TextView.BufferType.NORMAL);
        nameView.setOnFocusChangeListener(focusListener);

        //set surname
        surnameView = findViewById(R.id.surnameEdit);
        surnameView.setText(myUser.getSurname(), TextView.BufferType.NORMAL);
        surnameView.setOnFocusChangeListener(focusListener);

        //set email
        TextView emailView = findViewById(R.id.emailEdit);
        emailView.setText(myUser.getEmail(), TextView.BufferType.NORMAL);

        //set biography
        biographyView = findViewById(R.id.bioEdit);
        biographyView.setText(myUser.getBiography(), TextView.BufferType.NORMAL);
        biographyView.setOnFocusChangeListener(focusListener);

        //set city
        cityView = findViewById(R.id.cityEdit);
        cityView.setText(myUser.getCity(), TextView.BufferType.NORMAL);
        cityView.setOnFocusChangeListener(focusListener);

        TextView t = findViewById(R.id.back_toolbar_text);
        t.setText(R.string.edit_profile);

        //set changeImageButton
        ImageView changeImageButton = findViewById(R.id.imageViewEditButton);
        changeImageButton.setOnClickListener(v -> selectedImageUri = Utilities.requestImage(activityCompat,activity,CAMERA_REQUEST_CODE,PICK_IMAGE));

        //set backButton
        ImageButton backButton = findViewById(R.id.imageButton);
        backButton.setOnClickListener((parent) -> {
            onBackPressed();
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

        if(previousActivity.equals("login")){

            if (doubleBackToExitPressedOnce) {

                //exit application
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }

            doubleBackToExitPressedOnce = true;

            Toast.makeText(this,  getString(R.string.click_to_exit), Toast.LENGTH_SHORT).show();

            //after 2 seconds reset doubleBackToExitPressedOnce to false
            new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
        }
        else {
            if(saveUser()){
                super.onBackPressed();
                finish();
            }
        }


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

    //function to call before leave activity if true ok else error occurred and the activity cannot be leaved
    private boolean saveUser(){

        if(canExit()){

            myUser.setName(nameView.getText().toString());
            myUser.setSurname(surnameView.getText().toString());
            myUser.setBiography(biographyView.getText().toString());
            myUser.setCity(cityView.getText().toString());

            myUser.commit();

            return true;
        }
        return false;
    }

    private boolean canExit(){

        //check if name is empty and in case put red background
        if(nameView.getText().length()==0){
            nameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border_red));
            error = true;
        }

        //check if surname is empty and in case put red background
        if(surnameView.getText().length()==0){
            surnameView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border_red));
            error = true;
        }

        //check if city is empty and in case put red background
        if(cityView.getText().length()==0){
            cityView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border_red));
            error = true;
        }

        if(error)
            Toast.makeText(this, getString(R.string.mandatory_fields),Toast.LENGTH_SHORT).show();

        return !error;
    }

    public void saveButtonClick(View view){

        if(saveUser()){

            String caller = getIntent().getStringExtra("caller");

            if(caller!="showProfile"){
                Intent intent = new Intent(
                        getApplicationContext(),
                        showProfile.class
                );
                intent.putExtra("caller", "editProfile");
                startActivity(intent);
                finish();
            }
            else {
                onBackPressed();
            }
        }
    }

}
