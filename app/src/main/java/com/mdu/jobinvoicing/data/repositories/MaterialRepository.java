package com.mdu.jobinvoicing.data.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.MaterialDao;
import com.mdu.jobinvoicing.data.local.entities.Material;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;


import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Repository for Material operations.
 */
public class MaterialRepository {
    private final MaterialDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public MaterialRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.materialDao();
        syncQueueRepository = new SyncQueueRepository(ctx);
    }

    public LiveData<List<Material>> getForWorkPlan(String workPlanId) {
        return dao.getForWorkPlan(workPlanId);
    }

    public LiveData<Material> getMaterialById(String id) {
        return dao.getMaterialById(id);
    }

    public void insert(final Material material) {
        insert(material, false);
    }

    public void insert(final Material material, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(material);
            if (enqueueSync) {
                enqueueSync(material.id, gson.toJson(material), "CREATE");
            }
        });
    }

    public void update(final Material material) {
        update(material, false);
    }

    public void update(final Material material, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(material);
            if (enqueueSync) {
                enqueueSync(material.id, gson.toJson(material), "UPDATE");
            }
        });
    }

    public void delete(final Material material) {
        delete(material, false);
    }

    public void delete(final Material material, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(material);
            if (enqueueSync) {
                enqueueSync(material.id, gson.toJson(material), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "Material", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
