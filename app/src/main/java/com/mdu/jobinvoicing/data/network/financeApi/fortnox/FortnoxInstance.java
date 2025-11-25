package com.mdu.jobinvoicing.data.network.financeApi.fortnox;

import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.HTTP;

public class FortnoxInstance {
    private final HashMap<String, String> headers;
    private static final String baseURL = "http://10.0.2.2:5000/"; //"https://api.fortnox.se";
    private static Retrofit retrofit;
    private static FortnoxService service;

    public FortnoxInstance(String oAuthKey){
        retrofit = getInstance();
        service = getService();
        headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + "123456789");
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
    }

    private static Retrofit getInstance(){
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
    private static FortnoxService getService(){
        return getInstance().create(FortnoxService.class);
    }

    public InvoiceResponse getInvoice(String id){
        if(retrofit == null){return null;}
        InvoiceResponse[] responseData = new InvoiceResponse[1];
        service.getInvoice(headers, id).enqueue(new Callback<InvoiceResponse>() {
            @Override
            public void onResponse(Call<InvoiceResponse> call, Response<InvoiceResponse> response) {

                responseData[0] = response.body();
            }

            @Override
            public void onFailure(Call<InvoiceResponse> call, Throwable t) {


            }
        });
        return responseData[0];
    }

}
