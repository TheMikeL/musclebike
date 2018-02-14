package me.jonahchin.musclebike.Fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import me.jonahchin.musclebike.Entities.Coordinates;
import me.jonahchin.musclebike.Entities.LinePoint;
import me.jonahchin.musclebike.Entities.Ride;
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
    private PieChart mIntensityPie;
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

        mDistanceText.setText(String.format("%.1f", mCurrentRide.getDistance()));
        mTimeText.setText(StringUtil.getHourMinuteSecondFromMillis(mCurrentRide.getElapsedTime()));

        double speed = mCurrentRide.getDistance() / (mCurrentRide.getElapsedTime() * 2.77778e-7);
        mSpeedText.setText(String.format("%.1f", speed));


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
                mCadenceText.setText(String.format("%.1f", avgCadence));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(Void[]) null);
    }

    private void initializePieCharts(View view) {
        mIntensityPie = view.findViewById(R.id.zone_pie);
        mBalancePie = view.findViewById(R.id.balance_pie);

        mBalancePie.setTouchEnabled(false);
        mIntensityPie.setTouchEnabled(false);

        mIntensityPie.setUsePercentValues(true);
        mBalancePie.setUsePercentValues(true);

        mBalancePie.setDrawSliceText(false);
        mIntensityPie.setDrawSliceText(false);

        mIntensityPie.setCenterText("Intensity");
        mBalancePie.setCenterText("Balance");

        mIntensityPie.setHoleRadius(70);
        mBalancePie.setHoleRadius(70);

        mIntensityPie.getDescription().setEnabled(false);
        mBalancePie.getDescription().setEnabled(false);

        new AsyncTask<Void, Void, String>() {
            PieData balanceData;
            PieData intensityData;
            @Override
            protected String doInBackground(Void... params) {
                RideDatapointDao dataDao = MainActivity.mAppDatabase.rideDatapointDao();

                List<PieEntry> balanceEntries = new ArrayList<>();

                float left = dataDao.getAverageBalance(mCurrentRide.getRideId());

                balanceEntries.add(new PieEntry(100.0f - left, "Right"));
                balanceEntries.add(new PieEntry(left, "Left"));

                List<PieEntry> intensityEntries = new ArrayList<>();

                int low = dataDao.getNumLowIntensities(mCurrentRide.getRideId());
                int med = dataDao.getNumMedIntensities(mCurrentRide.getRideId());
                int high = dataDao.getNumHighIntensities(mCurrentRide.getRideId());
                int total = low + med + high;

                float lowPercent = ((float) low / (float) total) * 100;
                float medPercent = ((float) med / (float) total) * 100;
                float highPercent = ((float) high / (float) total) * 100;

                intensityEntries.add(new PieEntry(lowPercent, "Low"));
                intensityEntries.add(new PieEntry(medPercent, "Medium"));
                intensityEntries.add(new PieEntry(highPercent, "High"));

                PieDataSet balanceSet = new PieDataSet(balanceEntries, "");
                PieDataSet intensitySet = new PieDataSet(intensityEntries, "");

                intensitySet.setSliceSpace(5);
                balanceSet.setSliceSpace(5);

                balanceSet.setColors(new int[] { R.color.colorPrimary, R.color.colorSecondary}, getContext());
                intensitySet.setColors(new int[] { R.color.colorPrimary, R.color.colorSecondary, R.color.colorTertiary}, getContext());

                balanceData = new PieData(balanceSet);
                intensityData = new PieData(intensitySet);

                balanceData.setValueTextColor(Color.WHITE);
                intensityData.setValueTextColor(Color.WHITE);
                balanceData.setValueFormatter(new PercentFormatter());
                intensityData.setValueFormatter(new PercentFormatter());

                return "Complete";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                mBalancePie.setData(balanceData);
                mIntensityPie.setData(intensityData);
                mBalancePie.invalidate();
                mIntensityPie.invalidate();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
    }


    private void initializeChart(View view) {
        mLineChart = view.findViewById(R.id.chart);
        mLineChart.setTouchEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.getDescription().setEnabled(false);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);


        /* ------ DEBUG ------- */
/*        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 10));
        entries.add(new Entry(700000, 25)); //15
        entries.add(new Entry(1000000, 50)); //20
        entries.add(new Entry(1300000, 50)); //30
        entries.add(new Entry(1600000, 70)); //35
        entries.add(new Entry(1900000, 70)); //43
        entries.add(new Entry(2300000, 20)); //50
        entries.add(new Entry(3180000, 20)); //53
        entries.add(new Entry(3300000, 10)); //53

        LineDataSet dataSet = new LineDataSet(entries, "Your Ride");
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setColor(R.color.colorPrimaryDark);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.08f);
        LineData lineData = new LineData(dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();*/

        /* ------ END DEBUG ------- */

        new AsyncTask<Void, Void, String>() {
            LineData lineData;
            boolean hasData;
            @Override
            protected String doInBackground(Void... params) {
                RideDatapointDao dataDao = MainActivity.mAppDatabase.rideDatapointDao();
                List<Entry> entries = new ArrayList<>();

                List<LinePoint> points = dataDao.getChartData(mCurrentRide.getRideId());

                for(LinePoint point : points){
                    entries.add(new Entry((float)point.timestamp, point.muscle));
                }
                hasData = false;
                if(entries.size() > 0){
                    hasData = true;
                    LineDataSet dataSet = new LineDataSet(entries, "Your Ride");
                    dataSet.setDrawCircles(false);
                    dataSet.setDrawValues(false);
                    dataSet.setColor(R.color.colorPrimaryDark);
                    lineData = new LineData(dataSet);
                }
                return "Complete";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(hasData){
                    mLineChart.setData(lineData);
                    mLineChart.invalidate();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
//        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);

        /* ------ DEBUG ------- */

        /*MarkerOptions markerOptionsStart = new MarkerOptions();
        MarkerOptions markerOptionsEnd = new MarkerOptions();
        markerOptionsStart.position(new LatLng(44.229, -76.505)).title("Start");
        markerOptionsEnd.position(new LatLng(44.231249, -76.508709)).title("End");
        PolylineOptions options = new PolylineOptions()
                .add(new LatLng(44.229, -76.505))
                .add(new LatLng(44.231437, -76.504998))
                .add(new LatLng(44.231693, -76.495769))
                .add(new LatLng(44.232637, -76.495790))
                .add(new LatLng(44.232637, -76.495790))
                .add(new LatLng(44.233959, -76.498933))
                .add(new LatLng(44.235890, -76.499100))
                .add(new LatLng(44.238750, -76.505895))
                .add(new LatLng(44.233980, -76.505299))
                .add(new LatLng(44.233842, -76.508979))
                .add(new LatLng(44.231249, -76.508709))
                .color(Color.BLUE);

        mGoogleMap.addMarker(markerOptionsStart);
        mGoogleMap.addMarker(markerOptionsEnd);
        mGoogleMap.addPolyline(options);

        LatLngBounds BOUNDS = new LatLngBounds(new LatLng(44.225458, -76.508012), new LatLng(44.238648, -76.487929));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(BOUNDS, 15));*/

        /* ------ END DEBUG ------- */

        new AsyncTask<Void, Void, String>() {
            LatLngBounds BOUNDS;
            PolylineOptions options;
            MarkerOptions markerOptionsStart;
            MarkerOptions markerOptionsEnd;
            boolean hasData;

            @Override
            protected String doInBackground(Void... params) {
                RideDatapointDao dataDao = MainActivity.mAppDatabase.rideDatapointDao();

                long rideId = mCurrentRide.getRideId();

                double minLat = dataDao.getMinLat(rideId);
                double minLng = dataDao.getMinLong(rideId);
                double maxLat = dataDao.getMaxLat(rideId);
                double maxLng = dataDao.getMaxLong(rideId);
                double deltaLat = 0.2 * (maxLat - minLat);
                double deltaLng = 0.1 * (maxLng - minLng);

                BOUNDS = new LatLngBounds(
                        new LatLng(minLat, minLng - deltaLng), new LatLng(maxLat + deltaLat, maxLng + deltaLng));

                options = new PolylineOptions();
                markerOptionsStart = new MarkerOptions();
                markerOptionsEnd = new MarkerOptions();

                List<Coordinates> coords = dataDao.getAllCoordinates(mCurrentRide.getRideId());
                hasData = false;
                if(coords.size() > 0){
                    hasData = true;
                    markerOptionsStart.position(new LatLng(coords.get(0).lat, coords.get(0).lng)).title("Start");
                    markerOptionsEnd.position(new LatLng(coords.get(coords.size() - 1).lat, coords.get(coords.size() - 1).lng)).title("End");

                    for (Coordinates coord : coords) {
                        options.add(new LatLng(coord.lat, coord.lng));
                    }

                    options.color(Color.BLUE);
                }

                return "Complete";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(hasData) {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(BOUNDS, 15));
                    mGoogleMap.addPolyline(options);
                    mGoogleMap.addMarker(markerOptionsStart);
                    mGoogleMap.addMarker(markerOptionsEnd);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);

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

    public class MyXAxisValueFormatter implements IAxisValueFormatter {


        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)

             return StringUtil.getMinutesFromMillis(value);
        }

    }




}
