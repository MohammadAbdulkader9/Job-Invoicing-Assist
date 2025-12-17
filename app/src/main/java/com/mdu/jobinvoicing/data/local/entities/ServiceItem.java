package com.mdu.jobinvoicing.data.local.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * ServiceItem entity stores service lines (non-material work) for a WorkPlan.
 */
@Entity(tableName = "service_items",
        foreignKeys = @ForeignKey(entity = WorkPlan.class,
                parentColumns = "id",
                childColumns = "workPlanId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("workPlanId")})
public class ServiceItem {
    @PrimaryKey
    @NonNull
    private String id;

    private String workPlanId;
    private String description;

    private double serviceFee;

    private String notes;

    public ServiceItem() {
        // No-argument constructor required for Firestore deserialization
    }

    public ServiceItem(@NonNull String id, String workPlanId, String description,
                       double serviceFee, String notes) {
        this.id = id;
        this.workPlanId = workPlanId;
        this.description = description;
        this.serviceFee = serviceFee;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
