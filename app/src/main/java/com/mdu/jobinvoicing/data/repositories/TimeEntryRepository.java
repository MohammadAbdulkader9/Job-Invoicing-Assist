package com.mdu.jobinvoicing.data.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.TimeEntryDao;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;
import com.mdu.jobinvoicing.data.local.entities.TimeEntry;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TimeEntryRepository {
    private final TimeEntryDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public TimeEntryRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.timeEntryDao();
        syncQueueRepository = new SyncQueueRepository(ctx);
    }

    public LiveData<List<TimeEntry>> getForWorkPlan(String workPlanId) {
        return dao.getForWorkPlan(workPlanId);
    }


    public LiveData<TimeEntry> getTimeEntryById(String id) {
        return dao.getTimeEntryById(id);
    }

    public void insert(final TimeEntry entry) {
        insert(entry, false);
    }

    /**
     * Insert and optionally enqueue for sync.
     */
    public void insert(final TimeEntry entry, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(entry);
            if (enqueueSync) {
                enqueueSync(entry.getId(), gson.toJson(entry), "CREATE");
            }
        });
    }

    public void update(final TimeEntry entry) {
        update(entry, false);
    }

    public void update(final TimeEntry entry, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(entry);
            if (enqueueSync) {
                enqueueSync(entry.getId(), gson.toJson(entry), "UPDATE");
            }
        });
    }

    public void delete(final TimeEntry entry) {
        delete(entry, false);
    }

    public void delete(final TimeEntry entry, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(entry);
            if (enqueueSync) {
                enqueueSync(entry.getId(), gson.toJson(entry), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "TimeEntry", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
