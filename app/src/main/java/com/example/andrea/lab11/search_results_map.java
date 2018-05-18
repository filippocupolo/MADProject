package com.example.andrea.lab11;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.Header;

public class search_results_map extends FragmentActivity implements OnMapReadyCallback {

    private ConcurrentHashMap<Marker,HashSet<String>> position_users;
    private ConcurrentHashMap<String,HashSet<String>> user_books;
    private GeoLocation researcherLoc;
    private String deBugTag;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        deBugTag = this.getClass().getName();
        position_users = new ConcurrentHashMap<>();
        user_books = new ConcurrentHashMap<>();
        setContentView(R.layout.search_results_map);

        //get query from previous activity
        Intent intent = getIntent();
        Query bookQuery = FirebaseDatabase.getInstance().getReference().child("books");

        String valueExtra = intent.getStringExtra("keyword");

        //set buttons
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
        ImageButton listButton = findViewById(R.id.mapButton);
        listButton.setOnClickListener(v->{
            Intent listIntent = new Intent(getApplicationContext(),ResultsList.class);
            listIntent.putExtra("keyword",valueExtra);
            startActivity(listIntent);
            finish();
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        //get position of the user
        MyUser researcher  = new MyUser(getApplicationContext());
        Location location = new Location(getApplicationContext());
        researcherLoc = location.getCoordinates(researcher.getCity());

        String[] valueVector;

        if (valueExtra.matches("[0-9]+"))
            valueVector = new String[]{"ISBN"};
        else
            valueVector = new String[]{"authorSearch","bookTitleSearch","publisherSearch"};

        for(int i = 0; i<valueVector.length; i++){
            bookQuery.orderByChild(valueVector[i]).equalTo(valueExtra).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    String ownerID = dataSnapshot.child("owner").getValue().toString();
                    String bookID = dataSnapshot.getKey();

                    //set ownerID and book ID on user_books map
                    HashSet<String> booksSet = user_books.get(ownerID);
                    if(booksSet==null){
                        booksSet = new HashSet<>();
                        booksSet.add(bookID);
                        user_books.put(ownerID,booksSet);
                    }else{
                        booksSet.add(bookID);
                    }

                    //search owner position
                    DatabaseReference positionRef = FirebaseDatabase.getInstance().getReference().child("usersPosition");
                    GeoFire geoFire = new GeoFire(positionRef);
                    geoFire.getLocation(ownerID, new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {

                            if (location == null) {
                                Log.e(deBugTag, "NOT location for key: " + key);
                            } else {

                                LatLng userLocation = new LatLng(location.latitude, location.longitude);

                                //add marker on map
                                Marker marker = googleMap.addMarker(new MarkerOptions().position(userLocation));

                                //set position and userID on position_users map
                                HashSet<String> usersSet = position_users.get(userLocation);
                                if(usersSet==null){
                                    usersSet = new HashSet<>();
                                    usersSet.add(key);
                                    position_users.put(marker,usersSet);
                                }else{
                                    if(!usersSet.contains(key))
                                        usersSet.add(key);
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            networkProblem(databaseError);
                        }
                    });

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    //todo testare l'eliminazione di un oggetto

                    String ownerID = dataSnapshot.child("owner").getValue().toString();
                    String bookID = dataSnapshot.getKey();

                    HashSet <String> bookList = user_books.get(ownerID);

                    if(bookList.size()>1){
                        bookList.remove(bookID);
                    }else{

                        user_books.remove(ownerID);

                        Marker toRemove = null;

                        for (Map.Entry<Marker, HashSet<String>> entry : position_users.entrySet()) {

                            if (entry.getValue().contains(ownerID)) {
                                toRemove = entry.getKey();
                                break;
                            }
                        }

                        HashSet <String> userList = position_users.get(toRemove);

                        if(userList.size()>1){
                            userList.remove(ownerID);
                        }else {

                            position_users.remove(toRemove);
                            toRemove.remove();
                        }

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        UiSettings settings = googleMap.getUiSettings();
        settings.setMapToolbarEnabled(false);

        //set map zoom on user location
        LatLng latlng = new LatLng(researcherLoc.latitude,researcherLoc.longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng)
                .zoom(8)
                .bearing(0)
                .tilt(30)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                HashSet<String> usersAtPosition = position_users.get(marker);
                LinkedList<String> selectedBook = new LinkedList<>();
                for(String user : usersAtPosition){
                    selectedBook.addAll(user_books.get(user));
                }

                if(selectedBook.size()==1){

                    //open show book
                    Intent intent = new Intent(getApplicationContext(), ShowBook.class);
                    intent.putExtra("bookId",selectedBook.get(0));
                    startActivity(intent);

                }else{

                    //open a list of the selected books
                    Intent intent = new Intent(getApplicationContext(),ResultsList.class);
                    intent.putExtra("bookIdList",selectedBook);
                    startActivity(intent);
                }

                for(String s : selectedBook)
                    Log.d(deBugTag, "libro selezionato: " + s);

                return true;
            }
        });

    }

    private void networkProblem(DatabaseError databaseError){
        if(databaseError.getCode()!=-3){
            Toast.makeText(getApplicationContext(),R.string.network_problem,Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }
}
