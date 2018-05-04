package com.example.andrea.lab11;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class search_results_map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Map<String,Vector<Float>> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    private void apiRequest(String city){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://maps.googleapis.com/maps/api/geocode/json?address="+city+"&key=AIzaSyCUHon0d5OjPqv03FrbViO1VfD8WxkZQoY", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try{
                    JSONObject rootJSON=new JSONObject(new String(response));
                    JSONObject cityInfo = rootJSON.getJSONArray("items").optJSONObject(0).getJSONObject("results");



                    Intent intent = new Intent(getApplicationContext(),insertBook.class);
                    intent.putExtra("city", cities.get());
                    startActivity(intent);

                }catch (JSONException e){
                    Log.e(deBugTag,"JSONException");
                    Log.e(deBugTag,e.getMessage());

                    Toast.makeText(context,getString(R.string.book_not_found),Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(deBugTag,statusCode+"");
                Toast.makeText(context,getString(R.string.book_not_found),Toast.LENGTH_SHORT);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
