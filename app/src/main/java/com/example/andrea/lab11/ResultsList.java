package com.example.andrea.lab11;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ResultsList extends AppCompatActivity {

    //todo put firebase rules to "auth != null" after debug work is finished

    private String deBugTag;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recycler_view_search_list);

        deBugTag = this.getClass().getName();

        //set toolbar
        ImageButton backArrow = findViewById(R.id.backButton);
        backArrow.setOnClickListener((v) -> {onBackPressed();});
        ImageButton mapButton = findViewById(R.id.mapButton);

        //get elements
        list = findViewById(R.id.rv);

        Query query = FirebaseDatabase.getInstance().getReference().child("books");

        FirebaseRecyclerOptions<BookInfo> options = new FirebaseRecyclerOptions.Builder<BookInfo>()
            .setQuery(query, new SnapshotParser<BookInfo>() {
                @NonNull
                @Override
                public BookInfo parseSnapshot(@NonNull DataSnapshot dataSnapshot) {
                    BookInfo book = new BookInfo();
                    if(dataSnapshot.exists()){

                        book.setBookID(dataSnapshot.getKey());

                        for(DataSnapshot s : dataSnapshot.getChildren()){

                            switch (s.getKey()){
                                case "ISBN":
                                    book.set_ISBN((String)s.getValue());
                                    break;
                                case "author":
                                    book.setAuthor((String)s.getValue());
                                    break;
                                case "bookTitle":
                                    book.setBookTitle((String)s.getValue());
                                    break;
                                case "editionYear":
                                    book.setEditionYear(s.getValue().toString());
                                    break;
                            }

                        }
                    }
                    return book;
                }
            })
            .setLifecycleOwner(this)
            .build();



        adapter = new FirebaseRecyclerAdapter<BookInfo, CardViewBook>(options) {

            @Override
            public CardViewBook onCreateViewHolder(ViewGroup parent, int viewType) {

                Log.d(deBugTag,"onCreateViewHolder");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_search_results_list, parent, false);

                return new CardViewBook(view);
            }

            @Override
            protected void onBindViewHolder(CardViewBook holder, int position, BookInfo model) {
                Log.d(deBugTag,"onBindViewHolder");
                holder.bindData(model.getBookTitle(),model.getAuthor(),model.get_ISBN(), model.getEditionYear(), model.getBookID());
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                //todo gestire errore
                Log.d(deBugTag,"Error: " + error.getMessage());
            }
        };

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }



}
