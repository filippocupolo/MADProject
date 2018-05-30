package com.example.andrea.lab11;

import android.content.Context;
import android.util.Log;

import com.firebase.geofire.GeoLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Location {

    private String deBugTag;
    private HashMap<String, GeoLocation> italianSuburbs_coordinates_Map;
    private HashMap<String, GeoLocation> italianTowns_coordinates_Map;
    private JSONArray italianSuburbsJSON;
    private JSONArray italianTownsJSON;

    public Location(Context context){

        deBugTag = this.getClass().getName();
        italianSuburbs_coordinates_Map = new HashMap<>();

        try{
            InputStream is = context.getAssets().open("ItalianProvince.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String myJson = new String(buffer, "UTF-8");

            italianSuburbsJSON = new JSONArray(myJson);
            for (int i = 0; i < italianSuburbsJSON.length(); i++) {
                JSONObject child = italianSuburbsJSON.getJSONObject(i);
                italianSuburbs_coordinates_Map.put(child.getString("nome"),
                        new GeoLocation(child.getDouble("latitudine"),child.getDouble("longitudine")));
            }
        }
        catch(JSONException e){
            Log.e(deBugTag,e.getMessage());
        }
        catch (IOException ioe){
            Log.e(deBugTag,ioe.getMessage());
        }
    }

    public List<String> getItalianSuburbsList(){
        List<String> list = new LinkedList<>();
        list.add("");
        list.addAll(italianSuburbs_coordinates_Map.keySet());
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        return list;
    }

    public List<String> getItalianTowns(String city, Context context){

        //town
        italianTowns_coordinates_Map = new HashMap<>();

        String filename;

        switch (city){
            case "Ascoli Piceno":
                filename = "comuni/AscoliPiceno.json";
                break;
            case "Bolzano/Bozen":
                filename = "comuni/Bolzano.json";
                break;
            case "La Spezia":
                filename = "comuni/Laspezia.json";
                break;
            case "Medio Campidano":
                filename = "comuni/MedioCampidano.json";
                break;
            case "Monza e della Brianza":
                filename = "comuni/MonzaBrianza.json";
                break;
            case "Pesaro e Urbino":
                filename = "comuni/PesaroUrbino.json";
                break;
            case "Reggio di Calabria":
                filename = "comuni/ReggioCalabria.json";
                break;
            case "Reggio nell'Emilia":
                filename = "comuni/ReggioEmilia.json";
                break;
            case "Valle d'Aosta/Vallée d'Aoste":
                filename = "comuni/Aosta.json";
                break;
            case "Vibo Valentia":
                filename = "comuni/ViboValentia.json";
                break;

                default:
                    filename = "comuni/"+city+".json";
                    break;
        }

        //clear hashmap
        italianTowns_coordinates_Map.clear();

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String myJson = new String(buffer, "UTF-8");

            italianTownsJSON = new JSONArray(myJson);
            for (int i = 0; i < italianTownsJSON.length(); i++) {
                JSONObject child = italianTownsJSON.getJSONObject(i);
                italianTowns_coordinates_Map.put(child.getString("nome"), new GeoLocation(child.getDouble("latitudine"), child.getDouble("longitudine")));
            }
        } catch (JSONException e) {
            Log.e(deBugTag, e.getMessage());
        } catch (IOException ioe) {
            Log.e(deBugTag, ioe.getMessage());
        }

        List<String> list = new LinkedList<>();
        list.addAll(italianTowns_coordinates_Map.keySet());
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        return list;
    }

    public GeoLocation getCoordinates(String suburbs){
        return italianSuburbs_coordinates_Map.get(suburbs);
    }

    public String getSuburbs(GeoLocation geoLocation){
        for(Map.Entry<String,GeoLocation> entry :italianSuburbs_coordinates_Map.entrySet()){
            if( entry.getValue().latitude == geoLocation.latitude && entry.getValue().longitude == geoLocation.longitude)
                return entry.getKey();
        }
        return null;
    }

    public GeoLocation getTownCoordinates(String town, String city, Context context){
        //town
        italianTowns_coordinates_Map = new HashMap<>();

        String filename;

        switch (city){
            case "Ascoli Piceno":
                filename = "comuni/AscoliPiceno.json";
                break;
            case "Bolzano/Bozen":
                filename = "comuni/Bolzano.json";
                break;
            case "La Spezia":
                filename = "comuni/Laspezia.json";
                break;
            case "Medio Campidano":
                filename = "comuni/MedioCampidano.json";
                break;
            case "Monza e della Brianza":
                filename = "comuni/MonzaBrianza.json";
                break;
            case "Pesaro e Urbino":
                filename = "comuni/PesaroUrbino.json";
                break;
            case "Reggio di Calabria":
                filename = "comuni/ReggioCalabria.json";
                break;
            case "Reggio nell'Emilia":
                filename = "comuni/ReggioEmilia.json";
                break;
            case "Valle d'Aosta/Vallée d'Aoste":
                filename = "comuni/Aosta.json";
                break;
            case "Vibo Valentia":
                filename = "comuni/ViboValentia.json";
                break;

            default:
                filename = "comuni/"+city+".json";
                break;
        }

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String myJson = new String(buffer, "UTF-8");

            italianTownsJSON = new JSONArray(myJson);
            for (int i = 0; i < italianTownsJSON.length(); i++) {
                JSONObject child = italianTownsJSON.getJSONObject(i);
                italianTowns_coordinates_Map.put(child.getString("nome"), new GeoLocation(child.getDouble("latitudine"), child.getDouble("longitudine")));
            }
        } catch (JSONException e) {
            Log.e(deBugTag, e.getMessage());
        } catch (IOException ioe) {
            Log.e(deBugTag, ioe.getMessage());
        }

        return italianTowns_coordinates_Map.get(town);
    }
}