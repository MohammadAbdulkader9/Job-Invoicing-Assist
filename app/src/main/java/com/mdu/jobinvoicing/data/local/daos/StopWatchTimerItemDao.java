package com.mdu.jobinvoicing.data.local.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.StopWatchTimerItem;

import java.util.List;

@Dao
public interface StopWatchTimerItemDao {
    @Insert
    long insert(StopWatchTimerItem stopWatchTimerItem);

    @Update
    void update(StopWatchTimerItem stopWatchTimerItem);

    @Delete
    void delete(StopWatchTimerItem stopWatchTimerItem);

    @Query("DELETE FROM stop_watch_timer_items")
    void deleteAllTimerItems();

    @Query("SELECT * FROM stop_watch_timer_items ORDER BY id ASC")
    LiveData<List<StopWatchTimerItem>> getAllTimerItems();
}
