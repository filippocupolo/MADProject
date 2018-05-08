package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class myBooks extends AppCompatActivity{

    private String previousActivity;
    private float x1,x2;
    private Context context;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        //set layout
        setContentView(R.layout.my_books);

        //set query
        Query query = FirebaseDatabase.getInstance().getReference().child("books").orderByChild("owner").equalTo(new MyUser(context).getUserID());

        //get and populate list
        RecyclerView list = findViewById(R.id.my_books_rv);
        FirebaseRecyclerOptions<BookInfo> options = new FirebaseRecyclerOptions.Builder<BookInfo>()
                .setQuery(query, new SnapshotParser<BookInfo>() {
                    @NonNull
                    @Override
                    public BookInfo parseSnapshot(@NonNull DataSnapshot snapshot) {

                        return ResultsList.parseDataSnapshotBook(snapshot);

                    }
                })
                .setLifecycleOwner(this)
                .build();
        FirebaseRecyclerAdapter<BookInfo, CardViewBook> adapter = new FirebaseRecyclerAdapter<BookInfo, CardViewBook>(options) {

            @NonNull
            @Override
            public CardViewBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_search_results_list, parent, false);

                return new CardViewBook(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CardViewBook holder, int position, @NonNull BookInfo model) {

                holder.bindData(model.getBookTitle(),model.getAuthor(),model.get_ISBN(), model.getEditionYear(), model.getBookID());
            }
        };
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);


        //attach the ontouchlistener to the entire view
        View myView = findViewById(R.id.my_books_swipe);
        myView.setOnTouchListener(touchListener);

        previousActivity = getIntent().getStringExtra("caller");

        //tab listener
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.getTabAt(0).select();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case 0:

                        break;
                    case 1:
                        Utilities.goToSearchBook(getApplicationContext(), previousActivity,
                                "myBooks", myBooks.this);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("tab", "edit-unselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("tab", "edit-reselected" + tab.getText());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.getTabAt(0).select();
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("swipe", "ok");

            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    float deltaX = x2 - x1;
                    if (-deltaX > MIN_DISTANCE)
                    {
                        Utilities.goToSearchBook(getApplicationContext(), previousActivity,
                                "myBooks", myBooks.this);
                    }
                    else
                    {
                        // consider something else
                    }
                    break;
            }
            return true;
        }
    };

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
                                "myBooks", myBooks.this);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void addBook(View v){
        Utilities.goToAddBook(getApplicationContext(), previousActivity,
                "myBooks", myBooks.this);
    }
}
