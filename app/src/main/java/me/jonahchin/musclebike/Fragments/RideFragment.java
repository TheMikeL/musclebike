package me.jonahchin.musclebike.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.jonahchin.musclebike.R;

/**
 * Created by jonahchin on 2017-10-16.
 * Fragment containing info that cyclist will see during ride
 */

public class RideFragment extends Fragment {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRideRef = mRootRef.child("live");
    DatabaseReference mForceRef = mRideRef.child("muscle-intensity");
    DatabaseReference mCadenceRef = mRideRef.child("cadence");
    DatabaseReference mLeftBalRef = mRideRef.child("muscle-left");
    DatabaseReference mRightBalRef = mRideRef.child("muscle-right");


    TextView mMuscleData;
    TextView mCadenceData;
    TextView mLeftBalData;
    TextView mRightBalData;
    TextView mSpeedData;
    TextView mDistanceData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride, container, false);
        mCadenceData = view.findViewById(R.id.cadence_value);
        mLeftBalData = view.findViewById(R.id.muscle_left);
        mRightBalData = view.findViewById(R.id.muscle_right);
        mMuscleData = view.findViewById(R.id.muscle_value);
        mDistanceData = view.findViewById(R.id.distance_elapsed);
        mSpeedData = view.findViewById(R.id.speed_value);

        mDistanceData.setText("10.1 km");
        mSpeedData.setText("5.1 km/h");


        setListeners();
        return view;
    }

    private void setListeners() {
        mForceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long force = dataSnapshot.getValue(Long.class);
                String forceStr = String.valueOf(force);
                mMuscleData.setText(forceStr + "%");
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
                mCadenceData.setText(cadenceStr + " RPM");
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
                mLeftBalData.setText(leftBalStr + "%");
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
                mRightBalData.setText(rightBalStr + "%");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
