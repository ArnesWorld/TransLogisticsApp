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


}
