package com.example.arne.translogistics_app.DAL;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;


import com.example.arne.translogistics_app.Model.DataSegment;

import java.util.Date;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;


@Dao
@TypeConverters(DateConverter.class)
public interface DataSegmentDAO{

    @Query("SELECT * FROM DataSegment")
    List<DataSegment> getAllDataSegments();

    @Query("SELECT * FROM DataSegment WHERE time_stamp = :timeStamp")
    DataSegment getDataSegmentByTimeStamp(Date timeStamp);

    @Query("SELECT * FROM DataSegment WHERE id = :id")
    DataSegment getDataSegmentById(int id);

    @Query("SELECT * FROM DataSegment WHERE data_recording_id = :id")
    List<DataSegment> getDataSegmentByRecId(int id);

    @Insert(onConflict = IGNORE)
    void insertDataSegment(DataSegment dataSegment);

    @Query("DELETE FROM DataSegment")
    void deleteAll();



}