package me.jonahchin.musclebike.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.jonahchin.musclebike.Entities.Ride;
import me.jonahchin.musclebike.Interfaces.RideDao;
import me.jonahchin.musclebike.Interfaces.RideDatapointDao;
import me.jonahchin.musclebike.MainActivity;
import me.jonahchin.musclebike.R;
import me.jonahchin.musclebike.Utility.StringUtil;

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
    private PieChart mZonePie;
    private PieChart mBalancePie;

    //stats card
    private TextView mDistanceText;
    private TextView mTimeText;
    private TextView mCadenceText;
    private TextView mSpeedText;

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
        View view = inflater.inflate(R.layout.fragment_results_scroll, container, false);

        mTitle = view.findViewById(R.id.title_bar_title);

        mTitle.setText(StringUtil.getTitleDateFromMillis(mCurrentRide.getRideId()));

        mMapView = view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        initializeStatsCard(view);
        initializeChart(view);
        initializePieCharts(view);

        return view;
    }

    private void initializeStatsCard(View view) {

        mDistanceText = view.findViewById(R.id.distance_data_text);
        mTimeText = view.findViewById(R.id.time_data_text);
        mCadenceText = view.findViewById(R.id.cadence_data_text);
        mSpeedText = view.findViewById(R.id.speed_data_text);



        mDistanceText.setText(String.format("%.2fkm", mCurrentRide.getDistance()));
        mTimeText.setText(StringUtil.getHourMinuteSecondFromMillis(mCurrentRide.getElapsedTime()));

        double speed = mCurrentRide.getDistance() / (mCurrentRide.getElapsedTime() * 2.77778e-7);
        mSpeedText.setText(String.format("%.1fkmh", speed));


        new AsyncTask<Void, Void, Void>() {
            final RideDatapointDao dataDao = MainActivity.mAppDatabase.rideDatapointDao();
            double avgCadence = 0;

            @Override
            protected Void doInBackground(Void... params) {
                avgCadence = dataDao.getAverageCadence(mCurrentRide.getRideId());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mCadenceText.setText(String.format("%.2f", avgCadence));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void[]) null);
    }

    private void initializePieCharts(View view) {
        mZonePie = view.findViewById(R.id.zone_pie);
        mBalancePie = view.findViewById(R.id.balance_pie);

        mZonePie.setHoleRadius(90);
        mBalancePie.setHoleRadius(90);

        mZonePie.getLegend().setEnabled(false);
        mBalancePie.getLegend().setEnabled(false);

        mZonePie.getDescription().setText("Intensity Breakdown");
        mBalancePie.getDescription().setText("Balance");

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(18.5f, ""));
        entries.add(new PieEntry(26.7f, ""));
        entries.add(new PieEntry(24.0f, ""));
        entries.add(new PieEntry(30.8f, ""));

        PieDataSet set = new PieDataSet(entries, "");

        set.setColors(new int[] { android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_light }, getContext());

        PieData data = new PieData(set);
        mZonePie.setData(data);
        mBalancePie.setData(data);

        mZonePie.invalidate();
        mBalancePie.invalidate();
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
//        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Start"));

        addPathToMap();


    }

    private void addPathToMap() {

        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(44.227957, -76.491584)).title("Start"));
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(44.229661, -76.502837)).title("End"));
        PolylineOptions pathOptions = new PolylineOptions()
                .add(new LatLng(44.227957, -76.491584))
                .add(new LatLng(44.228195, -76.499871))
                .add(new LatLng(44.229661, -76.502837)).color(Color.RED);

        Polyline path = mGoogleMap.addPolyline(pathOptions);

        LatLngBounds AUSTRALIA = new LatLngBounds(
                new LatLng(44.225474, -76.503977), new LatLng(44.233160, -76.490130));


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA , 15));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }


}
