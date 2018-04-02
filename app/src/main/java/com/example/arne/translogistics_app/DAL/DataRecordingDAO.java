package com.example.arne.translogistics_app.DAL;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;


import com.example.arne.translogistics_app.Model.DataRecording;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
@TypeConverters(DateConverter.class)
public interface DataRecordingDAO {

    @Query("SELECT * FROM DataRecording")
    List<DataRecording> getAllDataRecordings();

    @Query("SELECT * FROM DataRecording WHERE id = :id")
    DataRecording getDataRecordingById(int id);

    @Update(onConflict = REPLACE)
    void updateDataRecording(DataRecording dataRecording);

    @Insert(onConflict = IGNORE)
    long insertDataRecording(DataRecording dataRecording);

    @Query("DELETE FROM DataRecording")
    void deleteAll();



}
