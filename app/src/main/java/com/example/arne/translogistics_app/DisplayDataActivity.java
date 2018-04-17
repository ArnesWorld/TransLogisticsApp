package com.example.arne.translogistics_app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arne.translogistics_app.DAL.AppDataBase;
import com.example.arne.translogistics_app.Model.DataRecording;
import com.example.arne.translogistics_app.Model.DataSegment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DisplayDataActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private GraphView graphMaxShock;
    private GraphView graphShocksOverLimit;
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    private GoogleMap map;
    private String lastCharInTime = "";
    private Marker currMarker;
    private ArrayList<DataSegment> dataSegments;
    private AppDataBase db;
    private Geocoder geoCoder;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        geoCoder = new Geocoder(this, Locale.ENGLISH);
        db = AppDataBase.getInstance(getApplicationContext());
        int recId = getIntent().getIntExtra("recId", 0);
        setTitle("Display Data-Recording id: " + recId);

        //Get datasegments from database
        dataSegments = (ArrayList<DataSegment>) db.dataSegmentModel().getDataSegmentByRecId(recId);
        //Get DataRecording from database
        DataRecording dataRecording = db.dataRecordingModel().getDataRecordingById(recId);
        //Initialize graphs
        graphMaxShock = findViewById(R.id.graphMaxShock);
        graphShocksOverLimit = findViewById(R.id.graphShocksOverLimit);
        //Initialize Data series
        BarGraphSeries<DataPoint> shocksOverLimitSeries = new BarGraphSeries<>();
        LineGraphSeries<DataPoint> maxShockValueSeries = new LineGraphSeries<>();

        //Make limit line

        int shockLimit = dataRecording.getMaxShockLimit();

        LineGraphSeries<DataPoint> shockValueLimitLineSeries = new LineGraphSeries<>();
        //Load data into series from dataSegment
        for (DataSegment d : dataSegments) {
            DataPoint shocksOverLimit = new DataPoint(d.getTimeStamp(), d.getShocksOverLimit());
            DataPoint maxShock = new DataPoint(d.getTimeStamp(), d.getMaxShock());
            DataPoint limitLine = new DataPoint(d.getTimeStamp(), shockLimit);

            shocksOverLimitSeries.appendData(shocksOverLimit, true, 100);
            maxShockValueSeries.appendData(maxShock, true, 100);
            shockValueLimitLineSeries.appendData(limitLine, true, 100);
        }

        //Set up tap listeners
        setupTapListener(maxShockValueSeries);
        setupTapListener(shocksOverLimitSeries);
        //Set titles
        graphMaxShock.setTitle("Max-Shock Values");
        graphShocksOverLimit.setTitle("Number of Shocks Over Limit");
        //Set up graphs to display as desired
        setUpGraph(graphMaxShock, dataSegments);
        setUpGraph(graphShocksOverLimit, dataSegments);
        //Add series to graphs
        graphShocksOverLimit.addSeries(shocksOverLimitSeries);
        graphMaxShock.addSeries(maxShockValueSeries);
        graphMaxShock.addSeries(shockValueLimitLineSeries);

        //Make limit-line pretty
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setARGB(124, 229,82,165);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        shockValueLimitLineSeries.setDrawAsPath(true);
        shockValueLimitLineSeries.setCustomPaint(paint);

        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        List<LatLng> coordinates = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions().clickable(true);
        for (DataSegment d: dataSegments) {
            LatLng currPos = new LatLng(d.getLatitude(),d.getLongitude());
            coordinates.add(currPos);
            polylineOptions.add(currPos);
        }
        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        Polyline polyline1 = googleMap.addPolyline(polylineOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(0),14));

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    private void setupTapListener(Series<DataPoint> series) {
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                LatLng locationPoint = findLocationByTimeStamp(dataPoint.getX());
                SetMarkerOnMap(locationPoint, dataPoint.getY(), dataPoint.getX());
            }
        });
    }

    private void SetMarkerOnMap(LatLng locationPoint, double yValue, double xValue) {
        if(currMarker != null){
            currMarker.remove();
        }

        currMarker = map.addMarker(new MarkerOptions().position(new LatLng(locationPoint.latitude, locationPoint.longitude))
                .title("Value: " + yValue ).snippet("Time: " + formatter.format(xValue) + "\n" + getAddressFromLocation(locationPoint)));
    }

    private LatLng findLocationByTimeStamp(double x) {
        LatLng res = null;
        for (DataSegment d: dataSegments) {
            double currTime = d.getTimeStamp().getTime();
            if (x == currTime){
                res = new LatLng(d.getLatitude(),d.getLongitude());
            }
        }
        return res;
    }

    private void setUpGraph(GraphView graph, ArrayList<DataSegment> dataSegments) {

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX){
                    Date date = new Date((long)value);
                    String time = formatter.format(date);
                    String lastChar = time.substring(time.length()-1, time.length());
                    if(!lastChar.equals(lastCharInTime))
                    {
                        lastCharInTime = lastChar;
                        return time;
                    }
                    else{return "";}
                }
                else{return super.formatLabel(value, isValueX);}
            }
        });


        graph.getViewport().setScalable(true);
        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(dataSegments.get(0).getTimeStamp().getTime());
        graph.getViewport().setMaxX(dataSegments.get(dataSegments.size() - 1).getTimeStamp().getTime());
        graph.getViewport().setXAxisBoundsManual(true);

    }

    private String getAddressFromLocation(LatLng latLng) {

        Address addresse = null;
        try {
            addresse = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder mSB = new StringBuilder("Address:\n");
        int j = addresse.getMaxAddressLineIndex();
        if (j >= 0){
            for (int i = 0; i <= j; i++) {
                mSB.append(addresse.getAddressLine(i)).append("\n");
            }
        }
        return mSB.toString();
    }


}

