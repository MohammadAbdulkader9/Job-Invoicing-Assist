package com.mdu.jobinvoicing.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

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
    public String description;
    public String location;
    public long startDate;
    public long endDate;
    public WorkPlanStatus status;

    public WorkPlan(@NonNull String id, String title, String clientName, String location,
                    long startDate, long endDate, WorkPlanStatus status) {
        this.id = id;
        this.title = title;
        this.clientName = clientName;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public WorkPlanStatus getStatus() {
        return status;
    }

    public void setStatus(WorkPlanStatus status) {
        this.status = status;
        }

    public String getWorkTitle() {
        return title;

    }
    public String getClientName() {
        return clientName;
    }

    public long getStartDate() {
        return startDate;
    }
}
