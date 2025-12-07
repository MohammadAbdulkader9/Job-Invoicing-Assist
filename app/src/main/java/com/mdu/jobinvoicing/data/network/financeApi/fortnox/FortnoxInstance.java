package com.mdu.jobinvoicing.data.network.financeApi.fortnox;
import com.mdu.jobinvoicing.data.network.financeApi.ApiCallback;
import com.mdu.jobinvoicing.data.network.financeApi.ApiInterface;
import com.mdu.jobinvoicing.data.network.financeApi.CustomerResponse;
import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;

import java.util.HashMap;
import java.util.List;

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



    public void getInvoiceById(String id, ApiCallback<InvoiceResponse> callback){
        service.getInvoiceById(headers, id).enqueue(callback);
    }
    public void getAllInvoices(ApiCallback<List<InvoiceResponse>> callback) {
        service.getAllInvoices(headers).enqueue(callback);
    }
    public void postInvoice(InvoiceResponse body, ApiCallback<InvoiceResponse> callback) {
        service.postInvoice(headers, body).enqueue(callback);
    }

    public void putInvoiceById(String id, InvoiceResponse body, ApiCallback<InvoiceResponse> callback) {
        service.putInvoice(headers, id, body).enqueue(callback);
    }
    public void deleteInvoiceById(String id, ApiCallback<InvoiceResponse> callback){
        service.deleteInvoiceById(headers, id).enqueue(callback);
    }

    public void getCustomerById(String id, ApiCallback<CustomerResponse> callback){
        service.getCustomerById(headers, id).enqueue(callback);
    }
    public void getAllCustomers(ApiCallback<List<CustomerResponse>>  callback){
        service.getAllCustomers(headers).enqueue(callback);
    }

    public void postCustomer(CustomerResponse body,ApiCallback<CustomerResponse> callback){
        service.postCustomer(headers, body).enqueue(callback);
    }
    public void putCustomer(String id,CustomerResponse body, ApiCallback<CustomerResponse> callback){
        service.putCustomerById(headers,id,body).enqueue(callback);
    }
    public void deleteCustomer(String id, ApiCallback<CustomerResponse> callback){
        service.deleteCustomerById(headers,id).enqueue(callback);
    }

}
