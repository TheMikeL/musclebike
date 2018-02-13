package me.jonahchin.musclebike.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import me.jonahchin.musclebike.Entities.Ride;
import me.jonahchin.musclebike.Entities.RideDatapoint;
import me.jonahchin.musclebike.Interfaces.HistoryListCallbacks;
import me.jonahchin.musclebike.Interfaces.RideDao;
import me.jonahchin.musclebike.Interfaces.RideDatapointDao;
import me.jonahchin.musclebike.MainActivity;
import me.jonahchin.musclebike.R;

import static com.facebook.stetho.inspector.network.ResponseHandlingInputStream.TAG;
import static java.lang.Double.parseDouble;
import static me.jonahchin.musclebike.MainActivity.mAppDatabase;

/**
 * Created by jonahchin on 2017-10-16.
 * Fragment containing info that cyclist will see during ride
 */

public class RideFragment extends Fragment {
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRideRef = mRootRef.child("livetest");
    DatabaseReference mForceRef = mRideRef.child("muscle");
    DatabaseReference mCadenceRef = mRideRef.child("cadence");
    DatabaseReference mBalRef = mRideRef.child("balance");
    DatabaseReference mPyRef = mRootRef.child("pyStart");

    boolean buttonState;
    TextView mMuscleData;
    TextView mCadenceData;
    TextView mLeftBalData;
    TextView mRightBalData;
    TextView mSpeedData;
    TextView mDistanceData;
    int cadenceVal;
    int balanceVal;
    int muscleForce;
    boolean locationCheck;


    boolean riding;
    TextView stopWatch;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    int Seconds, Minutes, MilliSeconds, Hours;
    double initialLatitude, initialLongitude;
    double prevLat, prevLong, currLat, currLong;
    double distance_covered;

    private Ride mCurrentRide;
    private HistoryListCallbacks mCallbacks;

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


        mCurrentRide = new Ride();

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

        riding = true;

        buttonState = true;
        final FloatingActionButton fab = view.findViewById(R.id.fab);

        initializeLocation();
        if (riding == true) {
            handler = new Handler();
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            mCurrentRide.setRideId(System.currentTimeMillis());
            addNewRide();
            locationCallBacks();
            startLocationUpdates();
            setListeners();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                riding = false;
                TimeBuff += MillisecondTime;
                long total_elapsed_time = MillisecondTime;
                updateRide(total_elapsed_time, distance_covered);
                stopLocationUpdates();
                mPyRef.setValue(false);
                locationCheck = false;
                handler.removeCallbacksAndMessages(runnable);
                ((MainActivity) getActivity()).mBottomNav.setVisibility(View.VISIBLE);
                mCallbacks.onListItemClick(mCurrentRide);

            }
        });


        return view;
    }

    private void initializeLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                initialLatitude = location.getLatitude();
                initialLongitude = location.getLongitude();
                if (initialLatitude < 0 || initialLongitude > 0)
                    locationCheck = true;
                prevLat = initialLatitude;
                prevLong = initialLongitude;
                if (location != null) {
                    // Logic to handle location object
                }
            }
        });
    }

    private void locationCallBacks(){
        mLocationCallback = new LocationCallback() {
            DecimalFormat speedF = new DecimalFormat("#.#");
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    if (location.hasSpeed()) {
                        mSpeedData.setText(String.valueOf(speedF.format(location.getSpeed() * 3.6)));
                    }else{
                        mSpeedData.setText("0");
                    }
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
                if (riding) {
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
                muscleForce = dataSnapshot.getValue(Integer.class);
                String forceStr = String.valueOf(muscleForce);
                mMuscleData.setText(forceStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCadenceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cadenceVal = dataSnapshot.getValue(Integer.class);
                String cadenceStr = String.valueOf(cadenceVal);
                mCadenceData.setText(cadenceStr);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                balanceVal = dataSnapshot.getValue(Integer.class);
                String rightBalStr = String.valueOf(100-balanceVal);
                String balanceStr = String.valueOf(balanceVal);
                mLeftBalData.setText(balanceStr);
                mRightBalData.setText(rightBalStr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRideRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (riding == true)
                    addNewDataPoint();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void addNewRide(){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                RideDao dao = mAppDatabase.rideDao();

                dao.insertAll(mCurrentRide);


                return "Complete";
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
    }

    @SuppressLint("StaticFieldLeak")
    public void updateRide(final long time, final double distance){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                RideDao dao = mAppDatabase.rideDao();

                mCurrentRide.setElapsedTime(time);
                mCurrentRide.setDistance(distance);

                dao.updateRides(mCurrentRide);

                return "Complete";
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
    }

    @SuppressLint("StaticFieldLeak")
    public void addNewDataPoint(){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                RideDatapointDao dataDao = mAppDatabase.rideDatapointDao();
                RideDatapoint point = new RideDatapoint();
                if (locationCheck == true) {
                    point.setTimestamp(MillisecondTime);
                    point.setCadence(cadenceVal);
                    point.setMuscle(muscleForce);
                    point.setBalance(balanceVal);
                    point.setLat(currLat);
                    point.setLng(currLong);
                    point.setRideId(mCurrentRide.getRideId());

                    dataDao.insertAll(point);
                }

                return "Complete Data Point";
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallbacks = (HistoryListCallbacks) context;
        } catch (ClassCastException e) {
            Log.e(TAG, context.toString() + " must implement history");
        }
    }

}
