package com.mdu.jobinvoicing.data.local.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * TimeEntry entity stores start/end times for work done on a WorkPlan
 * and references an Operator who performed the work.
 */
@Entity(tableName = "time_entries",
        foreignKeys = {
                @ForeignKey(entity = WorkPlan.class,
                        parentColumns = "id",
                        childColumns = "workPlanId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Operator.class,
                        parentColumns = "id",
                        childColumns = "operatorId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index("workPlanId"), @Index("operatorId")})
public class TimeEntry {
    @PrimaryKey
    @NonNull
    public String id;

    public String workPlanId; // FK -> WorkPlan.id
    public String operatorId; // FK -> Operator.id (nullable if operator removed)
    public long startTimeMillis;
    public long endTimeMillis; // 0 if still running
    public double hours; // computed value stored for convenience
    public double hourRate;
    public double total;
    public String notes;

    public TimeEntry(@NonNull String id, String workPlanId, String operatorId,
                     long startTimeMillis, long endTimeMillis, double hours, double hourRate, String notes) {
        this.id = id;
        this.workPlanId = workPlanId;
        this.operatorId = operatorId;
        this.startTimeMillis = startTimeMillis;
        this.endTimeMillis = endTimeMillis;
        this.hours = hours;
        this.hourRate = hourRate;
        this.total = hours * hourRate;
        this.notes = notes;
    }
}
