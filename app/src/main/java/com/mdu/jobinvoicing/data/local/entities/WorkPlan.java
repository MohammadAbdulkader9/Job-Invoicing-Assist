package com.mdu.jobinvoicing.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * WorkPlan entity represents a work plan.
 */
@Entity(tableName = "work_plans")
public class WorkPlan {
    @PrimaryKey
    @NonNull
    public String id;

    public String title;
    public String clientName;
    public String location;
    public long startDateMillis;
    public long endDateMillis;
    public String status; // e.g., "OPEN", "IN_PROGRESS", "CLOSED"

    public WorkPlan(@NonNull String id, String title, String clientName, String location,
                    long startDateMillis, long endDateMillis, String status) {
        this.id = id;
        this.title = title;
        this.clientName = clientName;
        this.location = location;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.status = status;
    }
}