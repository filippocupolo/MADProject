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
import com.google.android.gms.maps.model.CameraPosition;
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
    private MyUser researcher;
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

        researcher  = new MyUser(getApplicationContext());
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("usersPosition");
        geoFire = new GeoFire(dbRef);

        Location location = new Location();

        researcherLoc = location.getCoordinates(researcher.getCity());

        GeoQuery geoQuery = geoFire.queryAtLocation(researcherLoc, 30);     //Query geoFire for all locations in 30km radius. Save the keys in an arrayList used later
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





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        int i = 0;

        LatLng latlng = new LatLng(researcherLoc.latitude,researcherLoc.longitude);
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng)
                .zoom(10)
                .bearing(0)
                .tilt(30)
                .build();
        mMap.addMarker(new MarkerOptions().position(latlng).title("I'm Here"));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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
