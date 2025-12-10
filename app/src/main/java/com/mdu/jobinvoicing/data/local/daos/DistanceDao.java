package com.mdu.jobinvoicing.data.local.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.Distance;

import java.util.List;

@Dao
public interface DistanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Distance distance);

    @Update
    void update(Distance distance);

    @Delete
    void delete(Distance distance);

    @Query("SELECT * FROM distances WHERE workPlanId = :workPlanId")
    LiveData<List<Distance>> getDistancesForWorkPlan(String workPlanId);

}
