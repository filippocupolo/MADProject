
package com.example.andrea.lab11;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by s201913 on 23/03/2018.
 */

public class MyUser {

    private String userID                        = null;
    private String name                          = null;
    private String surname                       = null;
    private String email                         = null;
    private String city                          = null;
    private String biography                     = null;
    private SharedPreferences sharedPreferences  = null;
    private String photo                         = null;
    private Context applicationContext           = null;
    private SharedPreferences.Editor editor;

    public MyUser(Context applicationContext){

        this.applicationContext=applicationContext;

        //open sharedPreferences and load all the data
        sharedPreferences = applicationContext.getSharedPreferences("Profile", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        name = sharedPreferences.getString("name", null);
        userID = sharedPreferences.getString("userID", null);
        surname = sharedPreferences.getString("surname", null);
        email = sharedPreferences.getString("email", null);
        city = sharedPreferences.getString("city", null);
        biography = sharedPreferences.getString("biography", null);

        //set image path
        File file = new File(applicationContext.getFilesDir(), Utilities.ImagePath);
        if(file.exists()){
            photo = file.getPath();
        }
    }

    //getters
    public String getUserID(){return userID;}
    public String getName(){
        return name;
    }
    public String getSurname(){return surname;}
    public String getEmail(){return email;}
    public String getCity(){return  city;}
    public String getBiography(){return biography;}
    public String getImage(){return photo;}

    //setters
    public void setUserID(String value){
        editor.putString("userID",value);
        userID = value;
    }
    public void setName(String value){
        editor.putString("name",value);
        name = value;
    }
    public void setSurname(String value){
        editor.putString("surname",value);
        surname = value;
    }
    public void setEmail(String value){
        editor.putString("email",value);
        email = value;
    }
    public void setCity(String value){
        editor.putString("city",value);
        city = value;
    }
    public void setBiography(String value){
        editor.putString("biography",value);
        biography = value;
    }
    public void commit(){
        Log.d(this.getClass().getName(),"Commit");
        editor.commit();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("user").child(userID).setValue(name);
    }

    public void setImage(Bitmap bitmap){

        OutputStream out = null;
        try {

            File file = new File(applicationContext.getFilesDir(), Utilities.ImagePath);
            out = new FileOutputStream(file);

            // Transfer bytes from in to out
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);

        }catch (FileNotFoundException ex) {
            Toast.makeText(applicationContext, R.string.toast_MyUser_setImage,Toast.LENGTH_LONG).show();
        }finally {
            try{
                out.close();
            }catch (IOException ioex){

            }
        }
    }
}
