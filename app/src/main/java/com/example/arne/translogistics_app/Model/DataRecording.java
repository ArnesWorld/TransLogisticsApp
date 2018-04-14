package com.example.arne.translogistics_app.Model;



import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.example.arne.translogistics_app.DAL.DateConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@TypeConverters(DateConverter.class)
public class DataRecording implements Serializable {
    static final long serialVersionUID = 123456789123456789L;
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "rec_intervals")
    private int recordingIntervals;
    @ColumnInfo(name = "max_shock_limit")
    private int maxShockLimit;
    @ColumnInfo(name = "start_time")
    private Date startTime;
    @ColumnInfo(name = "end_time")
    private Date endTime;
    @ColumnInfo(name = "package_id")
    private int packageId;

    @Ignore
    public Package pack;

    @Ignore
    public List<DataSegment> dataSegments;

    public DataRecording(){}

    public DataRecording(int recordingIntervals, int maxShockLimit, Date startTime, Date endTime, int packageId) {
        this.recordingIntervals = recordingIntervals;
        this.maxShockLimit = maxShockLimit;
        this.startTime = startTime;
        this.endTime = endTime;
        this.packageId = packageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecordingIntervals() {
        return recordingIntervals;
    }

    public void setRecordingIntervals(int recordingIntervals) {
        this.recordingIntervals = recordingIntervals;
    }

    public int getMaxShockLimit() {
        return maxShockLimit;
    }

    public void setMaxShockLimit(int maxShockLimit) {
        this.maxShockLimit = maxShockLimit;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(this);
        return b.toByteArray();
    }
}
