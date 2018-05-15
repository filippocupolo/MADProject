package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchBookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchBookFragment} factory method to
 * create an instance of this fragment.
 */
public class SearchBookFragment extends Fragment {

    private String deBugTag;
    private ImageButton authorSearchButton;
    private ImageButton titleSearchButton;
    private ImageButton publisherSearchButton;
    private ImageButton ISBNSearchButton;
    private EditText authorEditText;
    private EditText titleEditText;
    private EditText publisherEditText;
    private EditText ISBNEditText;
    private Context context;

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
    public SearchBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment SearchBookFragment.
     */
    /*
    // TODO: Rename and change types and number of parameters
    public static SearchBookFragment newInstance(String param1, String param2) {
        SearchBookFragment fragment = new SearchBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialization
        deBugTag = this.getClass().getName();
        context = getActivity().getApplicationContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_book, container, false);
        authorSearchButton = view.findViewById(R.id.authorSearchButton);
        titleSearchButton = view.findViewById(R.id.titleSearchButton);
        publisherSearchButton = view.findViewById(R.id.publisherSearchButton);
        ISBNSearchButton = view.findViewById(R.id.ISBNSearchButton);
        authorEditText = view.findViewById(R.id.searchAuthor);
        titleEditText = view.findViewById(R.id.searchTitle);
        publisherEditText = view.findViewById(R.id.searchPublisher);
        ISBNEditText = view.findViewById(R.id.searchISBN);

        authorSearchButton.setOnClickListener(v -> {
            if(authorEditText.getText().toString().equals("")){
                //field cannot be empty
                authorEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(context, ResultsList.class);
                intent.putExtra("author",authorEditText.getText().toString());
                startActivity(intent);
            }
        });

        titleSearchButton.setOnClickListener(v -> {
            if(titleEditText.getText().toString().equals("")){
                //field cannot be empty
                titleEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(context,ResultsList.class);
                intent.putExtra("bookTitle",titleEditText.getText().toString());
                startActivity(intent);
            }
        });

        publisherSearchButton.setOnClickListener(v -> {
            if(publisherEditText.getText().toString().equals("")){
                //field cannot be empty
                publisherEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(context,ResultsList.class);
                intent.putExtra("publisher",publisherEditText.getText().toString());
                startActivity(intent);
            }
        });

        ISBNSearchButton.setOnClickListener(v -> {
            if(ISBNEditText.getText().toString().equals("")){
                //field cannot be empty
                ISBNEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(context,ResultsList.class);
                intent.putExtra("ISBN",ISBNEditText.getText().toString());
                startActivity(intent);
            }
        });

        return view;
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
}
