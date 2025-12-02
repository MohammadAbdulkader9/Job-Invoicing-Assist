package com.mdu.jobinvoicing.data.network.financeApi.fortnox;


import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FortnoxService {
    //Invoice Actions
    @GET("/3/invoices/{id}")
    Call<InvoiceResponse> getInvoiceById(@HeaderMap Map<String, String> headers,
                                         @Path("id") String id);
    @GET("/3/invoices")
    Call<List<InvoiceResponse>> getAllInvoices(@HeaderMap Map<String, String> headers);
    @POST("/3/invoices")
    Call<InvoiceResponse> postInvoice(@HeaderMap Map<String, String> headers,
                                      @Body InvoiceResponse invoice);
    @PUT("/3/invoices/{id}")
    Call<InvoiceResponse> putInvoice(@HeaderMap Map<String, String> headers,
                                     @Body InvoiceResponse invoice);
}
