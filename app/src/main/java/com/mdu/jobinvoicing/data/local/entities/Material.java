package com.mdu.jobinvoicing.data.local.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Material entity stores materials used on a WorkPlan.
 */
@Entity(tableName = "materials",
        foreignKeys = @ForeignKey(entity = WorkPlan.class,
                parentColumns = "id",
                childColumns = "workPlanId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("workPlanId")})
public class Material {
    @PrimaryKey
    @NonNull
    public String id;

    public String workPlanId;
    public String name;
    public double quantity;
    public String unit; // e.g., "pcs", "m", "kg"
    public double unitPrice;
    public String notes;

    public Material(@NonNull String id, String workPlanId, String name,
                    double quantity, String unit, double unitPrice, String notes) {
        this.id = id;
        this.workPlanId = workPlanId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.notes = notes;
    }
}
