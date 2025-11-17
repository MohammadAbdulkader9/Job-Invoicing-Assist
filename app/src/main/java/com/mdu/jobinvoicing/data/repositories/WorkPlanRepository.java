package com.mdu.jobinvoicing.data.repositories;


import android.content.Context;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.WorkPlanDao;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;
import com.mdu.jobinvoicing.data.local.entities.WorkPlan;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorkPlanRepository {
    private final WorkPlanDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public WorkPlanRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.workPlanDao();
        syncQueueRepository = new SyncQueueRepository(ctx);
    }

    public LiveData<List<WorkPlan>> getAllWorkPlans() {
        return dao.getAllWorkPlans();
    }

    public LiveData<WorkPlan> getWorkPlanById(String id) {
        return dao.getWorkPlanById(id);
    }

    public void insertAll(List<WorkPlan> workPlans) {
        executor.execute(() -> dao.insertAll(workPlans));
    }

    public void insert(final WorkPlan workPlan) {
        insert(workPlan, false);
    }

    public void insert(final WorkPlan workPlan, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(workPlan);
            if (enqueueSync) {
                enqueueSync(workPlan.id, gson.toJson(workPlan), "CREATE");
            }
        });
    }

    public void update(final WorkPlan workPlan) {
        update(workPlan, false);
    }

    public void update(final WorkPlan workPlan, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(workPlan);
            if (enqueueSync) {
                enqueueSync(workPlan.id, gson.toJson(workPlan), "UPDATE");
            }
        });
    }

    public void delete(final WorkPlan workPlan) {
        delete(workPlan, false);
    }

    public void delete(final WorkPlan workPlan, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(workPlan);
            if (enqueueSync) {
                enqueueSync(workPlan.id, gson.toJson(workPlan), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "WorkPlan", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
