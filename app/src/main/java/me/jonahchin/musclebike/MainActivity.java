package me.jonahchin.musclebike;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import me.jonahchin.musclebike.Fragments.HistoryListFragment;
import me.jonahchin.musclebike.Fragments.RideFragment;
import me.jonahchin.musclebike.Fragments.SettingsFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView mBottomNav;

    //frags
    HistoryListFragment mHistoryListFragment;
    RideFragment mRideFragment;
    SettingsFragment mSettingsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBottomNav = findViewById(R.id.bottom_nav_view);
        mBottomNav.setSelectedItemId(R.id.menu_ride);


        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.primary_container, new RideFragment())
                .commit();

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });
    }


    private void selectFragment(MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.menu_history:
                fragment = new HistoryListFragment();
                Log.e(TAG, "selected history");
                break;
            case R.id.menu_ride:
                fragment = new RideFragment();
                break;
            case R.id.menu_settings:
                fragment = new SettingsFragment();
                break;
        }

        if(fragment == null) return;

        Log.e(TAG, "adding frag");
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.primary_container, fragment)
                .commit();
    }


}
