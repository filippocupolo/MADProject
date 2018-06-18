package com.example.andrea.lab11;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;

import static android.support.constraint.Constraints.TAG;


/**
 * Created by filippocupolo on 26/03/18.
 **/

public class Utilities {

    /**
     * This is a static class that contains all the constants and useful methods in common whit more classes
     **/
    public final static String deBugTag = "Utilities";

    private Utilities(){}

    //path of profile image
    public final static String ImagePath = "image.jpg";

    //paths of books pics
    public final static String[] BooksImgsPath = {"BookPic1.jpg","BookPic2.jpg","BookPic3.jpg","BookPic4.jpg","BookPic5.jpg","BookPic6.jpg"};

    //function to validate a Email Address
    public static boolean ValidateEmailAddress(String emailAddress){
        return !TextUtils.isEmpty(emailAddress) && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    /** function to send intent to get a image from storage or from camera.
     *  CAMERA_REQUEST_CODE: the code to request the permissions if needed
     *  PICK_IMAGE: the code to send the intent
     *  is returned a Uri that will point to the image
     **/
    public static Uri requestImage(ActivityCompat activityCompat, Activity activity, int CAMERA_REQUEST_CODE, int PICK_IMAGE){

        Uri selectedImageUri = null;

        if ( Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission( activity, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission( activity, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED)) {
            activityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_REQUEST_CODE);
        }else{

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
            selectedImageUri = Uri.fromFile(photo);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);

            Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePictureIntent});

            activity.startActivityForResult(chooserIntent, PICK_IMAGE);
        }

        return selectedImageUri;
    }

    /** function to read the results of the request permission for the camera and then send intent to get a picture.
     *  grantResults is given by onRequestPermissionsResult call back
     *  CAMERA_REQUEST_CODE the code with witch permissions was asked
     *  PICK_IMAGE: the code to send the intent
     *  is returned a Uri that will point to the image or null if camera permissions were not granted
     *  AAA the to code has to be the same of Utilities.setImage
     **/
    public static Uri cameraRequestPermissionsResult(Activity activity, int[] grantResults, int CAMERA_REQUEST_CODE, int PICK_IMAGE){

        Uri selectedImageUri = null;

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            //return to setImage
            selectedImageUri = requestImage(null, activity, CAMERA_REQUEST_CODE, PICK_IMAGE);

        } else {

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {});

            activity.startActivityForResult(chooserIntent, PICK_IMAGE);

            Toast.makeText(activity, R.string.toast_EditProfile_onRequestPermissionsResult, Toast.LENGTH_LONG).show();
        }

        return selectedImageUri;

    }

    /** function get the image from an intent
     *  required data given by onActivityResult call back
     *  selectedImageUri is a uri were the image can be. This uri is the one returned by setImage
     *  AAA the to selectedImageUri has to be the same of Utilities.requestImage
     **/
    public static Bitmap pictureActivityResult (Activity activity, Intent data, Uri selectedImageUri) throws IOException {

       if(data != null){
            if(data.getData() != null){

                //photos app was selected
                selectedImageUri = data.getData();
            }
        }

        //get bitmap
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);

        //make the bitmap squared
        Bitmap modifiedBitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width > height){
            modifiedBitmap = Bitmap.createBitmap(bitmap,(width-height)/2,0,height,height);
        }else{
            modifiedBitmap = Bitmap.createBitmap(bitmap,0,(height-width)/2,width,width);
        }

        return modifiedBitmap;
    }

    public static void signOut(Context context){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut(); //facebook

        Log.d(deBugTag,"elimino il service");
        context.stopService(new Intent(context, ChatService.class));

        Intent intent = new Intent(
                context,
                login.class
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void loading_and_blur_background(View v, ProgressBar spinner){
        AlphaAnimation alpha = new AlphaAnimation(0.6F, 0.6F);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true);
        spinner.setVisibility(View.VISIBLE);
        v.startAnimation(alpha);
        return;
    }

    public static void show_background(View v, ProgressBar spinner){
        AlphaAnimation alpha = new AlphaAnimation(1F, 1F);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true);
        spinner.setVisibility(View.GONE);
        v.startAnimation(alpha);
        return;
    }

    public static String setStringForResearch(String string){

        //put all letter lowercase
        string = string.toLowerCase();

        //remove all spaces
        string = string.replaceAll("\\s+","");

        return string;
    }

    public static void showDialogForComment(Context context, String type, String userID) {

        String title;
        String message;

        if(type.equals("LENDER_COMMENT")){
            title = context.getResources().getString(R.string.comment);
            message = context.getResources().getString(R.string.give_rate_question);
        }else{
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface d,int i) {
                Intent intent = new Intent(context,CommentActivity.class);
                intent.putExtra("userId",userID);
                context.startActivity(intent);
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();

    }
}
