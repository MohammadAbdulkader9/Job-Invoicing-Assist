package com.mdu.jobinvoicing.data.network.financeApi;

import com.mdu.jobinvoicing.data.network.financeApi.fortnox.FortnoxInstance;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceManager {

    public enum ApiProvider {
        FORTNOX("http://10.0.2.2:5000/"),
        BOKIO("http://10.0.2.2:5000/");

        private final String baseURL;

        ApiProvider(String baseUrl) {
            this.baseURL = baseUrl;
        }
    }

    private static ApiServiceManager instance;
    private ApiInterface api;
    private static Retrofit retrofit;

    private static String token;
    private static String baseURL;

    public static synchronized ApiServiceManager getInstance() {
        return instance;
    }

    public static void createInstance(ApiProvider provider) {
        if (instance == null) {
            instance = new ApiServiceManager(provider);
        }
    }

    private ApiServiceManager(ApiProvider provider) {
        this.baseURL = provider.baseURL;
        this.retrofit = getRetrofitInstance();
        if (provider == ApiProvider.FORTNOX) this.api = new FortnoxInstance(
            "123abc",
            retrofit
        );
    }

    private Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Logging \/ \/
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

    public static ErrorResponse parseErrorResponse(Response<?> response) {
        Converter<ResponseBody, ErrorResponse> converter =
                retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ErrorResponse();
        }

        return error;

    }

    public void getInvoiceById(
            String id,
            ApiCallback<InvoiceResponse> callback
    ) {
        api.getInvoiceById(id, callback);
    }
    public void getAllInvoices(ApiCallback<List<InvoiceResponse>> callback) {
        api.getAllInvoices(callback);
    }
    public void postInvoice(InvoiceResponse body,
                            ApiCallback<InvoiceResponse> callback){
        api.postInvoice(body, callback);
    }
    public void putInvoiceById(String id, InvoiceResponse body,
                               ApiCallback<InvoiceResponse> callback){
        api.putInvoiceById(id,body,callback);
    }
    public void getCustomerById(String id, ApiCallback<CustomerResponse> callback){
        api.getCustomerById(id, callback);
    }
}
