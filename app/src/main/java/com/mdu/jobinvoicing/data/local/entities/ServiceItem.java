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
    public String id;

    public String workPlanId;
    public String description;

    public double serviceFee;

    public String notes;

    public ServiceItem(@NonNull String id, String workPlanId, String description,
                       double serviceFee, String notes) {
        this.id = id;
        this.workPlanId = workPlanId;
        this.description = description;
        this.serviceFee = serviceFee;
        this.notes = notes;
    }
}
