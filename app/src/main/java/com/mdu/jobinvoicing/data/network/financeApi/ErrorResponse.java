package com.mdu.jobinvoicing.data.network.financeApi;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("Code")
    private String Code;
    @SerializedName("Error")
    private String Error;
    @SerializedName("Message")
    private String Message;

    public ErrorResponse(String Code, String Error, String Message){
        this.Code = Code;
        this.Error = Error;
        this.Message = Message;
    }
}
