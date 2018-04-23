package com.example.andrea.lab11;

import android.app.Activity;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import static android.graphics.drawable.Drawable.createFromPath;


public class showProfile extends AppCompatActivity{

    //todo open profileImage full screen
    //todo aggiungere un strings a toolbar addbook

    private MyUser myUser;
    private float downX, downY,upY, upX;
    private String previousActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //+++++++++++++set fields+++++++++++++
        setContentView(R.layout.show_profile);

        previousActivity = getIntent().getStringExtra("caller");

        //tab listener
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.getTabAt(0).select();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()){
                    case "Profilo":

                        break;
                    case "AddBook":
                        Utilities.goToAddBook(getApplicationContext(), previousActivity,
                                "showProfile", showProfile.this);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("tab", "edit-unselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("tab", "edit-reselected" + tab.getText());
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.getTabAt(0).select();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //create MyUser
        myUser = new MyUser(getApplicationContext());

        //TODO change value if myUser's elements are null (to delete in final release of app)
        String name;
        String surname;
        String email;
        String biography;
        if(myUser.getName() ==null){
            name = getResources().getString(R.string.name);
        }else{
            name = myUser.getName();
        }
        if(myUser.getSurname()==null){
            surname = getResources().getString(R.string.surname);
        }else{
            surname = myUser.getSurname();
        }
        if(myUser.getEmail()==null){
                email = getResources().getString(R.string.email);
            }else{
            email = myUser.getEmail();
        }if(myUser.getBiography()==null){
            biography = getResources().getString(R.string.bio);
        }else{
            biography = myUser.getBiography();
        }

        //set name and surname
        TextView nameSurnameView = findViewById(R.id.nameSurnameShow);
        nameSurnameView.setText(getString(R.string.nameSurname,name,surname));

        //set email
        TextView emailView = findViewById(R.id.emailShow);
        emailView.setText(email);

        //set biography
        TextView biographyView = findViewById(R.id.showProfileBio);
        biographyView.setText(biography);

        //set image
        ImageView profileView = findViewById(R.id.imageViewShow);
        if( myUser.getImage() == null){

            //if there is not a profile image load the default one
            profileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_40dp));
        }else{

            //load the profile image
            Drawable bd = createFromPath(myUser.getImage());
            profileView.setImageDrawable(bd);
        }
    }

    /*
    //TODO make swipe properly this is just a simple way
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;

                // swipe horizontal?
                if (Math.abs(deltaX) > 100) {
                    // left or right
                    if (deltaX > 0) {
                        Log.d(this.getClass().getName(), "swipe");
                        Intent intent = new Intent(
                                getApplicationContext(),
                                AddBookAutomatic.class);
                        startActivity(intent);

                    }
                    //if(deltaX > 0) { this.onRightToLeftSwipe(v); return super.onTouchEvent(event); }
                } else {
                    Log.i(this.getClass().getName(), "Swipe was only " + Math.abs(deltaX) + " long, need at least 100");
                }

            }

        }
        return super.onTouchEvent(event);
    }*/

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.general_menu, popup.getMenu());

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_logout:
                        Utilities.signOut(getApplicationContext());
                        return true;
                    case R.id.menu_edit_profile:
                        //Log.d("popup", "i:" + getIntent().getStringExtra("caller") + " c:"+this.getClass()+ "a: "+getApplicationContext());
                        Utilities.goToEditProfile(getApplicationContext(), previousActivity,
                                "showProfile", showProfile.this);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
