package me.jonahchin.musclebike;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

//        DEBUG_RIDE_DATA();
//        DEBUG_DELETE_RIDES();
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
                ride.setDistance(10.20);


                long rideIdTwo = System.currentTimeMillis() + 10;

                Ride ride2 = new Ride();
                ride2.setRideId(rideIdTwo);
                ride2.setElapsedTime(354399);
                ride2.setDistance(50);

                dao.insertAll(ride, ride2);

                for(int i = 0; i < 10; i++){
                    RideDatapoint point = new RideDatapoint();
                    point.setTimestamp(i);
                    point.setCadence(10*i);
                    point.setMuscle(70);
                    point.setBalance(40+i);
                    point.setLat(44.229 + (i * 0.05));
                    point.setLng(-76.505 + (i * 0.05));
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
    public void onListItemClick(Ride ride) {


        Fragment fragment = ResultsFragment.newInstance(ride);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.primary_container, fragment)
                .commit();
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
