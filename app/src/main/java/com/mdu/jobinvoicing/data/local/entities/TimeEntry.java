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
    private String id;

    private String workPlanId; // FK -> WorkPlan.id
    private String operatorId; // FK -> Operator.id (nullable if operator removed)
    private long startTimeMillis;
    private long endTimeMillis; // 0 if still running
    private double hours; // computed value stored for convenience
    private double hourRate;
    private double total;
    private String notes;
    private String timeEntryId;

    public TimeEntry() {
        // No-argument constructor required for Firestore deserialization
    }

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

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getWorkPlanId() {
        return workPlanId;
    }

    public void setWorkPlanId(String workPlanId) {
        this.workPlanId = workPlanId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getHourRate() {
        return hourRate;
    }

    public void setHourRate(double hourRate) {
        this.hourRate = hourRate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTimeEntryId() {
        return timeEntryId;
    }

    public void setTimeEntryId(String timeEntryId) {
        this.timeEntryId = timeEntryId;
    }
}
