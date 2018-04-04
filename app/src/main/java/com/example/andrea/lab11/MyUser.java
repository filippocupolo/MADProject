
package com.example.andrea.lab11;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.logging.Logger;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by s201913 on 23/03/2018.
 */

public class MyUser {

    private String name                          = null;
    private String surname                       = null;
    private String email                         = null;
    private String city                          = null;
    private String phoneNumber                   = null;
    private String birthDay                      = null;
    private String biography                     = null;
    private String occupation                    = null;
    private SharedPreferences sharedPreferences  = null;
    private String photo                         = null;
    private Context applicationContext           = null;

    public MyUser(Context applicationContext){

        this.applicationContext=applicationContext;
        //open sharedPreferences and load all the data
        sharedPreferences = applicationContext.getSharedPreferences("Profile", MODE_PRIVATE);
        name = sharedPreferences.getString("name", null);
        surname = sharedPreferences.getString("surname", null);
        email = sharedPreferences.getString("email", null);
        city = sharedPreferences.getString("city", null);
        phoneNumber = sharedPreferences.getString("phoneNumber", null);
        birthDay = sharedPreferences.getString("birthDay", null);
        biography = sharedPreferences.getString("biography", null);
        occupation = sharedPreferences.getString("occupation", null);

        //set image path
        File file = new File(applicationContext.getFilesDir(), Utilities.ImagePath);
        if(file.exists()){
            photo = file.getPath();
        }
    }

    //getters
    public String getName(){
        return name;
    }
    public String getSurname(){return surname;}
    public String getEmail(){return email;}
    public String getCity(){return  city;}
    public String getPhoneNumber(){return phoneNumber;}
    public String getBirthDay(){return birthDay;}
    public String getBiography(){return biography;}
    public String getOccupation(){return occupation;}
    public String getImage(){return photo;}

    //setters
    public void setName(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name",value);
        editor.commit();
        name = value;
    }
    public void setSurname(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("surname",value);
        editor.commit();
        surname = value;
    }
    public void setEmail(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",value);
        editor.commit();
        email = value;
    }
    public void setCity(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city",value);
        editor.commit();
        city = value;
    }
    public void setPhoneNumber(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber",value);
        editor.commit();
        phoneNumber = value;
    }
    public void setBirthDay(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("birthDay",value);
        editor.commit();
        birthDay = value;
    }
    public void setBiography(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("biography",value);
        editor.commit();
        biography = value;
    }
    public void setOccupation(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("occupation",value);
        editor.commit();
        occupation = value;
    }

    public void setImage(Bitmap bitmap){

        OutputStream out = null;
        try {

            File file = new File(applicationContext.getFilesDir(), Utilities.ImagePath);
            out = new FileOutputStream(file);

            // Transfer bytes from in to out
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        }catch (FileNotFoundException ex) {


        }finally {
            try {
                out.close();
            }catch (IOException ex){

            }
        }
    }
}
