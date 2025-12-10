package com.mdu.jobinvoicing.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "weights",
        foreignKeys = {
                @ForeignKey(entity = WorkPlan.class,
                        parentColumns = "id",
                        childColumns = "workPlanId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("workPlanId")})
public class Weight {
    @PrimaryKey
    @NonNull
    private String id;

    private String workPlanId; // FK -> WorkPlan.id
    private double weight; // in kilograms
    private double ratePerKg;
    private double total;
    private String notes;

    public Weight() {
        // No-argument constructor required for Firestore deserialization
    }

    public Weight(@NonNull String id, String workPlanId, double weight, double ratePerKg, String notes) {
        this.id = id;
        this.workPlanId = workPlanId;
        this.weight = weight;
        this.ratePerKg = ratePerKg;
        this.total = weight * ratePerKg;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getRatePerKg() {
        return ratePerKg;
    }

    public void setRatePerKg(double ratePerKg) {
        this.ratePerKg = ratePerKg;
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
}
