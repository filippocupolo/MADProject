package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchBookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchBookFragment} factory method to
 * create an instance of this fragment.
 */
public class SearchBookFragment extends Fragment implements OnMapReadyCallback {

    private String deBugTag;
    private ImageButton authorSearchButton;
    private EditText authorEditText;
    private Context context;
    private GoogleMap googleMap;
    private MapView mapView;
    private Query mapQuery;
    private GeoQuery geoQuery;
    private GeoLocation researcherLoc;
    private ConcurrentHashMap<Marker,HashSet<String>> position_users;
    private ConcurrentHashMap<String,HashSet<String>> user_books;
    private MyUser user;
    private final static double RADIUS = 30.0;

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
        mapQuery = FirebaseDatabase.getInstance().getReference().child("books").limitToFirst(10);
        position_users = new ConcurrentHashMap<>();
        user_books = new ConcurrentHashMap<>();
        user = new MyUser(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_book, container, false);
        authorSearchButton = view.findViewById(R.id.authorSearchButton);
        authorEditText = view.findViewById(R.id.searchAuthor);
        mapView = view.findViewById(R.id.mapViewSearchBook);
        mapView.onCreate(savedInstanceState);

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

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        //get position of the user
        MyUser researcher  = new MyUser(context);
        Location location = new Location(context);

        researcherLoc = location.getTownCoordinates(researcher.getTown(), researcher.getCity(), context);

        LatLng latlng = new LatLng(researcherLoc.latitude,researcherLoc.longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng)
                .zoom(10)
                .bearing(0)
                .tilt(30)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("usersPosition"));

        //geoquery with center and radius
        geoQuery = geoFire.queryAtLocation(new GeoLocation(researcherLoc.latitude, researcherLoc.longitude), RADIUS);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                LatLng userLocation = new LatLng(location.latitude, location.longitude);
                Marker marker = googleMap.addMarker(new MarkerOptions().position(userLocation));

                //set position and userID on position_users map
                HashSet<String> usersSet = position_users.get(userLocation);
                if(usersSet==null){
                    usersSet = new HashSet<>();
                    usersSet.add(key);
                    position_users.put(marker, usersSet);
                } else {
                    usersSet.add(key);
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

/*
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("usersPosition"));
        geoFire.getLocation(user.getUserID(), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    //set map zoom on user location
                    LatLng latlng = new LatLng(location.latitude,location.longitude);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latlng)
                            .zoom(10)
                            .bearing(0)
                            .tilt(30)
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    //geoquery with center and radius
                    geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), RADIUS);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            LatLng userLocation = new LatLng(location.latitude, location.longitude);
                            Marker marker = googleMap.addMarker(new MarkerOptions().position(userLocation));

                            //set position and userID on position_users map
                            HashSet<String> usersSet = position_users.get(userLocation);
                            if(usersSet==null){
                                usersSet = new HashSet<>();
                                usersSet.add(key);
                                position_users.put(marker, usersSet);
                            } else {
                                usersSet.add(key);
                            }

                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });

                } else {
                    //When location is null
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //LogDatabase error
            }
        });*/


        //markers
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                HashSet<String> usersAtPosition = position_users.get(marker);
                Log.d(deBugTag, "c:"+usersAtPosition.size());
                //open a list of the selected books
                Intent intent = new Intent(context,ResultsList.class);
                intent.putExtra("usersList",usersAtPosition);
                startActivity(intent);

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
