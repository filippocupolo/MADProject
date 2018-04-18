package com.example.andrea.lab11;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
//import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import com.example.andrea.lab11.touchPhoto;

public class insertBook extends AppCompatActivity{

    MyUser myUser;
    BookInfo book;
    private Uri selectedImageUri;
    private static final int CAMERA_REQUEST_CODE = 666;
    private static final int PICK_IMAGE = 999;
    ActivityCompat compatActivity;
    Activity activity;
    private spinnerListener SL;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.compatActivity = compatActivity;
        this.activity = this;

        setContentView(R.layout.activity_add_book_manual);

        myUser = new MyUser(getApplicationContext());
        book = new BookInfo(getApplicationContext());
        SL = new spinnerListener(getApplicationContext(),book);
        EditText ISBNView = findViewById(R.id.ISBNaddManual);
        if(book.get_ISBN() != null)
            ISBNView.setText(book.get_ISBN(), TextView.BufferType.NORMAL);
        else
            ISBNView.setHint("ISBN");
        ISBNView.addTextChangedListener(textWatcher);

        EditText TitleView = findViewById(R.id.TitleAddManual);
        if(book.getBookTitle() != null)
            TitleView.setText(book.getBookTitle(), TextView.BufferType.NORMAL);
        else
            TitleView.setHint("Title");
        TitleView.addTextChangedListener(textWatcher);

        EditText AuthorView = findViewById(R.id.AuthorAddManual);
        if(book.getAuthor() != null)
            AuthorView.setText(book.getAuthor(), TextView.BufferType.NORMAL);
        else
            AuthorView.setHint("Author");
        AuthorView.addTextChangedListener(textWatcher);

        EditText PublisherView = findViewById(R.id.PublisherAddManual);
        if(book.getPublisher() != null)
            PublisherView.setText(book.getPublisher(), TextView.BufferType.NORMAL);
        else
            PublisherView.setHint("Publisher");
        PublisherView.addTextChangedListener(textWatcher);

        Spinner spinner = (Spinner) findViewById(R.id.conditions_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conditions_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(SL);

        /*EditText OwnerView = findViewById(R.id.);                                 //TODO ADD CORRESPONDENT VIEW IN XML FILE
        if(book.getOwner() != null)
            OwnerView.setText(book.getOwner(), TextView.BufferType.NORMAL);
        else
            OwnerView.setText("Owner", TextView.BufferType.NORMAL);
        OwnerView.addTextChangedListener(textWatcher);*/

        EditText EditionView = findViewById(R.id.EditionYearAddManual);
        if(book.getEditionYear() != 0)
            EditionView.setText(Integer.toString(book.getEditionYear()), TextView.BufferType.NORMAL);
        else
            EditionView.setHint("Edition Year");
        EditionView.addTextChangedListener(textWatcher);



        GridView bookImageGrid = findViewById(R.id.addBookManualGrid);

        bookImageGrid.setAdapter(new bookImageAdapter(this, new Drawable[]{getResources().getDrawable(R.drawable.ic_add_button_24dp)}));
        //Listens to when a component of the AdapterView gets pressed. In case it's the add button, it asks the user to choose a new photo, otherwise it zooms in the photo already saved.
        bookImageGrid.setOnItemClickListener((parent, v, position, id) -> {
            if(position == book.getPhotosQty())
                selectedImageUri = Utilities.requestImage(compatActivity, activity, CAMERA_REQUEST_CODE, PICK_IMAGE);
            else
                v.setOnTouchListener(new touchPhoto());

        });

    }
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
            ImageView bookPic = new ImageView(this);
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
                try {

                    Bitmap modifiedBitmap = Utilities.pictureActivityResult(activity, data, selectedImageUri);
                    bookPic.setImageBitmap(modifiedBitmap);
                    book.setPhoto(modifiedBitmap);

                } catch (IOException ex) {
                    Log.e(this.getClass().getName(), ex.toString());
                    Toast.makeText(this, R.string.toast_EditProfile_onActivityResult, Toast.LENGTH_LONG).show();
                }
            }
        }

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
                case R.id.ISBNaddManual:
                    book.set_ISBN(s.toString());
                    break;
                case R.id.TitleAddManual:
                    book.setBookTitle(s.toString());
                    break;
                case R.id.AuthorAddManual:
                    book.setAuthor(s.toString());
                    break;
                /*case R.id.BookOwner:
                    book.setOwner(s.toString());
                    break;*/
                /*case R.id.EditionYear:                            //TODO ADD CORRESPONDENT VIEW IN XML
                    book.setEditionYear(s.toString());
                    break;*/
                case R.id.PublisherAddManual:
                    book.setPublisher(s.toString());
                    break;
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };

}

