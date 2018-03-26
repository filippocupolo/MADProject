package com.example.andrea.lab11;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toolbar;


public class show_profile extends Activity {

    private MyUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        Toolbar spTB= findViewById(R.id.edit_profile_toolbar);
        //setSupportActionBar(spTB);
        myUser = new MyUser(getApplicationContext());
        TextView nameView = findViewById(R.id.nameEdit);
        nameView.setText(myUser.GetName());
        TextView emailView = findViewById(R.id.emailEdit);
        emailView.setText(myUser.GetEmail());
    }

}
