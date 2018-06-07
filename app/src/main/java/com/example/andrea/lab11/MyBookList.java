package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyBookList extends Fragment {

    private String deBugTag;
    private Context context;
    private RecyclerView list;
    private ImageButton addBookButton;
    private TextView noItemMessage;
    private ProgressBar spinner;
    private FirebaseRecyclerAdapter<BookInfo, CardViewBook> adapter;

    public MyBookList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialization
        context = getActivity().getApplicationContext();
        deBugTag = this.getClass().getName();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_book_list, container, false);

        //get elements
        list = view.findViewById(R.id.my_books_rv);
        addBookButton = view.findViewById(R.id.imageViewEditButton);
        noItemMessage = view.findViewById(R.id.my_books_emptyListMessage);
        spinner = view.findViewById(R.id.progressBarBooks);

        Utilities.loading_and_blur_background(view, spinner);

        //set query
        Query query = FirebaseDatabase.getInstance().getReference().child("books").orderByChild("owner").equalTo(new MyUser(context).getUserID());

        //get and populate list
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

        adapter = new FirebaseRecyclerAdapter<BookInfo, CardViewBook>(options) {

            @NonNull
            @Override
            public CardViewBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_view_search_results_list, parent, false);

                return new CardViewBook(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CardViewBook holder, int position, @NonNull BookInfo model) {

                holder.bindData(model.getBookTitle(),model.getAuthor(),model.get_ISBN(), model.getEditionYear(), model.getBookID(),true, true );
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Utilities.show_background(view, spinner);
                noItemMessage.setVisibility(
                        getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE
                );
            }

        };

        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);

        //set addBookButton
        addBookButton.setOnClickListener( v -> addBook());

        return view;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void addBook(){
        Intent intent = new Intent(context,AddBookAutomatic.class);
        startActivity(intent);
    }
}
