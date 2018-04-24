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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private String deBugTag;
    private bookImageAdapter myAdapter;
    private boolean error;

    //EdiTexts
    private EditText ISBNView;
    private EditText titleView;
    private EditText authorView;
    private EditText publisherView;
    private EditText editionView;

    //gridview
    private GridView bookImageGrid;

    //loading spinner
    private ProgressBar spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializations
        deBugTag = this.getClass().getName();
        this.compatActivity = compatActivity;
        this.activity = this;
        error = false;

        //set fields
        setContentView(R.layout.activity_add_book_manual);

        spinner = findViewById(R.id.progressBarAddBook);
        spinner.setVisibility(View.GONE);

        //get Book from previous activity
        Intent intent = getIntent();
        book = (BookInfo)intent.getSerializableExtra("book");
        if(book == null){
            book = new BookInfo(getApplicationContext());
        }

        //get user
        myUser = new MyUser(getApplicationContext());

        //set focus listener
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /*
                if(hasFocus && error){
                    ISBNView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border));
                    titleView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border));
                    authorView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border));
                    publisherView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border));
                    editionView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border));
                    error = false;
                }*/
            }
        };

        //set ISBN
        ISBNView = findViewById(R.id.ISBNaddManual);
        ISBNView.setText(book.get_ISBN(), TextView.BufferType.NORMAL);
        ISBNView.setOnFocusChangeListener(focusListener);

        //set title
        titleView = findViewById(R.id.TitleAddManual);
        titleView.setText(book.getBookTitle(), TextView.BufferType.NORMAL);
        titleView.setOnFocusChangeListener(focusListener);

        //set author
        authorView = findViewById(R.id.AuthorAddManual);
        authorView.setText(book.getAuthor(), TextView.BufferType.NORMAL);
        authorView.setOnFocusChangeListener(focusListener);

        //set publisher
        publisherView = findViewById(R.id.PublisherAddManual);
        publisherView.setText(book.getPublisher(), TextView.BufferType.NORMAL);
        publisherView.setOnFocusChangeListener(focusListener);

        //TODO make list
        //set edition year
        editionView = findViewById(R.id.EditionYearAddManual);
        editionView.setText(Integer.toString(book.getEditionYear()), TextView.BufferType.NORMAL);
        editionView.setOnFocusChangeListener(focusListener);

        //set conditions
        SL = new spinnerListener(getApplicationContext(),book);
        Spinner spinner = (Spinner) findViewById(R.id.conditions_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conditions_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(SL);

        //set book photos
        bookImageGrid = findViewById(R.id.addBookManualGrid);
        myAdapter = new bookImageAdapter(this,book.getImageList(), compatActivity);
        bookImageGrid.setAdapter(myAdapter);


        //Listens to when a component of the AdapterView gets pressed. In case it's the add button, it asks the user to choose a new photo, otherwise it zooms in the photo already saved.
        bookImageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ciao", "p: "+ position);
                if(position == myAdapter.getCount()-1){
                    selectedImageUri = Utilities.requestImage(compatActivity, activity, CAMERA_REQUEST_CODE, PICK_IMAGE);

                }else{

                }
            }
        });

        //set back button toolbar
        ImageButton backButton = findViewById(R.id.imageButton);
        backButton.setOnClickListener((parent) -> {
            onBackPressed();
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {

                Bitmap modifiedBitmap = Utilities.pictureActivityResult(activity, data, selectedImageUri);
                myAdapter.addImage(modifiedBitmap);
                bookImageGrid.requestFocus();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void uploadBook(View view) {

        if(!canUpload())
            return;

        book.set_ISBN(ISBNView.getText().toString());
        book.setAuthor(authorView.getText().toString());
        book.setBookTitle(titleView.getText().toString());
        book.setEditionYear(editionView.getText().toString());
        book.setOwner(myUser.getUserID());
        book.setPublisher(publisherView.getText().toString());

        Utilities.loading_and_blur_background(view, spinner);
        book.loadBook();
        Utilities.show_background(view, spinner);
        onBackPressed();
    }

    private boolean canUpload(){

        //check if ISBN is empty and in case put red background
        if(ISBNView.getText().length()==0){
            ISBNView.setError(getString(R.string.required));

            //ISBNView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border_red));
            error = true;
        }

        //check if title is empty and in case put red background
        if(titleView.getText().length()==0){
            titleView.setError(getString(R.string.required));

            //titleView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border_red));
            error = true;
        }

        //check if author is empty and in case put red background
        if(authorView.getText().length()==0){
            authorView.setError(getString(R.string.required));

            //authorView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border_red));
            error = true;
        }

        //check if publisher is empty and in case put red background
        if(publisherView.getText().length()==0){
            publisherView.setError(getString(R.string.required));
            //publisherView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border_red));
            error = true;
        }

        //check if edition year is empty and in case put red background
        if(editionView.getText().length()==0){
            editionView.setError(getString(R.string.required));
            //editionView.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_border_red));
            error = true;
        }

        if(error)
            Toast.makeText(this,getString(R.string.all_fields_mandatory),Toast.LENGTH_SHORT).show();

        return !error;
    }

}

