package com.mdu.jobinvoicing.data.local.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a wheel loader operator (a user who performs work and records time).
 */
@Entity(tableName = "operators")
public class Operator {
    @PrimaryKey
    @NonNull
    public String id; // UUID string

    public String name;
    public String phone;
    public String email;
    public String role; // e.g., "Operator", "Manager"
    public boolean isActive;

    public Operator(@NonNull String id, String name, String phone, String email, String role, boolean isActive) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }
}