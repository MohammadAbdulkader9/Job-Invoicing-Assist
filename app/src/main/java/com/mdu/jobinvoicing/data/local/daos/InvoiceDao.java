package com.mdu.jobinvoicing.data.local.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.Invoice;

import java.util.List;

/**
 * DAO for Invoice entity.
 */
@Dao
public interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY createdAtMillis DESC")
    LiveData<List<Invoice>> getAllInvoices();

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    LiveData<Invoice> getInvoiceById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Invoice invoice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Invoice> invoices);

    @Update
    void update(Invoice invoice);

    @Delete
    void delete(Invoice invoice);
}
