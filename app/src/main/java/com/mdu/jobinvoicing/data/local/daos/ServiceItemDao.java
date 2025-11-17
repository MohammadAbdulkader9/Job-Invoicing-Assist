package com.mdu.jobinvoicing.data.local.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.ServiceItem;

import java.util.List;

/**
 * DAO for ServiceItem entity.
 */
@Dao
public interface ServiceItemDao {
    @Query("SELECT * FROM service_items WHERE workPlanId = :workPlanId ORDER BY id")
    LiveData<List<ServiceItem>> getForWorkPlan(String workPlanId);

    @Query("SELECT * FROM service_items WHERE id = :id LIMIT 1")
    LiveData<ServiceItem> getServiceItemById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ServiceItem serviceItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ServiceItem> items);

    @Update
    void update(ServiceItem serviceItem);

    @Delete
    void delete(ServiceItem serviceItem);
}
