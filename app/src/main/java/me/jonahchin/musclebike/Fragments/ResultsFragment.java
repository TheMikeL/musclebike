package me.jonahchin.musclebike.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import me.jonahchin.musclebike.Entities.Ride;
import me.jonahchin.musclebike.R;

/**
 * Created by jonahchin on 2017-11-22.
 */

public class ResultsFragment extends Fragment implements OnMapReadyCallback {
    private String TAG = "ResultsFrag";
    TextView mTitle;

    private Ride mCurrentRide;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LineChart mLineChart;

    public static ResultsFragment newInstance(Ride ride) {
        
        Bundle args = new Bundle();

        ResultsFragment fragment = new ResultsFragment();
        fragment.setCurrentRide(ride);
        fragment.setArguments(args);

        return fragment;
    }
    
    private void setCurrentRide(Ride ride){
        mCurrentRide = ride;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        mTitle = view.findViewById(R.id.title_bar_title);
        mTitle.setText("Ride Details");


        initializeTable(view);
        initializeChart(view);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.map_fragment);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    private void initializeTable(View view) {
        TextView timeView = view.findViewById(R.id.time_data);
        TextView distanceView = view.findViewById(R.id.distance_data);
        TextView muscleUseView = view.findViewById(R.id.muscle_use_data);
        TextView PPMView = view.findViewById(R.id.ppm_data);
        final Button chartButton = view.findViewById(R.id.chart_button);

        timeView.setText(Long.toString(mCurrentRide.getEndTime()));
        distanceView.setText(Double.toString(mCurrentRide.getTotalDistance()));
        muscleUseView.setText(Long.toString(mCurrentRide.getStartTime()));
        PPMView.setText(mCurrentRide.getDate().toString());

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLineChart.getVisibility() == View.INVISIBLE){
                    mMapView.setVisibility(View.INVISIBLE);
                    mLineChart.setVisibility(View.VISIBLE);
                    chartButton.setText("Show Map");

                }else{
                    mMapView.setVisibility(View.VISIBLE);
                    mLineChart.setVisibility(View.INVISIBLE);
                    chartButton.setText("Show Muscle Chart");
                }

            }
        });
    }


    private void initializeChart(View view) {
        mLineChart = view.findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();

        entries.add(new Entry(15,3));
        entries.add(new Entry(19,10));

        LineDataSet dataSet = new LineDataSet(entries, "Your Ride");
        dataSet.setColor(R.color.colorPrimaryDark);
        LineData lineData = new LineData(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        
        mGoogleMap = googleMap;
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Start"));
    }


}
