package com.example.arne.translogistics_app.DAL;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.arne.translogistics_app.Model.DataRecording;
import com.example.arne.translogistics_app.Model.DataSegment;
import com.example.arne.translogistics_app.Model.Package;


@Database(entities = {DataRecording.class, DataSegment.class, Package.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {

    private static  AppDataBase INSTANCE;

    public abstract DataSegmentDAO dataSegmentModel();
    public abstract DataRecordingDAO dataRecordingModel();
    public abstract PackageDAO packageModel();

    public static AppDataBase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class,"TransLogicDeviceDB")
                            .allowMainThreadQueries().fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
