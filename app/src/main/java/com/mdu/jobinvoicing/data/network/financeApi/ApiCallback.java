package com.mdu.jobinvoicing.data.network.financeApi;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.mdu.jobinvoicing.data.network.financeApi.ErrorResponse;

public abstract class ApiCallback<T> implements Callback<T> {

    public abstract void onSuccess(T response);

    public void onError(String errorMessage, ErrorResponse err) {
        System.err.println("[API Error]: " + errorMessage);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful() && response.body() != null) {
            onSuccess(response.body());
        } else {

            ErrorResponse err = ApiServiceManager.parseErrorResponse(response);
            onError("Server Error: " + response.code(), err);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onError("Network Failed: " + t.getMessage(), null);
    }
}
