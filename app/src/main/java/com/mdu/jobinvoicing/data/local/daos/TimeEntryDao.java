package com.mdu.jobinvoicing.data.local.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.TimeEntry;

import java.util.List;

/**
 * DAO for TimeEntry entity.
 */
@Dao
public interface TimeEntryDao {
    @Query("SELECT * FROM time_entries WHERE workPlanId = :workPlanId")
    LiveData<List<TimeEntry>> getForWorkPlan(String workPlanId);

    @Query("SELECT * FROM time_entries WHERE id = :id LIMIT 1")
    LiveData<TimeEntry> getTimeEntryById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimeEntry entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TimeEntry> entries);

    @Update
    void update(TimeEntry entry);

    @Delete
    void delete(TimeEntry entry);
}