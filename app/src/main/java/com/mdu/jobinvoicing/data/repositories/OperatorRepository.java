package com.mdu.jobinvoicing.data.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.OperatorDao;
import com.mdu.jobinvoicing.data.local.entities.Operator;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OperatorRepository {
    private final OperatorDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final SyncQueueRepository syncQueueRepository;
    private final Gson gson = new Gson();

    public OperatorRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.operatorDao();
        syncQueueRepository = new SyncQueueRepository(ctx);
    }

    public LiveData<List<Operator>> getAllOperators() {
        return dao.getAllOperators();
    }

    public LiveData<Operator> getOperatorById(String id) {
        return dao.getOperatorById(id);
    }

    public void insertAll(List<Operator> operators) {
        executor.execute(() -> dao.insertAll(operators));
    }

    public void insert(final Operator operator) {
        insert(operator, false);
    }

    public void insert(final Operator operator, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.insert(operator);
            if (enqueueSync) {
                enqueueSync(operator.id, gson.toJson(operator), "CREATE");
            }
        });
    }

    public void update(final Operator operator) {
        update(operator, false);
    }

    public void update(final Operator operator, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.update(operator);
            if (enqueueSync) {
                enqueueSync(operator.id, gson.toJson(operator), "UPDATE");
            }
        });
    }

    public void delete(final Operator operator) {
        delete(operator, false);
    }

    public void delete(final Operator operator, final boolean enqueueSync) {
        executor.execute(() -> {
            dao.delete(operator);
            if (enqueueSync) {
                enqueueSync(operator.id, gson.toJson(operator), "DELETE");
            }
        });
    }

    private void enqueueSync(String entityId, String json, String action) {
        String qId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        SyncQueue q = new SyncQueue(qId, "Operator", entityId, json, action, "PENDING", now, 0L, 0);
        syncQueueRepository.insert(q);
    }
}
