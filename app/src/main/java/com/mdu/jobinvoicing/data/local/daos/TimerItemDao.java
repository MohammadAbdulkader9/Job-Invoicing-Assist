package com.mdu.jobinvoicing.data.local.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.TimerItem;

import java.util.List;

@Dao
public interface TimerItemDao {
    @Insert
    void insert(TimerItem timerItem);

    @Update
    void update(TimerItem timerItem);

    @Delete
    void delete(TimerItem timerItem);

    @Query("DELETE FROM timer_items")
    void deleteAllTimerItems();

    @Query("SELECT * FROM timer_items ORDER BY id ASC")
    LiveData<List<TimerItem>> getAllTimerItems();
}
