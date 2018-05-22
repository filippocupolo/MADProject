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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.gms.maps.UiSettings;
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

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchBookFragment extends Fragment implements OnMapReadyCallback {

    private String deBugTag;
    private ImageButton authorSearchButton;
    private EditText authorEditText;
    private Context context;
    private MapView mapView;
    private ConcurrentHashMap<String,LatLng> user_position;
    private ConcurrentHashMap<LatLng,Marker> position_marker;
    private ConcurrentHashMap<Marker,HashSet<String>> position_books;
    private final static double RADIUS = 45.0;


    public SearchBookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialization
        deBugTag = this.getClass().getName();
        context = getActivity().getApplicationContext();
        user_position = new ConcurrentHashMap<>();
        position_marker = new ConcurrentHashMap<>();
        position_books = new ConcurrentHashMap<>();
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
            if(authorEditText.getText().toString().trim().length()==0){
                //field cannot be empty
                authorEditText.setError(getString(R.string.empty_search));
            }else{
                Intent intent = new Intent(context, ResultsList.class);
                intent.putExtra("keyword",Utilities.setStringForResearch(authorEditText.getText().toString()));
                startActivity(intent);
            }
        });

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        UiSettings uiSettings = googleMap.getUiSettings();

        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setCompassEnabled(false);

        //get position of the user
        MyUser researcher  = new MyUser(context);
        Location location = new Location(context);

        GeoLocation researcherLoc = location.getTownCoordinates(researcher.getTown(), researcher.getCity(), context);

        //todo cambiare lo zoom e il radius
        LatLng latlng = new LatLng(researcherLoc.latitude,researcherLoc.longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng)
                .zoom(9)
                .bearing(0)
                .tilt(30)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //set queries of
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        GeoFire geoFire = new GeoFire(dbRef.child("usersPosition"));

        //geoquery with center and radius
        geoFire.queryAtLocation(new GeoLocation(researcherLoc.latitude, researcherLoc.longitude), RADIUS).addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                LatLng userLocation = new LatLng(location.latitude, location.longitude);
                user_position.put(key,userLocation);

                dbRef.child("books").orderByChild("owner").equalTo(key).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        LatLng bookLocation = user_position.get(dataSnapshot.child("owner").getValue());
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(bookLocation));

                        HashSet<String> bookSet = position_books.get(bookLocation);
                        if(bookSet==null){
                            bookSet = new HashSet<>();
                            bookSet.add(dataSnapshot.getKey());
                            position_books.put(marker,bookSet);
                            position_marker.put(bookLocation,marker);
                        }else{
                            bookSet.add(dataSnapshot.getKey());
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        //todo testare se facendo cosi rimuove tutti i libri da quella location
                        Log.d(deBugTag,"re");
                        if(dataSnapshot == null)
                            return;

                        LatLng bookLocation = user_position.get(dataSnapshot.child("owner").getValue().toString());
                        Marker m = position_marker.get(bookLocation);
                        Log.d(deBugTag, dataSnapshot.child("bookTitle").getValue().toString()+"");
                        position_books.get(m).remove(dataSnapshot.getKey());

                        if(position_books.get(m).size() == 0){
                            position_books.remove(bookLocation);
                            m.remove();
                            position_marker.remove(bookLocation);

                        }

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        networkProblem(databaseError);
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                Log.d(deBugTag, "exited");
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(deBugTag, "moved");
            }

            @Override
            public void onGeoQueryReady() {
                Log.d(deBugTag, "ready");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                //todo gestisci
                Log.e(deBugTag, "error");
            }
        });

        //markers
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                HashSet<String> booksAtPosition = position_books.get(marker);

                //open a list of the selected books
                Intent intent = new Intent(context,ResultsList.class);
                Log.d(deBugTag,booksAtPosition.size()+"");
                intent.putExtra("bookIdList",booksAtPosition);
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

    private void networkProblem(DatabaseError databaseError){
        if(databaseError.getCode()!=-3){
            Toast.makeText(context,R.string.network_problem,Toast.LENGTH_SHORT).show();
        }
    }
}
