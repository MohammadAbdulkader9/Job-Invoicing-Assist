package com.mdu.jobinvoicing.vm;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mdu.jobinvoicing.data.local.entities.Invoice;
import com.mdu.jobinvoicing.data.repositories.InvoiceRepository;

import java.util.List;

/**
 * ViewModel for managing and providing a list of Invoices to the UI.
 */
public class InvoiceViewModel extends AndroidViewModel {

    private final InvoiceRepository repository;
    private final LiveData<List<Invoice>> allInvoices;

    public InvoiceViewModel(@NonNull Application application) {
        super(application);
        repository = new InvoiceRepository(application);
        allInvoices = repository.getAllInvoices();
    }

    // --- Public API for the UI ---

    /**
     * Exposes the LiveData list of all invoices to the UI.
     */
    public LiveData<List<Invoice>> getAllInvoices() {
        return allInvoices;
    }

    /**
     * Gets a single invoice by its ID.
     */
    public LiveData<Invoice> getInvoiceById(String id) {
        return repository.getInvoiceById(id);
    }

    /**
     * Inserts a new invoice.
     * The enqueueSync flag determines if this change should be synced with a remote server.
     */
    public void insert(Invoice invoice, boolean enqueueSync) {
        repository.insert(invoice, enqueueSync);
    }

    /**
     * Deletes an invoice.
     * The enqueueSync flag determines if this change should be synced with a remote server.
     */
    public void delete(Invoice invoice, boolean enqueueSync) {
        repository.delete(invoice, enqueueSync);
    }

    // In the future, a method to trigger the Fortnox API invoice generation
    // could be added here. For example:
    // public void generateFortnoxInvoice(String workPlanId) { ... }
}
