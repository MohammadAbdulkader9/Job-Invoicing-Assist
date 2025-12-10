package com.mdu.jobinvoicing.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "distances",
        foreignKeys = {
                @ForeignKey(entity = WorkPlan.class,
                        parentColumns = "id",
                        childColumns = "workPlanId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("workPlanId")})
public class Distance {
    @PrimaryKey
    @NonNull
    private String id;

    private String workPlanId; // FK -> WorkPlan.id
    private double distance; // in kilometers
    private double ratePerKm;
    private double total;
    private String notes;

    public Distance() {
        // No-argument constructor required for Firestore deserialization
    }

    public Distance(@NonNull String id, String workPlanId, double distance, double ratePerKm, String notes) {
        this.id = id;
        this.workPlanId = workPlanId;
        this.distance = distance;
        this.ratePerKm = ratePerKm;
        this.total = distance * ratePerKm;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getRatePerKm() {
        return ratePerKm;
    }

    public void setRatePerKm(double ratePerKm) {
        this.ratePerKm = ratePerKm;
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
