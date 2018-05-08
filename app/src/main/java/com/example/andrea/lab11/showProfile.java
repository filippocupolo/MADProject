package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static android.graphics.drawable.Drawable.createFromPath;


public class showProfile extends AppCompatActivity{

    private MyUser myUser;
    private String previousActivity;
    private Context context;
    private ImageView profile_image;
    private String deBugTag;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();

        context = getApplicationContext();

        //+++++++++++++set fields+++++++++++++
        setContentView(R.layout.show_profile);

        previousActivity = getIntent().getStringExtra("caller");

        profile_image = findViewById(R.id.imageViewShow);
        profile_image.setOnClickListener(v -> {

            String path = myUser.getImagePath();
            if(myUser.getImageExist() == false){
                Intent intent = new Intent(
                        getApplicationContext(),
                        fullScreenImage.class
                );
                intent.putExtra("path", path);
                startActivity(intent);
            }
            //profile_image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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

        if( myUser.getImageExist() == false){

            //if there is not a profile image load the default one
            profileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_40dp));
        }else{

            //load the profile image
            Drawable bd = createFromPath(myUser.getImagePath());
            profileView.setImageDrawable(bd);
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.general_menu, popup.getMenu());

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_logout:
                        Utilities.signOut(context);
                        return true;
                    case R.id.menu_edit_profile:
                        //Log.d("popup", "i:" + getIntent().getStringExtra("caller") + " c:"+this.getClass()+ "a: "+getApplicationContext());
                        Utilities.goToEditProfile(getApplicationContext(), previousActivity,
                                "showProfile", showProfile.this);
                        return true;
                    case R.id.menu_search_book:
                        Intent intent = new Intent(getApplicationContext(), SearchBook.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

}
