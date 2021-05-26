package com.aiton.pestscontrolandroid.data.persistence;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface PestsDao {
    @Insert
    void insert(Pests... direcs);
    @Update
    void update(Pests... direcs);
    @Delete
    void delete(Pests... direcs);

    @Query("DELETE FROM PESTS")
    void deleteAll();

    @Query("DELETE FROM PESTS WHERE ID =:id")
    void deleteWithId(int id);

    @Query("SELECT * FROM PESTS WHERE UPDATE_SERVER =:update ORDER BY ID")
    Pests[] findAllObject(boolean update);

    @Query("SELECT * FROM PESTS")
    Pests[] findAllObject();

    @Query("SELECT * from PESTS where id = :id LIMIT 1")
    Pests findById(int id);

    @Query("SELECT * from PESTS where LATITUDE = :lat AND LONGITUDE= :lon AND STIME= :stime AND USER_ID= :userId AND QRCODE= :qrcode LIMIT 1")
    Pests findByLatLonAndUserIdAndStime(Double lat,Double lon, String stime,String userId,String qrcode);

    @Query("SELECT * FROM PESTS WHERE UPDATE_SERVER =:update ORDER BY ID")
    LiveData<List<Pests>> findAll(boolean update);

    @Query("SELECT * FROM PESTS ORDER BY ID")
    LiveData<List<Pests>> findAll();

    @Query("SELECT * FROM PESTS WHERE USER_ID =:userId ORDER BY ID")
    LiveData<List<Pests>> findWithUserId(String userId);
}
