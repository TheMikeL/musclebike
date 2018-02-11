package me.jonahchin.musclebike.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

import me.jonahchin.musclebike.MainActivity;
import me.jonahchin.musclebike.R;

import static java.lang.Double.parseDouble;

/**
 * Created by jonahchin on 2017-10-16.
 * Fragment containing info that cyclist will see during ride
 */

public class RideFragment extends Fragment{


    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRideRef = mRootRef.child("live");
    DatabaseReference mForceRef = mRideRef.child("muscle-intensity");
    DatabaseReference mCadenceRef = mRideRef.child("cadence");
    DatabaseReference mLeftBalRef = mRideRef.child("muscle-left");
    DatabaseReference mRightBalRef = mRideRef.child("muscle-right");

    boolean buttonState;
    TextView mMuscleData;
    TextView mCadenceData;
    TextView mLeftBalData;
    TextView mRightBalData;
    TextView mSpeedData;
    TextView mDistanceData;
    boolean ride;
    TextView stopWatch;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    int Seconds, Minutes, MilliSeconds, Hours;
    double initialLatitude, initialLongitude;
    double prevLat, prevLong, currLat, currLong;
    double distance_covered;


    Handler handler;

    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride, container, false);
        stopWatch = view.findViewById(R.id.time_value);
        mCadenceData = view.findViewById(R.id.cadence_value);
        mLeftBalData = view.findViewById(R.id.muscle_left);
        mRightBalData = view.findViewById(R.id.muscle_right);
        mMuscleData = view.findViewById(R.id.muscle_value);
        mDistanceData = view.findViewById(R.id.distance_elapsed);
        mSpeedData = view.findViewById(R.id.speed_value);

        mDistanceData.setText("10.1");
        mSpeedData.setText("5.1");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        ride = true;

        buttonState = true;
        final FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonState){
                    initializeLocation();

                    handler = new Handler() ;
                    fab.setColorFilter(Color.parseColor("#b30000"));
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    final long rideID = System.currentTimeMillis(); //Save this
                    locationCallBacks();
                    startLocationUpdates();

                    //Start timer and distance calc
                    buttonState = false;
                }else{
                    fab.setColorFilter(Color.parseColor("#1f4927"));
                    ride = false;
                    TimeBuff += MillisecondTime;
                    String elapsed_time = String.format("%2dm%2ds", Minutes, Seconds).trim();
                    mDistanceData.setText(String.valueOf(distance_covered));
                    stopLocationUpdates();
                    handler.removeCallbacksAndMessages(runnable);
                    //End Ride
                    //Save distance, elapsed time and ride ID to phone
                }
            }
        });

        setListeners();
        return view;
    }

    private void initializeLocation(){

        if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( getActivity(), new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    11);
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                initialLatitude = location.getLatitude();
                initialLongitude = location.getLongitude();
                prevLat = initialLatitude;
                prevLong = initialLongitude;
                if (initialLatitude != 0)
                    Toast.makeText(getActivity(),String.valueOf(initialLatitude) + ',' + String.valueOf(initialLongitude),Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(),"no Lats",Toast.LENGTH_LONG).show();
                if (location != null) {
                    // Logic to handle location object
                }
            }
        });
    }

    private void locationCallBacks(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {

                    currLat = location.getLatitude();
                    currLong = location.getLongitude();
//                    Toast.makeText(getActivity(),String.valueOf(prevLat) + ',' + String.valueOf(currLat) +","+ String.valueOf(prevLong) + ',' + String.valueOf(currLong),Toast.LENGTH_SHORT).show();
                    calcDistance(prevLat, prevLong, currLat, currLong);
                }
                prevLat = currLat;
                prevLong = currLong;
            };
        };
    }

    private void calcDistance(double prevLat, double prevLong, double currLat, double currLong){
        DecimalFormat df = new DecimalFormat("#.######");
        DecimalFormat resultF = new DecimalFormat("#.#");
        DecimalFormat resultF2= new DecimalFormat("#.##");
        prevLat= parseDouble(df.format(prevLat));
        prevLong= parseDouble(df.format(prevLong));
        currLat= parseDouble(df.format(currLat));
        currLong= parseDouble(df.format(currLong));
        currLong= parseDouble(df.format(currLong));
//        Toast.makeText(getActivity(),df.format(prevLat) + ',' + df.format(prevLong) + ',' + df.format(currLat) + ',' + df.format(currLong),Toast.LENGTH_LONG).show();


        double dLat = Math.toRadians(currLat-prevLat);
        double dLon = Math.toRadians(Math.abs(currLong)-Math.abs(prevLong));
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(prevLat)) * Math.cos(Math.toRadians(currLat)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        distance_covered += (Math.round(6371000 * c))*0.001;
        if (distance_covered < 1){
            distance_covered = parseDouble(resultF2.format(distance_covered));
        }else{
            distance_covered = parseDouble(resultF.format(distance_covered));
        }

//                Toast.makeText(getActivity(),String.valueOf(distance_covered),Toast.LENGTH_LONG).show();

        mDistanceData.setText(String.valueOf(distance_covered));
    }

    private void startLocationUpdates() {
        if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( getActivity(), new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    11);
        }
        mFusedLocationClient.requestLocationUpdates(createLocationRequest(),
                mLocationCallback,
                null /* Looper */);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public Runnable runnable = new Runnable() {

            public void run(){
                if (ride) {
                    MillisecondTime = SystemClock.uptimeMillis() - StartTime;
                    UpdateTime = TimeBuff + MillisecondTime;
                    Seconds = (int) (UpdateTime / 1000);
                    Hours = Minutes/ 60;
                    Minutes = Seconds / 60;
                    if (Minutes  >= 60){
                        Minutes = 0;
                    }
                    Seconds = Seconds % 60;
                    MilliSeconds = (int) (UpdateTime % 100);
                    if (Hours >= 1){
                        stopWatch.setText(String.format("%02d",Hours) + ":"
                                + String.format("%02d", Minutes) + ":"
                                + String.format("%02d", Seconds) + ":" );
                    }else {
                        stopWatch.setText(String.format("%02d", Minutes) + ":"
                                + String.format("%02d", Seconds) + ":"
                                + String.format("%02d", MilliSeconds));
                    }
                    handler.postDelayed(this, 0);
                }
            }
    };

    private void setListeners() {
        mForceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long force = dataSnapshot.getValue(Long.class);
                String forceStr = String.valueOf(force);
                mMuscleData.setText(forceStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCadenceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long cadenceVal = dataSnapshot.getValue(Long.class);
                String cadenceStr = String.valueOf(cadenceVal);
                mCadenceData.setText(cadenceStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mLeftBalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long leftBal = dataSnapshot.getValue(Long.class);
                String leftBalStr = String.valueOf(leftBal);
                mLeftBalData.setText(leftBalStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRightBalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long rightBal = dataSnapshot.getValue(Long.class);
                String rightBalStr = String.valueOf(rightBal);
                mRightBalData.setText(rightBalStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
