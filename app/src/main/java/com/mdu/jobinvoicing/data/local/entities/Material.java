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
    private String id;

    private String workPlanId;
    private String name;
    private double quantity;
    private String unit; // e.g., "pcs", "m", "kg"
    private double unitPrice;
    private double total;
    private String notes;
    private String materialId;


    public Material() {
        // No-argument constructor required for Firestore deserialization
    }

    public Material(@NonNull String id, String workPlanId, String name,
                    double quantity, String unit, double unitPrice, String notes) {
        this.id = id;
        this.workPlanId = workPlanId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.total = quantity * unitPrice;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
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

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
}
