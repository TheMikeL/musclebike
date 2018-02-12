package me.jonahchin.musclebike.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.jonahchin.musclebike.Interfaces.HomePageCallbacks;
import me.jonahchin.musclebike.MainActivity;
import me.jonahchin.musclebike.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private HomePageCallbacks mCallbacks;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button start_button = view.findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).mBottomNav.setVisibility(View.GONE);
                mCallbacks.onStartButtonClick();

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallbacks = (HomePageCallbacks) context;
        } catch (ClassCastException e) {
            Log.e(TAG, context.toString() + " must implement HomeFragmentCallbacks");
        }
    }

}
