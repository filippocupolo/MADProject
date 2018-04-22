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
<<<<<<< HEAD
<<<<<<< HEAD
=======
import android.widget.Button;
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15
=======
import android.widget.Button;
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15
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
    Uri[] bookImgUri = new Uri[]{Uri.parse("android.resource://com.example.andrea.lab11/drawable/ic_add_button_24dp")};
    private Uri selectedImageUri;
    private static final int CAMERA_REQUEST_CODE = 666;
    private static final int PICK_IMAGE = 999;
    ActivityCompat compatActivity;
    Activity activity;
    private spinnerListener SL;
<<<<<<< HEAD
<<<<<<< HEAD

=======
    private String deBugTag;
    private bookImageAdapter myAdapter;
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15

=======
    private String deBugTag;
    private bookImageAdapter myAdapter;

>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15
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

<<<<<<< HEAD
<<<<<<< HEAD
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

        EditText EditionView = findViewById(R.id.EditionYearAddManual);
        if(book.getEditionYear() != 0)
            EditionView.setText(Integer.toString(book.getEditionYear()), TextView.BufferType.NORMAL);
        else
            EditionView.setHint("Edition Year");
        EditionView.addTextChangedListener(textWatcher);



        GridView bookImageGrid = findViewById(R.id.addBookManualGrid);

        bookImageGrid.setAdapter(new bookImageAdapter(this,bookImgUri));
        //Listens to when a component of the AdapterView gets pressed. In case it's the add button, it asks the user to choose a new photo, otherwise it zooms in the photo already saved.
        bookImageGrid.setOnItemClickListener((parent, v, position, id) -> {
            if(position == book.getPhotosQty())
                selectedImageUri = Utilities.requestImage(compatActivity, activity, CAMERA_REQUEST_CODE, PICK_IMAGE);
            else
                v.setOnTouchListener(new touchPhoto());
=======
        Intent intent = getIntent();

        book = (BookInfo)intent.getSerializableExtra("book");
        if(book == null){
            book = new BookInfo(getApplicationContext());
        }
        myUser = new MyUser(getApplicationContext());
        SL = new spinnerListener(getApplicationContext(),book);

        ISBNView = findViewById(R.id.ISBNaddManual);
        ISBNView.setText(book.get_ISBN(), TextView.BufferType.NORMAL);
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15

        titleView = findViewById(R.id.TitleAddManual);
        titleView.setText(book.getBookTitle(), TextView.BufferType.NORMAL);

<<<<<<< HEAD
        ImageButton backButton = findViewById(R.id.imageButton);
        backButton.setOnClickListener((parent) -> {
            onBackPressed();
        });

    }
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
            ImageView bookPic = new ImageView(this);
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
                try {
=======
        authorView = findViewById(R.id.AuthorAddManual);
        authorView.setText(book.getAuthor(), TextView.BufferType.NORMAL);
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15
=======
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
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15

        publisherView = findViewById(R.id.PublisherAddManual);
        publisherView.setText(book.getPublisher(), TextView.BufferType.NORMAL);

        //TODO make list
        editionView = findViewById(R.id.EditionYearAddManual);
        editionView.setText(Integer.toString(book.getEditionYear()), TextView.BufferType.NORMAL);
<<<<<<< HEAD

        Spinner spinner = (Spinner) findViewById(R.id.conditions_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conditions_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(SL);

=======

        Spinner spinner = (Spinner) findViewById(R.id.conditions_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conditions_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(SL);

>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15
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

<<<<<<< HEAD
<<<<<<< HEAD
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
                case R.id.EditionYearAddManual:                            //TODO ADD CORRESPONDENT VIEW IN XML
                    book.setEditionYear(s.toString());
                    break;
                case R.id.PublisherAddManual:
                    book.setPublisher(s.toString());
                    break;
=======
=======
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15
            Uri uri = Utilities.cameraRequestPermissionsResult(activity, grantResults, CAMERA_REQUEST_CODE, PICK_IMAGE);

            if(uri != null){
                selectedImageUri = uri;
<<<<<<< HEAD
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15
=======
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15
            }

        }
    }
<<<<<<< HEAD

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


=======

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
>>>>>>> 7ac6f863517a60ba0a259e459b431bee73b9bd15

}

