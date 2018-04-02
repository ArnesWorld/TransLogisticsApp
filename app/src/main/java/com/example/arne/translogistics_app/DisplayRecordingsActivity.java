package com.example.arne.translogistics_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;


import com.example.arne.translogistics_app.DAL.AppDataBase;
import com.example.arne.translogistics_app.Model.DataRecording;
import com.example.arne.translogistics_app.Model.DataSegment;
import com.example.arne.translogistics_app.Model.Package;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DisplayRecordingsActivity extends AppCompatActivity {

    private AppDataBase db;
    private ListView listView;
    private MyDataRecAdapter myDataRecAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recordings);
        setTitle("Data Recordings");
        db = AppDataBase.getInstance(getApplicationContext());
        LoadDummyData();

        ArrayList<DataRecording> dataRecordings = (ArrayList<DataRecording>) db.dataRecordingModel().getAllDataRecordings();
        loadPackageObjects(dataRecordings);
        listView = findViewById(R.id.listView);
        myDataRecAdapter = new MyDataRecAdapter(getApplicationContext(),R.layout.datarec_list_item, dataRecordings);
        listView.setAdapter(myDataRecAdapter);
    }

    private void loadPackageObjects(ArrayList<DataRecording> dataRecordings) {
        for (DataRecording dr: dataRecordings ) {
            dr.pack = db.packageModel().getPackageById(dr.getPackageId());
        }
    }

    private void LoadDummyData() {
        db.dataSegmentModel().deleteAll();
        db.dataRecordingModel().deleteAll();
        db.packageModel().deleteAll();

        Calendar cal = Calendar.getInstance();
        Date ts0 = cal.getTime();
        cal.add(Calendar.SECOND, 30);
        Date ts1 = cal.getTime();
        cal.add(Calendar.SECOND, 30);
        Date ts2 = cal.getTime();
        cal.add(Calendar.SECOND, 30);
        Date ts3 = cal.getTime();
        cal.add(Calendar.SECOND, 30);
        Date ts4 = cal.getTime();
        cal.add(Calendar.SECOND, 30);
        Date ts5 = cal.getTime();

        Package pack = new Package("Tomatoes", 500, "FreshFruit A/S", "New York", "New York", "UPS");
        int packId = (int)db.packageModel().insertPackage(pack);

        DataRecording dr1 = new DataRecording(30, 3000, ts0, ts5, packId);
        int drId = (int)db.dataRecordingModel().insertDataRecording(dr1);

        DataSegment ds1 = new DataSegment(ts0,40.71217148, -74.01005366, 3560,1,drId);
        db.dataSegmentModel().insertDataSegment(ds1);
        DataSegment ds2 = new DataSegment(ts1,40.71374915, -74.00874474, 2345,0,drId);
        db.dataSegmentModel().insertDataSegment(ds2);
        DataSegment ds3 = new DataSegment(ts2,40.71287086, -74.00713542, 5400,3,drId);
        db.dataSegmentModel().insertDataSegment(ds3);
        DataSegment ds4 = new DataSegment(ts3,40.71207389, -74.00559046, 2890,0,drId);
        db.dataSegmentModel().insertDataSegment(ds4);
        DataSegment ds5 = new DataSegment(ts4,40.71357024, -74.00353053, 7000,5,drId);
        db.dataSegmentModel().insertDataSegment(ds5);
        DataSegment ds6 = new DataSegment(ts5,40.7152292, -74.00217162, 2600,0,drId);
        db.dataSegmentModel().insertDataSegment(ds6);
    }
}
