package com.mdu.jobinvoicing.data.network.financeApi;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;



public class InvoiceResponse {
    @SerializedName("@url")
    private String Url;
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

    @Override
    public String toString() {
        return this.CustomerName; // Or whatever field name holds your Invoice ID
    }
    public InvoiceResponse(String Address1, String City, String Country, String Comments,
                           String Currency, String CustomerName, String CustomerId, float Total, float TotalToPay) {
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

    public String getId() {
        Uri uri = Uri.parse(this.Url);
        return uri.getLastPathSegment();
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String Address1) {
        this.Address1 = Address1;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String Country) {
        this.Country = Country;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String Currency) {
        this.Currency = Currency;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String CustomerName) {
        this.CustomerName = CustomerName;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String CustomerId) {
        this.CustomerId = CustomerId;
    }

    public float getTotal() {
        return Total;
    }

    public void setTotal(float Total) {
        this.Total = Total;
    }

    public float getTotalToPay() {
        return TotalToPay;
    }

    public void setTotalToPay(float TotalToPay) {
        this.TotalToPay = TotalToPay;
    }


}
