package com.mdu.jobinvoicing.data.repositories;

import android.content.Context;

import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.entities.SyncQueue;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SyncQueueRepository {
    private final com.mdu.jobinvoicing.data.local.daos.SyncQueueDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public SyncQueueRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        dao = db.syncQueueDao();
    }

    /**
     * Worker-friendly synchronous read of pending items.
     */
    public List<SyncQueue> getPending() {
        return dao.getByState("PENDING");
    }

    public List<SyncQueue> getAllSyncItems() {
        return dao.getAll();
    }

    public SyncQueue getSyncItemById(String id) {
        return dao.getSyncItemById(id);
    }

    /**
     * Asynchronous insert.
     */
    public void insert(final SyncQueue item) {
        executor.execute(() -> dao.insert(item));
    }

    /**
     * Batch insert asynchronous.
     */
    public void insertAll(final List<SyncQueue> items) {
        executor.execute(() -> dao.insertAll(items));
    }

    public void update(final SyncQueue item) {
        executor.execute(() -> dao.update(item));
    }

    public void deleteByState(final String state) {
        executor.execute(() -> dao.deleteByState(state));
    }

    public void clearAll() {
        executor.execute(() -> dao.clearAll());
    }
}
