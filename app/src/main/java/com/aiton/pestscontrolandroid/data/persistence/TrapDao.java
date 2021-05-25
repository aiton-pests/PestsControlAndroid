package com.aiton.pestscontrolandroid.data.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TrapDao {
    @Insert
    void insert(Trap... direcs);
    @Update
    void update(Trap... direcs);
    @Delete
    void delete(Trap... direcs);

    @Query("DELETE FROM TRAP")
    void deleteAll();

    @Query("DELETE FROM TRAP WHERE ID =:id")
    void deleteWithId(int id);

    @Query("SELECT * FROM TRAP WHERE UPDATE_SERVER =:update ORDER BY ID")
    Trap[] findAllObject(boolean update);

    @Query("SELECT * FROM TRAP")
    Trap[] findAllObject();

    @Query("SELECT * from TRAP where id = :id LIMIT 1")
    Trap findById(int id);

    @Query("SELECT * FROM TRAP WHERE UPDATE_SERVER =:update ORDER BY ID")
    LiveData<List<Trap>> findAll(boolean update);

    @Query("SELECT * FROM TRAP ORDER BY ID")
    LiveData<List<Trap>> findAll();

    @Query("SELECT * FROM TRAP WHERE USER_ID =:userId ORDER BY ID")
    LiveData<List<Trap>> findWithUserId(String userId);
}
