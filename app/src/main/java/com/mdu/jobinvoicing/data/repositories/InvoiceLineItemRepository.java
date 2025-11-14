package com.mdu.jobinvoicing.data.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.InvoiceLineItemDao;
import com.mdu.jobinvoicing.data.local.entities.InvoiceLineItem;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InvoiceLineItemRepository {
    private final InvoiceLineItemDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public InvoiceLineItemRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.invoiceLineItemDao();
        syncQueueRepository = new SyncQueueRepository(ctx);
    }

    public LiveData<List<InvoiceLineItem>> getForInvoice(String invoiceId) {
        return dao.getForInvoice(invoiceId);
    }

    public LiveData<InvoiceLineItem> getInvoiceLineItemById(String id) {
        return dao.getInvoiceLineItemById(id);
    }

    public void insert(final InvoiceLineItem item) {
        insert(item, false);
    }

    public void insert(final InvoiceLineItem item, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(item);
            if (enqueueSync) {
                enqueueSync(item.id, gson.toJson(item), "CREATE");
            }
        });
    }

    public void update(final InvoiceLineItem item) {
        update(item, false);
    }

    public void update(final InvoiceLineItem item, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(item);
            if (enqueueSync) {
                enqueueSync(item.id, gson.toJson(item), "UPDATE");
            }
        });
    }

    public void delete(final InvoiceLineItem item) {
        delete(item, false);
    }

    public void delete(final InvoiceLineItem item, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(item);
            if (enqueueSync) {
                enqueueSync(item.id, gson.toJson(item), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "InvoiceLineItem", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
