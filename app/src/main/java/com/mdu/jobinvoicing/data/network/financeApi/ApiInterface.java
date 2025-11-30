package com.mdu.jobinvoicing.data.network.financeApi;

import java.util.HashMap;

public interface ApiInterface {
    public void getInvoiceById(String id, ApiCallback<InvoiceResponse> callback);
    public void getAllInvoices(ApiCallback<InvoiceResponse> callback);
}
