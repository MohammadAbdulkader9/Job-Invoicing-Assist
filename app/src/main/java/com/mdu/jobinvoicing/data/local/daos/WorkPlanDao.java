package com.mdu.jobinvoicing.data.local.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.WorkPlan;

import java.util.List;

/**
 * DAO for WorkPlan entity.
 */
@Dao
public interface WorkPlanDao {
    @Query("SELECT * FROM work_plans ORDER BY startDate DESC")
    LiveData<List<WorkPlan>> getAllWorkPlans();

    @Query("SELECT * FROM work_plans WHERE id = :id LIMIT 1")
    LiveData<WorkPlan> getWorkPlanById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WorkPlan workPlan);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WorkPlan> plans);

    @Update
    void update(WorkPlan workPlan);

    @Delete
    void delete(WorkPlan workPlan);
}
