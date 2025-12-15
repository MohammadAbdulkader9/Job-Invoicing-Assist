package com.mdu.jobinvoicing.data.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.ServiceItemDao;
import com.mdu.jobinvoicing.data.local.entities.ServiceItem;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServiceItemRepository {
    private final ServiceItemDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public ServiceItemRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.serviceItemDao();
        syncQueueRepository = new SyncQueueRepository(ctx);
    }

    public LiveData<List<ServiceItem>> getForWorkPlan(String workPlanId) {
        return dao.getForWorkPlan(workPlanId);
    }

    public LiveData<ServiceItem> getServiceItemById(String id) {
        return dao.getServiceItemById(id);
    }

    public void insert(final ServiceItem item) {
        insert(item, false);
    }

    public void insert(final ServiceItem item, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(item);
            if (enqueueSync) {
                enqueueSync(item.getId(), gson.toJson(item), "CREATE");
            }
        });
    }

    public void update(final ServiceItem item) {
        update(item, false);
    }

    public void update(final ServiceItem item, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(item);
            if (enqueueSync) {
                enqueueSync(item.getId(), gson.toJson(item), "UPDATE");
            }
        });
    }

    public void delete(final ServiceItem item) {
        delete(item, false);
    }

    public void delete(final ServiceItem item, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(item);
            if (enqueueSync) {
                enqueueSync(item.getId(), gson.toJson(item), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "ServiceItem", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
