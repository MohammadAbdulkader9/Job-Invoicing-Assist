package com.mdu.jobinvoicing.data.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.DistanceDao;
import com.mdu.jobinvoicing.data.local.entities.Distance;
import com.mdu.jobinvoicing.data.local.entities.Material;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DistanceRepository {

    private final DistanceDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public DistanceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.distanceDao();
        syncQueueRepository = new SyncQueueRepository(application);
    }

    public LiveData<List<Distance>> getDistancesForWorkPlan(String workPlanId) {
        return dao.getDistancesForWorkPlan(workPlanId);
    }


    public void insert(final Distance distance) {
        insert(distance, false);
    }


    public void insert(Distance distance, boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(distance);
            if (enqueueSync) {
                enqueueSync(distance.getId(), gson.toJson(distance), "CREATE");
            }
        });
    }

    public void update(final Distance distance) {
        update(distance, false);
    }

    public void update(Distance distance, boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(distance);
            if (enqueueSync) {
                enqueueSync(distance.getId(), gson.toJson(distance), "UPDATE");
            }
        });
    }

    public void delete(final Distance distance) {
        delete(distance, false);
    }

    public void delete(Distance distance, boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(distance);
            if (enqueueSync) {
                enqueueSync(distance.getId(), gson.toJson(distance), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "Distance", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
