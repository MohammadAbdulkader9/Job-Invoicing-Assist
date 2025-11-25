package com.mdu.jobinvoicing.data.network.financeApi;

import retrofit2.Retrofit;
public class FinanceApi {
    String url = "127.0.0.1";
    Retrofit retrofit;

    public FinanceApi(String url){
        this.url = url;

    }

}
