package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public class MainPageActivity extends AppCompatActivity {

    private String deBugTag;
    private Context context;
    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;
    private TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_page);

        //initialization
        deBugTag = this.getClass().getName();
        context = getApplicationContext();
        viewPager = findViewById(R.id.pageViewer);
        tab = findViewById(R.id.tabLayout);

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tab.setupWithViewPager(viewPager);

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

        private final static int NUM_PAGES = 2;

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
}
