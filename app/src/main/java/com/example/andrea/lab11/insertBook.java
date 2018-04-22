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

    //EdiTexts
    private EditText ISBNView;
    private EditText titleView;
    private EditText authorView;
    private EditText publisherView;
    private EditText editionView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();

        this.compatActivity = compatActivity;
        this.activity = this;

        setContentView(R.layout.activity_add_book_manual);

        Intent intent = getIntent();

        book = (BookInfo)intent.getSerializableExtra("book");
        if(book == null){
            book = new BookInfo(getApplicationContext());
        }
        myUser = new MyUser(getApplicationContext());
        SL = new spinnerListener(getApplicationContext(),book);

        ISBNView = findViewById(R.id.ISBNaddManual);
        ISBNView.setText(book.get_ISBN(), TextView.BufferType.NORMAL);

        titleView = findViewById(R.id.TitleAddManual);
        titleView.setText(book.getBookTitle(), TextView.BufferType.NORMAL);

        authorView = findViewById(R.id.AuthorAddManual);
        authorView.setText(book.getAuthor(), TextView.BufferType.NORMAL);

        publisherView = findViewById(R.id.PublisherAddManual);
        publisherView.setText(book.getPublisher(), TextView.BufferType.NORMAL);

        //TODO make list
        editionView = findViewById(R.id.EditionYearAddManual);
        editionView.setText(Integer.toString(book.getEditionYear()), TextView.BufferType.NORMAL);

        Spinner spinner = (Spinner) findViewById(R.id.conditions_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conditions_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(SL);

        GridView bookImageGrid = findViewById(R.id.addBookManualGrid);

        //TODO make a limit a images
        //TODO delete image
        myAdapter = new bookImageAdapter(this,book.getImageList());
        bookImageGrid.setAdapter(myAdapter);

        //Listens to when a component of the AdapterView gets pressed. In case it's the add button, it asks the user to choose a new photo, otherwise it zooms in the photo already saved.
        bookImageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == myAdapter.getCount()-1){
                    selectedImageUri = Utilities.requestImage(compatActivity, activity, CAMERA_REQUEST_CODE, PICK_IMAGE);

                }else{
                    view.setOnTouchListener(new touchPhoto());
                }
            }
        });

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

        //TODO some field cannot be empty
        book.set_ISBN(ISBNView.getText().toString());
        book.setAuthor(authorView.getText().toString());
        book.setBookTitle(titleView.getText().toString());
        book.setEditionYear(editionView.getText().toString());
        book.setOwner(myUser.getUserID());
        book.setPublisher(publisherView.getText().toString());
        book.loadBook();

        onBackPressed();
    }

}

