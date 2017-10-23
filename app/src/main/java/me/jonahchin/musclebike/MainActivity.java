package me.jonahchin.musclebike;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RideFragment rideFragment = new RideFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.primary_container, rideFragment).commit();

    }
}
