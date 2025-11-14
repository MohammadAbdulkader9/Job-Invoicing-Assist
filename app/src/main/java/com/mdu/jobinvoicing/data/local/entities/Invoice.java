package com.mdu.jobinvoicing.data.local.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Invoice entity stores invoice metadata and totals.
 * workPlanId is nullable and uses SET_NULL on parent deletion.
 */
@Entity(tableName = "invoices",
        foreignKeys = @ForeignKey(entity = WorkPlan.class,
                parentColumns = "id",
                childColumns = "workPlanId",
                onDelete = ForeignKey.SET_NULL),
        indices = {@Index("workPlanId")})
public class Invoice {
    @PrimaryKey
    @NonNull
    public String id;

    // Nullable link to WorkPlan; may be null if invoice not tied to a specific plan
    public String workPlanId;
    public long createdAtMillis;
    public long dueDateMillis;
    public double subtotal;
    public double vatAmount;
    public double total;
    public String currency;
    public String status; // e.g., "DRAFT", "SENT", "PAID"
    public String pdfPath;

    public Invoice(@NonNull String id, String workPlanId, long createdAtMillis, long dueDateMillis,
                   double subtotal, double vatAmount, double total, String currency, String status, String pdfPath) {
        this.id = id;
        this.workPlanId = workPlanId;
        this.createdAtMillis = createdAtMillis;
        this.dueDateMillis = dueDateMillis;
        this.subtotal = subtotal;
        this.vatAmount = vatAmount;
        this.total = total;
        this.currency = currency;
        this.status = status;
        this.pdfPath = pdfPath;
    }
}