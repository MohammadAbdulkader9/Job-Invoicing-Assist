package com.mdu.jobinvoicing.data.network.financeApi.fortnox;
import com.mdu.jobinvoicing.data.network.financeApi.ApiCallback;
import com.mdu.jobinvoicing.data.network.financeApi.ApiInterface;
import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;

import java.util.HashMap;
import retrofit2.Retrofit;


public class FortnoxInstance implements ApiInterface {
    private final HashMap<String, String> headers;
    private static FortnoxService service;

    public FortnoxInstance(String oAuthKey, Retrofit retrofit){
        service = retrofit.create(FortnoxService.class);
        headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + "123456789");
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
    }


    public void getAllInvoices(ApiCallback<InvoiceResponse> callback) {
        service.getAllInvoices(headers).enqueue(callback);
    }
    public void getInvoiceById(String id, ApiCallback<InvoiceResponse> callback){
        service.getInvoiceById(headers, id).enqueue(callback);
    }

}
