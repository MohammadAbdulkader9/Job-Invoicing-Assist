package com.mdu.jobinvoicing.data.local.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.Operator;

import java.util.List;

/**
 * DAO for managing Operator data.
 */
@Dao
public interface OperatorDao {

    @Query("SELECT * FROM operators ORDER BY name ASC")
    LiveData<List<Operator>> getAllOperators();

    @Query("SELECT * FROM operators WHERE id = :id LIMIT 1")
    LiveData<Operator> getOperatorById(String id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Operator operator);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Operator> operators);

    @Update
    void update(Operator operator);

    @Delete
    void delete(Operator operator);
}
