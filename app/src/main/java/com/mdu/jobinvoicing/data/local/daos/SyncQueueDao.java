package com.mdu.jobinvoicing.data.local.daos;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.SyncQueue;

import java.util.List;

/**
 * DAO for SyncQueue. Methods return synchronous Lists for background workers.
 */
@Dao
public interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE state = :state ORDER BY queuedAtMillis ASC")
    List<SyncQueue> getByState(String state);

    @Query("SELECT * FROM sync_queue ORDER BY queuedAtMillis ASC")
    List<SyncQueue> getAll();

    @Query("SELECT * FROM sync_queue WHERE id = :id LIMIT 1")
    SyncQueue getSyncItemById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncQueue item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SyncQueue> items);

    @Update
    void update(SyncQueue item);

    @Query("DELETE FROM sync_queue WHERE state = :state")
    void deleteByState(String state);

    @Query("DELETE FROM sync_queue")
    void clearAll();
}
