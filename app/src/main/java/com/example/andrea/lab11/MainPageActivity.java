package com.example.andrea.lab11;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainPageActivity extends AppCompatActivity {

    private String deBugTag;
    private DatabaseReference dbRef;
    private Context context;
    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;
    private TabLayout tab;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_page);

        //initialization
        deBugTag = this.getClass().getName();
        context = getApplicationContext();
        viewPager = findViewById(R.id.pageViewer);
        tab = findViewById(R.id.tabLayout);

        //set service
        Intent intent = new Intent(this, ChatService.class);
        intent.setPackage(this.getPackageName());
        startService(intent);

        //set ViewPager
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tab.setupWithViewPager(viewPager);

        //check if intent wants to open a specific page
        int numPage = getIntent().getIntExtra("page",0);
        viewPager.setCurrentItem(numPage);

        //check for notification
        dbRef = FirebaseDatabase.getInstance().getReference().child("bookAccepted")
                .child(new MyUser(getApplicationContext()).getUserID());
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //todo fai stringa
                String userId = dataSnapshot.child("userId").toString();
                String message = "Complimenti hai ricevuto un nuovo libro.\nVuoi lasciare un commento?";
                String title = "Hai ricevuto un libro";

                dbRef.child(dataSnapshot.getKey()).removeValue();

                sendNotification(title,message,userId);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addChildEventListener(childEventListener);

    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(getApplicationContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.general_menu, popup.getMenu());

        popup.show();

        //click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_logout:
                        Utilities.signOut(context);
                        return true;
                    case R.id.menu_edit_profile:
                        Intent editProfileIntent = new Intent(getApplicationContext(),editProfile.class);
                        startActivity(editProfileIntent);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        private final static int NUM_PAGES = 3;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = Fragment.instantiate(context, SearchBookFragment.class.getName());
                    break;
                case 1:
                    fragment = Fragment.instantiate(context, MyBookList.class.getName());
                    break;
                case 2:
                    fragment = Fragment.instantiate(context, Chat.class.getName());
                    break;
            }
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position){
                case 0:
                    title = getString(R.string.search_book);
                    break;
                case 1:
                    title = getString(R.string.my_books);
                    break;
                case 2:
                    title = "Chat";
                    break;
            }
            return title;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbRef!=null){
            dbRef.removeEventListener(childEventListener);
            childEventListener=null;
            dbRef=null;
        }
    }

    private void sendNotification(String messageTitle, String messageBody, String user_id) {
        Intent intent = new Intent(getApplicationContext(), showProfile.class);
        intent.putExtra("userId", user_id);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
