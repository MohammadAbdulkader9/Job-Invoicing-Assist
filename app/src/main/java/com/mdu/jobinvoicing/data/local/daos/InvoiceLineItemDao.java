package com.mdu.jobinvoicing.data.local.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mdu.jobinvoicing.data.local.entities.InvoiceLineItem;

import java.util.List;

/**
 * DAO for InvoiceLineItem entity.
 */
@Dao
public interface InvoiceLineItemDao {
    @Query("SELECT * FROM invoice_line_items WHERE invoiceId = :invoiceId ORDER BY id")
    LiveData<List<InvoiceLineItem>> getForInvoice(String invoiceId);

    @Query("SELECT * FROM invoice_line_items WHERE id = :id LIMIT 1")
    LiveData<InvoiceLineItem> getInvoiceLineItemById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(InvoiceLineItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<InvoiceLineItem> items);

    @Update
    void update(InvoiceLineItem item);

    @Delete
    void delete(InvoiceLineItem item);
}
