package com.mdu.jobinvoicing.data.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.WeightDao;
import com.mdu.jobinvoicing.data.local.entities.Distance;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;
import com.mdu.jobinvoicing.data.local.entities.Weight;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeightRepository {

    private final WeightDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public WeightRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.weightDao();
        syncQueueRepository = new SyncQueueRepository(application);
    }

    public LiveData<List<Weight>> getWeightsForWorkPlan(String workPlanId) {
        return dao.getWeightsForWorkPlan(workPlanId);
    }

    public void insert(final Weight weight) {
        insert(weight, false);
    }

    public void insert(Weight weight, boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(weight);
            if (enqueueSync) {
                enqueueSync(weight.getId(), gson.toJson(weight), "CREATE");
            }
        });
    }

    public void update(final Weight weight) {
        update(weight, false);
    }

    public void update(Weight weight, boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(weight);
            if (enqueueSync) {
                enqueueSync(weight.getId(), gson.toJson(weight), "UPDATE");
            }
        });
    }

    public void delete(final Weight weight) {
        delete(weight, false);
    }

    public void delete(Weight weight, boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(weight);
            if (enqueueSync) {
                enqueueSync(weight.getId(), gson.toJson(weight), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "Weight", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
