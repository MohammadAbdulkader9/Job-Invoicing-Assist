package com.mdu.jobinvoicing.data.local.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * SyncQueue stores pending sync operations for background workers.
 * entityJson should contain serialized JSON of the entity/change.
 */
@Entity(tableName = "sync_queue")
public class SyncQueue {
    @PrimaryKey
    @NonNull
    public String id;

    public String entityType;   // e.g., "WorkPlan", "TimeEntry", "Material", "Invoice"
    public String entityId;     // id of the entity being synced
    public String entityJson;   // serialized JSON payload
    public String action;       // e.g., "CREATE", "UPDATE", "DELETE"
    public String state;        // e.g., "PENDING", "SENT", "FAILED"
    public long queuedAtMillis;
    public long lastAttemptMillis;
    public int attemptCount;

    public SyncQueue(@NonNull String id, String entityType, String entityId, String entityJson,
                     String action, String state, long queuedAtMillis, long lastAttemptMillis, int attemptCount) {
        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityJson = entityJson;
        this.action = action;
        this.state = state;
        this.queuedAtMillis = queuedAtMillis;
        this.lastAttemptMillis = lastAttemptMillis;
        this.attemptCount = attemptCount;
    }
}