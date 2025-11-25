package com.mdu.jobinvoicing.data.network.financeApi;

import com.google.gson.annotations.SerializedName;



public class InvoiceResponse {
    @SerializedName("Address1")
    private String Address1;
    @SerializedName("City")
    private String City;
    @SerializedName("Country")
    private String Country;
    @SerializedName("Comments")
    private String Comments;
    @SerializedName("Currency")
    private String Currency;
    @SerializedName("CustomerName")
    private String CustomerName;
    @SerializedName("CustomerNumber")
    private String CustomerId;
    @SerializedName("Total")
    private float Total;
    @SerializedName("TotalToPay")
    private float TotalToPay;

    public InvoiceResponse(String Address1, String City, String Country, String Comments,
                           String Currency, String CustomerName, String CustomerId, float Total, float TotalToPay){
        this.Address1 = Address1;
        this.City = City;
        this.Country = Country;
        this.Comments = Comments;
        this.Currency = Currency;
        this.CustomerName = CustomerName;
        this.CustomerId = CustomerId;
        this.Total = Total;
        this.TotalToPay = TotalToPay;
    }

    public String getAddress1() {
        return Address1;
    }
    public void setAddress1(String Address1){
        this.Address1 = Address1;
    }
}
