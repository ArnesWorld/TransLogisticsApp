package com.example.arne.translogistics_app.Model;




import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.example.arne.translogistics_app.DAL.DateConverter;

import java.util.Date;

/**
 * Created by Arne on 11-03-2018.
 */
@Entity(foreignKeys = {
        @ForeignKey(entity = DataRecording.class,
                parentColumns = "id",
                childColumns = "data_recording_id")})

@TypeConverters(DateConverter.class)
public class DataSegment {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "time_stamp")
    private Date timeStamp;

    private double latitude;
    private double longitude;

    @ColumnInfo(name = "max_shock")
    private float maxShock;

    @ColumnInfo(name = "shocks_over_limit")
    private int shocksOverLimit;

    @ColumnInfo(name = "data_recording_id")
    private int dataRecordingId;


    public DataSegment() {
    }

    public DataSegment(Date timeStamp, double latitude, double longtitude, float maxShock, int shocksOverLimit, int dataRecordingId) {
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longtitude;
        this.maxShock = maxShock;
        this.shocksOverLimit = shocksOverLimit;
        this.dataRecordingId = dataRecordingId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }


    public float getMaxShock() {
        return maxShock;
    }

    public void setMaxShock(float maxShock) {
        this.maxShock = maxShock;
    }

    public int getShocksOverLimit() {
        return shocksOverLimit;
    }

    public void setShocksOverLimit(int shocksOverLimit) {
        this.shocksOverLimit = shocksOverLimit;
    }

    public int getDataRecordingId() {
        return dataRecordingId;
    }

    public void setDataRecordingId(int dataRecordingId) {
        this.dataRecordingId = dataRecordingId;
    }

    @Override
    public String toString() {
        return "time: " + timeStamp + " maxShockValue: " + maxShock + " Number of shocks over limit: " + shocksOverLimit + "\n" +
                "LOCATION: LAT: " + latitude + " LONG: " + longitude;

    }
}