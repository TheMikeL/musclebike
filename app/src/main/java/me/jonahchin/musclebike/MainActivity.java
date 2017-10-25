package me.jonahchin.musclebike;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        RideFragment rideFragment = (RideFragment) fm.findFragmentById(R.id.primary_container);

        if(rideFragment == null) {
            rideFragment = new RideFragment();
            fm.beginTransaction()
                    .add(R.id.primary_container, rideFragment)
                    .commit();
        }
    }


}
