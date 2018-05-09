package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

public class SearchBook extends AppCompatActivity {

    private String deBugTag;
    private ImageButton authorSearchButton;
    private ImageButton titleSearchButton;
    private ImageButton publisherSearchButton;
    private ImageButton ISBNSearchButton;
    private EditText authorEditText;
    private EditText titleEditText;
    private EditText publisherEditText;
    private EditText ISBNEditText;
    private float x1,x2;
    static final int MIN_DISTANCE = 100;
    private String previousActivity;
    private Context context;
    private TabLayout tabs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        deBugTag = this.getClass().getName();

        setContentView(R.layout.search_book);

        //set toolbar
        //ImageButton backArrow = findViewById(R.id.imageButton);
        //backArrow.setOnClickListener((v) -> {onBackPressed();});
        //TextView toolbarTitle = findViewById(R.id.back_toolbar_text);
        //toolbarTitle.setText(R.string.search_book);

        //get elements
        authorSearchButton = findViewById(R.id.authorSearchButton);
        titleSearchButton = findViewById(R.id.titleSearchButton);
        publisherSearchButton = findViewById(R.id.publisherSearchButton);
        ISBNSearchButton = findViewById(R.id.ISBNSearchButton);
        authorEditText = findViewById(R.id.searchAuthor);
        titleEditText = findViewById(R.id.searchTitle);
        publisherEditText = findViewById(R.id.searchPublisher);
        ISBNEditText = findViewById(R.id.searchISBN);

        authorSearchButton.setOnClickListener(v -> {
            if(authorEditText.getText().toString().equals("")){
                //field cannot be empty
                authorEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                intent.putExtra("author",authorEditText.getText().toString());
                startActivity(intent);
            }
        });

        titleSearchButton.setOnClickListener(v -> {
            if(titleEditText.getText().toString().equals("")){
                //field cannot be empty
                titleEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                intent.putExtra("bookTitle",titleEditText.getText().toString());
                startActivity(intent);
            }
        });

        publisherSearchButton.setOnClickListener(v -> {
            if(publisherEditText.getText().toString().equals("")){
                //field cannot be empty
                publisherEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                intent.putExtra("publisher",publisherEditText.getText().toString());
                startActivity(intent);
            }
        });

        ISBNSearchButton.setOnClickListener(v -> {
            if(ISBNEditText.getText().toString().equals("")){
                //field cannot be empty
                ISBNEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                intent.putExtra("ISBN",ISBNEditText.getText().toString());
                startActivity(intent);
            }
        });

        //attach the ontouchlistener to the entire view
        //View myView = findViewById(R.id.scrollV);
        //myView.setOnTouchListener(touchListener);

        previousActivity = getIntent().getStringExtra("caller");

        //tab listener
        tabs = findViewById(R.id.tabLayout);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case 0:
                        Log.d(deBugTag, "ok");
                        Utilities.goToMyBooks(getApplicationContext(), previousActivity,
                                "SearchBook", SearchBook.this);
                        break;
                    case 1:
                        Log.d(deBugTag, "ok2");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Log.d(deBugTag, "edit-unselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(deBugTag, "edit-reselected" + tab.getText());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        tabs.getTabAt(1).select();
    }

    /*
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    float deltaX = x1 - x2;
                    if (-deltaX > MIN_DISTANCE)
                    {
                        Utilities.goToMyBooks(getApplicationContext(), previousActivity,
                                "SearchBook", SearchBook.this);
                    }
                    else
                    {
                        // consider as something else - a screen tap for example
                    }
                    break;
            }
            return true;
        }
    };*/

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.general_menu, popup.getMenu());

        popup.show();

        //click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_logout:
                        Utilities.signOut(context);
                        return true;
                    case R.id.menu_edit_profile:
                        //Log.d("popup", "i:" + getIntent().getStringExtra("caller") + " c:"+this.getClass()+ "a: "+getApplicationContext());
                        Utilities.goToEditProfile(getApplicationContext(), previousActivity,
                                "SearchBook", SearchBook.this);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
