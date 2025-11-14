package com.mdu.jobinvoicing.data.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.InvoiceDao;
import com.mdu.jobinvoicing.data.local.entities.Invoice;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InvoiceRepository {
    private final InvoiceDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public InvoiceRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.invoiceDao();
        syncQueueRepository = new SyncQueueRepository(ctx);
    }

    public LiveData<List<Invoice>> getAllInvoices() {
        return dao.getAllInvoices();
    }

    public LiveData<Invoice> getInvoiceById(String id) {
        return dao.getInvoiceById(id);
    }

    public void insert(final Invoice invoice) {
        insert(invoice, false);
    }

    public void insert(final Invoice invoice, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(invoice);
            if (enqueueSync) {
                enqueueSync(invoice.id, gson.toJson(invoice), "CREATE");
            }
        });
    }

    public void update(final Invoice invoice) {
        update(invoice, false);
    }

    public void update(final Invoice invoice, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(invoice);
            if (enqueueSync) {
                enqueueSync(invoice.id, gson.toJson(invoice), "UPDATE");
            }
        });
    }

    public void delete(final Invoice invoice) {
        delete(invoice, false);
    }

    public void delete(final Invoice invoice, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(invoice);
            if (enqueueSync) {
                enqueueSync(invoice.id, gson.toJson(invoice), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "Invoice", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
