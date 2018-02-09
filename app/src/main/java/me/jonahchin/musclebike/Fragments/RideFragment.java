package me.jonahchin.musclebike.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
    DatabaseReference mForceRef = mRootRef.child("force");

    EditText mForceData;
    Button mUpdateButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride, container, false);
//        mForceData = view.findViewById(R.id.force_data);
//        mUpdateButton = view.findViewById(R.id.update_button);
//        setListeners();

        return view;
    }

    private void setListeners() {
        mForceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String force = dataSnapshot.getValue(String.class);
                mForceData.setText(force);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForceRef.setValue(mForceData.getText().toString());
            }
        });


    }
}
