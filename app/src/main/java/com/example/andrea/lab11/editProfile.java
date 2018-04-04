package com.example.andrea.lab11;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class editProfile extends AppCompatActivity {

    private MyUser myUser;
    private static final int MY_CAMERA_REQUEST_CODE = 432;
    private static final int PICK_IMAGE = 123;

    //Views
    ImageView profileView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creare MyUser
        myUser = new MyUser(getApplicationContext());

        //+++++++++++++set fields//+++++++++++++
        setContentView(R.layout.edit_profile);

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
        emailView.addTextChangedListener(textWatcher);

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
        changeImageButton.setClickable(true);
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });

        //set showProfileIcon
        ImageView showProfileIcon = findViewById(R.id.showProfileIcon);
        showProfileIcon.setClickable(true);
        showProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        showProfile.class
                );
                startActivity(intent);
                finish();
            }
        });

        //set image
        profileView = findViewById(R.id.imageViewEdit);
        if( myUser.getImage() == null){

            //if there is not a profile image load the default one
            profileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_24dp));
        }else{

            //load the profile image
            Bitmap bitmap = BitmapFactory.decodeFile(myUser.getImage());
            Log.d(this.getClass().getName(),myUser.getImage());
            profileView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setImage(){

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( this, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }else{

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePictureIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        }
    }

    //Get the photo from camera and put it as profileView
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();
                Bitmap bitmap;

                if(extras==null){

                    //the user chose the Photos App
                    Uri selectedImageUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                }else{

                    //the user chose the Camera App
                    bitmap = (Bitmap) extras.get("data");
                }

                //make the bitmap squared
                Bitmap modifiedBitmap;
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                if(width > height){
                    modifiedBitmap = Bitmap.createBitmap(bitmap,(width-height)/2,0,height,height);
                }else{
                    modifiedBitmap = Bitmap.createBitmap(bitmap,0,(height-width)/2,width,width);
                }

                //set bitmap on imageView and save it on myUser
                profileView.setImageBitmap(modifiedBitmap);
                myUser.setImage(modifiedBitmap);
            }

        }catch (IOException ex){
            Log.e(this.getClass().getName(),ex.toString());
        }
    }

    //Look if the request for the camera are positive or not. If yes send the intent to the camera.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePictureIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);

            } else {

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {});

                startActivityForResult(chooserIntent, PICK_IMAGE);
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
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
                    myUser.setEmail(s.toString());
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
