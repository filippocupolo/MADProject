
package com.example.andrea.lab11;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private String town                          = null;
    private String biography                     = null;
    private SharedPreferences sharedPreferences  = null;
    private String imagePath                     = null;
    private boolean imageExist                   = false;
    private Context applicationContext           = null;
    private SharedPreferences.Editor editor;
    private String deBugTag;

    public MyUser(Context applicationContext){

        deBugTag = this.getClass().getName();

        this.applicationContext=applicationContext;

        //open sharedPreferences and load all the data
        sharedPreferences = applicationContext.getSharedPreferences("Profile", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        name = sharedPreferences.getString("name", null);
        userID = sharedPreferences.getString("userID", null);
        surname = sharedPreferences.getString("surname", null);
        email = sharedPreferences.getString("email", null);
        city = sharedPreferences.getString("city", null);
        town = sharedPreferences.getString("town", null);
        biography = sharedPreferences.getString("biography", null);
        imageExist = sharedPreferences.getBoolean("imageExist", false);

        //set image path
        File file = new File(applicationContext.getFilesDir(), Utilities.ImagePath);
        if(file.exists()){
            imagePath = file.getPath();
        }
    }

    //getters
    public String getName(){
        return name;
    }
    public String getSurname(){return surname;}
    public String getEmail(){return email;}
    public String getCity(){return  city;}
    public String getTown(){return  town;}
    public String getBiography(){return biography;}
    public String getImagePath(){return imagePath;}
    public boolean getImageExist(){return imageExist;}
    public String getUserID(){return userID;}

    //setters
    public void setUserID(String value){
        editor.putString("userID",value);
        userID = value;
        editor.commit();
    }
    public void setName(String value){
        editor.putString("name",value);
        name = value;
        editor.commit();
    }
    public void setSurname(String value){
        editor.putString("surname",value);
        surname = value;
        editor.commit();
    }
    public void setEmail(String value){
        editor.putString("email",value);
        email = value;
        editor.commit();
    }
    public void setCity(String value){
        editor.putString("city",value);
        city = value;
        editor.commit();
    }

    public void setTown(String value){
        editor.putString("town", value);
        town = value;
        editor.commit();

    }
    public void setBiography(String value){
        editor.putString("biography",value);
        biography = value;
        editor.commit();
    }
    public void setImageExist(boolean value){
        editor.putBoolean("imageExist",value);
        imageExist = value;
        editor.commit();
    }
    public void commit(){

        Location location = new Location(applicationContext);
        GeoLocation coordinates = location.getTownCoordinates(town, city, applicationContext);

        Log.d(deBugTag,"Commit");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        dbRef.child(userID).child("name").setValue(name);
        dbRef.child(userID).child("surname").setValue(surname);
        dbRef.child(userID).child("email").setValue(email);
        dbRef.child(userID).child("city").setValue(city);
        dbRef.child(userID).child("biography").setValue(biography);
        dbRef.child(userID).child("image").setValue(imageExist);
        dbRef.child(userID).child("town").setValue(town);

        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("usersPosition"));
        geoFire.setLocation(userID, coordinates, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if(error!= null){

                    //todo testare
                    Log.e(deBugTag,error.getMessage());
                    dbRef.child(userID).removeValue();
                }
            }
        });
    }

    public void setImage(Bitmap bitmap){

        OutputStream out = null;
        File file = null;
        try {

            file = new File(applicationContext.getFilesDir(), Utilities.ImagePath);
            out = new FileOutputStream(file);

            // Transfer bytes from in to out
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

        }catch (FileNotFoundException ex) {
            Toast.makeText(applicationContext, R.string.toast_MyUser_setImage,Toast.LENGTH_LONG).show();
        }finally {
            try{
                out.close();
                setImageExist(true);
                uploadImage(new File(applicationContext.getFilesDir(), Utilities.ImagePath));
            }catch (IOException ioex){

            }
        }
    }

    public boolean isCompleted(){
        if(email==null || name==null || surname==null || city==null)
            return false;
        else
            return true;
    }

    public void downloadImage(){

        setImageExist(false);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImages/"+userID);
        File file = new File(applicationContext.getFilesDir(), Utilities.ImagePath);

        ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.d(deBugTag,"fileCreato");
                setImageExist(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(deBugTag,"eccezione " + exception.toString() + exception.getMessage() + " " + exception.getStackTrace());
            }
        });
    }

    private void uploadImage(File file) {

        if(file.exists()){
            Log.d(this.getClass().getName(),"esiste");
        }
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImages/"+userID);
        ref.putFile(Uri.fromFile(file))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
                        dbRef.child(userID).child("image").setValue(true);
                        Toast.makeText(applicationContext, applicationContext.getString(R.string.image_uploaded), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(this.getClass().getName(),e.getMessage());
                        Toast.makeText(applicationContext, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
