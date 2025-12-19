package com.mdu.jobinvoicing.data.network.financeApi;

import java.util.HashMap;
import java.util.List;

public interface ApiInterface {
    // Invoice API
    public void getInvoiceById(
        String id,
        ApiCallback<InvoiceResponse> callback
    );
    public void getInvoicePreviewById(
        String id,
        ApiCallback<InvoiceResponse> callback
    );
    public void getAllInvoices(ApiCallback<List<InvoiceResponse>> callback);
    public void postInvoice(
        InvoiceResponse body,
        ApiCallback<InvoiceResponse> callback
    );
    public void putInvoiceById(
        String id,
        InvoiceResponse body,
        ApiCallback<InvoiceResponse> callback
    );
    public void deleteInvoiceById(
        String id,
        ApiCallback<InvoiceResponse> callback
    );

    public void sendInvoiceById(String id, ApiCallback<InvoiceResponse> callback);

    // Customer API
    public void getCustomerById(
        String id,
        ApiCallback<CustomerResponse> callback
    );
    public void getAllCustomers(ApiCallback<List<CustomerResponse>> callback);

    public void postCustomer(
        CustomerResponse body,
        ApiCallback<CustomerResponse> callback
    );
    public void putCustomer(
        String id,
        CustomerResponse body,
        ApiCallback<CustomerResponse> callback
    );
    public void deleteCustomer(
        String id,
        ApiCallback<CustomerResponse> callback
    );
}
