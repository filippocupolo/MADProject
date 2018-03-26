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
    private String address                       = null;
    private String phoneNumber                   = null;
    private String birthDay                      = null;
    private String biography                     = null;
    private String occupation                    = null;
    private HashSet<String> interests            = null;
    private SharedPreferences sharedPreferences  = null;
    private Bitmap photo                         = null;
    private Context applicationContext           = null;

    public MyUser(Context applicationContext){
        try {

            this.applicationContext=applicationContext;

            //open sharedPreferences and load all the data
            sharedPreferences = applicationContext.getSharedPreferences("Profile", MODE_PRIVATE);
            name = sharedPreferences.getString("name", null);
            surname = sharedPreferences.getString("surname", null);
            email = sharedPreferences.getString("email", null);
            address = sharedPreferences.getString("address", null);
            phoneNumber = sharedPreferences.getString("phoneNumber", null);
            birthDay = sharedPreferences.getString("birthDay", null);
            biography = sharedPreferences.getString("biography", null);
            occupation = sharedPreferences.getString("occupation", null);

            //load data
            File file = new File(Utilities.ImagePath, "profile.jpg");
            photo = BitmapFactory.decodeStream(new FileInputStream(file));
        }catch (FileNotFoundException ex){

        }

    }

    //getters
    public String GetName(){
        return name;
    }
    public String GetSurname(){return surname;}
    public String GetEmail(){return email;}
    public String GetAddress(){return  address;}
    public String GetPhoneNumber(){return phoneNumber;}
    public String GetBirthDay(){return birthDay;}
    public String GetBiography(){return biography;}
    public String GetOccupation(){
        return occupation;
    }
    public File GetImage(){
        return new File(Utilities.ImagePath);
    }

    //setters
    public void SetName(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name",value);
        editor.commit();
        name = value;
    }
    public void SetSurname(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("surname",value);
        editor.commit();
        surname = value;
    }
    public void SetEmail(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",value);
        editor.commit();
        email = value;
    }
    public void SetAddress(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address",value);
        editor.commit();
        address = value;
    }
    public void SetPhoneNumber(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber",value);
        editor.commit();
        phoneNumber = value;
    }
    public void SetBirthDay(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("birthDay",value);
        editor.commit();
        birthDay = value;
    }
    public void SetBiography(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("biography",value);
        editor.commit();
        biography = value;
    }
    public void SetOccupation(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("occupation",value);
        editor.commit();
        occupation = value;
    }

    private void SetImage(File inputFile){

        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(inputFile);
            File file = new File(Utilities.ImagePath);
            out = new FileOutputStream(file);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }


        }catch (FileNotFoundException ex) {

        }
        catch (IOException ex){

        }finally {
            try {
                in.close();
                out.close();
            }catch (IOException ex){

            }
        }
    }
}
