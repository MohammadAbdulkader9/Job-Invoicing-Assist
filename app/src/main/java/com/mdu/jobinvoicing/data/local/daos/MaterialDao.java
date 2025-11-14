package com.mdu.jobinvoicing.data.local.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.Material;

import java.util.List;

/**
 * DAO for Material entity.
 */
@Dao
public interface MaterialDao {
    @Query("SELECT * FROM materials WHERE workPlanId = :workPlanId ORDER BY id")
    LiveData<List<Material>> getForWorkPlan(String workPlanId);

    @Query("SELECT * FROM materials WHERE id = :id LIMIT 1")
    LiveData<Material> getMaterialById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Material material);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Material> materials);

    @Update
    void update(Material material);

    @Delete
    void delete(Material material);
}
