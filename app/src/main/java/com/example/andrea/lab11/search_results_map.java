package com.example.andrea.lab11;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.ViewTreeObserver;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class search_results_map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<String> userResults = new ArrayList<>();
    private MyUser researcher = new MyUser(getApplicationContext());
    private GeoLocation researcherLoc;
    private GeoFire geoFire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("https://madproject-d48a9.firebaseio.com/usersPosition");
        geoFire = new GeoFire(dbRef);

        Location location = new Location();

        researcherLoc = location.getCoordinates(researcher.getCity());

        GeoQuery geoQuery = geoFire.queryAtLocation(researcherLoc, 10);     //Query geoFire for all locations in 10km radius. Save the keys in an arrayList used later
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                    if(!userResults.contains(key)) {
                        userResults.add(key);

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
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int i = 0;

        LatLng latlng = new LatLng(researcherLoc.latitude,researcherLoc.longitude);
        mMap.addMarker(new MarkerOptions().position(latlng).title("I'm Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        while(userResults.listIterator().hasNext())
        {
            geoFire.getLocation(userResults.get(i), new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {

                    if(location!=null)
                    {
                        LatLng latlng = new LatLng(location.latitude,location.longitude);
                        mMap.addMarker(new MarkerOptions().position(latlng));
                    }
                    else
                    {
                        System.out.println(String.format("Location for key %s not found",key));

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    System.err.println("There was an error getting the GeoFire location: " + databaseError);
                }
            });

            i++;
        }
    }
}
