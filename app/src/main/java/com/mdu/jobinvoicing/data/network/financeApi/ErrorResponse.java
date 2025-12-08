package com.mdu.jobinvoicing.data.network.financeApi;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("Code")
    private String Code;
    @SerializedName("Error")
    private String Error;
    @SerializedName("Message")
    private String Message;

    public ErrorResponse(){
    }
    public String getCode() {return this.Code;}
    public String getError() {return  this.Error;}
    public String getMessage() {return this.Message;}
}
