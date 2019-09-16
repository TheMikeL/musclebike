package me.jonahchin.musclebike;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.jonahchin.musclebike.Entities.Ride;
import me.jonahchin.musclebike.Entities.RideDatapoint;
import me.jonahchin.musclebike.Fragments.HistoryListFragment;
import me.jonahchin.musclebike.Fragments.HomeFragment;
import me.jonahchin.musclebike.Fragments.ResultsFragment;
import me.jonahchin.musclebike.Fragments.RideFragment;
import me.jonahchin.musclebike.Fragments.SettingsFragment;
import me.jonahchin.musclebike.Interfaces.HistoryListCallbacks;
import me.jonahchin.musclebike.Interfaces.HomePageCallbacks;
import me.jonahchin.musclebike.Interfaces.RideDao;
import me.jonahchin.musclebike.Interfaces.RideDatapointDao;


public class MainActivity extends AppCompatActivity implements HistoryListCallbacks, HomePageCallbacks{

    private static final String TAG = "MainActivity";
    public BottomNavigationView mBottomNav;

    public static AppDatabase mAppDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppDatabase = AppDatabase.getAppDatabase(getApplicationContext());
        mBottomNav = findViewById(R.id.bottom_nav_view);
        mBottomNav.setSelectedItemId(R.id.menu_ride);


        if ( (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED)) {

//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 12);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 13);

        }


        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.primary_container, new HomeFragment())
                .commit();

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

//        DEBUG_DELETE_RIDES();
//        DEBUG_RIDE_DATA();
    }

    private void DEBUG_DELETE_RIDES() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                RideDao dao = mAppDatabase.rideDao();
                dao.nukeTable();
                return "DONE";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.i(TAG, "completed deletion: " + s);
//                DEBUG_RIDE_DATA();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);

    }

    @SuppressLint("StaticFieldLeak")
    private void DEBUG_RIDE_DATA() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                RideDao dao = mAppDatabase.rideDao();
                RideDatapointDao dataDao = mAppDatabase.rideDatapointDao();

                long rideIdOne = System.currentTimeMillis();
                Ride ride = new Ride();
                ride.setRideId(rideIdOne);
                ride.setElapsedTime(3242000);
                ride.setDistance(7.20);

                dao.insertAll(ride);

                for(int i = 0; i < 3242; i++){
                    RideDatapoint point = new RideDatapoint();
                    point.setTimestamp(i * 1000);
                    point.setCadence(60.9);
                    if(i < 1000) point.setMuscle(20);
                    else if(i < 2000 && i >= 1000) point.setMuscle(20 + (i * 0.03));
                    else if(i >= 2000 && i < 2500) point.setMuscle(80);
                    else if(i >= 2500 && i < 3000) point.setMuscle(80 - (i*0.02));
                    else point.setMuscle(20);
                    point.setBalance(48);
                    point.setLat(44.229);
                    point.setLng(-76.505);
                    point.setRideId(rideIdOne);
                    dataDao.insertAll(point);
                }
                return "Complete";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);

    }

    private void selectFragment(MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.menu_history:
                fragment = new HistoryListFragment();
                break;
            case R.id.menu_ride:
                fragment = new HomeFragment();
                break;
            case R.id.menu_settings:
                fragment = new SettingsFragment();
                break;
        }

        if(fragment == null) return;

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.primary_container, fragment)
                .commit();
    }


    @Override
    public void onListItemClick(Ride ride, boolean backStack) {

        Fragment fragment = ResultsFragment.newInstance(ride);

        if(backStack) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.primary_container, fragment)
                    .addToBackStack("hi")
                    .commit();
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.primary_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onStartButtonClick() {
        Fragment fragment = new RideFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.primary_container, fragment)
                .commit();
    }
}
