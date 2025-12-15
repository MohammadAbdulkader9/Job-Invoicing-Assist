package com.mdu.jobinvoicing.data.local.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.Weight;

import java.util.List;

@Dao
public interface WeightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Weight weight);

    @Update
    void update(Weight weight);

    @Delete
    void delete(Weight weight);

    @Query("SELECT * FROM weights WHERE workPlanId = :workPlanId")
    LiveData<List<Weight>> getWeightsForWorkPlan(String workPlanId);

}
