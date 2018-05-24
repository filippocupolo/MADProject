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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyBookList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyBookList} factory method to
 * create an instance of this fragment.
 */
public class MyBookList extends Fragment {
    /*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    */

    private String deBugTag;
    private Context context;
    private RecyclerView list;
    private ImageButton addBookButton;
    private TextView noItemMessage;

    public MyBookList() {
        // Required empty public constructor
    }

    /*
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBookList.
     */
    /* TODO: Rename and change types and number of parameters
    public static MyBookList newInstance(String param1, String param2) {
        MyBookList fragment = new MyBookList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */

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

                holder.bindData(model.getBookTitle(),model.getAuthor(),model.get_ISBN(), model.getEditionYear(), model.getBookID(),true);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
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

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    /*

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void addBook(){
        Intent intent = new Intent(context,AddBookAutomatic.class);
        startActivity(intent);
    }
}
