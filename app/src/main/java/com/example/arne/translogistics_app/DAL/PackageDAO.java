package com.example.arne.translogistics_app.DAL;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.example.arne.translogistics_app.Model.Package;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;


@Dao
@TypeConverters(DateConverter.class)
public interface PackageDAO {

    @Query("SELECT * FROM Package")
    List<Package> getAllPackages();

    @Query("SELECT * FROM Package WHERE id = :id")
    Package getPackageById(int id);

    @Query("SELECT id FROM datasegment ORDER BY id DESC LIMIT 1;")
    int getLastId();

    @Insert(onConflict = IGNORE)
    long insertPackage(Package p);

    @Query("DELETE FROM Package")
    void deleteAll();


}