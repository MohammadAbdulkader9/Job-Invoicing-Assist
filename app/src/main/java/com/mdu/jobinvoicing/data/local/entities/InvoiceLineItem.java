package com.mdu.jobinvoicing.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * InvoiceLineItem stores individual lines for an Invoice.
 * represent single item in an invoice.
 */
@Entity(tableName = "invoice_line_items",
        foreignKeys = @ForeignKey(entity = Invoice.class,
                parentColumns = "id",
                childColumns = "invoiceId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("invoiceId")})
public class InvoiceLineItem {
    @PrimaryKey
    @NonNull
    public String id;

    public String invoiceId;
    public String description;
    public double quantity;
    public String unit;
    public double unitPrice;
    public double lineTotal;

    public InvoiceLineItem(@NonNull String id, String invoiceId, String description,
                           double quantity, String unit, double unitPrice, double lineTotal) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }
}
