package com.example.andrea.lab11;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by filippocupolo on 26/03/18.
 */

public class Utilities {

    /**
     * This is a static class that contains all the constants and useful methods in common whit more classes
     */

    private Utilities(){}

    //path of profile image
    public final static String ImagePath = "image.jpg";

    //function to validate a Email Address
    static boolean ValidateEmailAddress(String emailAddress){
        return !TextUtils.isEmpty(emailAddress) && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }


}
