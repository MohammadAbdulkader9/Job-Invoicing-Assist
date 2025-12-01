package com.mdu.jobinvoicing.data.network.financeApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {

    public abstract void onSuccess(T response);

    public void onError(String errorMessage) {
        System.err.println("[API Error]: " + errorMessage);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful() && response.body() != null) {
            onSuccess(response.body());
        } else {
            onError("Server Error: " + response.code());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onError("Network Failed: " + t.getMessage());
    }
}
