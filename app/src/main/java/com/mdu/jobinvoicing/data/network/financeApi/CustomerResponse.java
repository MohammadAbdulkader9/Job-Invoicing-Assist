package com.mdu.jobinvoicing.data.network.financeApi;

import com.google.gson.annotations.SerializedName;

public class CustomerResponse {
    @SerializedName("Active")
    private boolean active;

    @SerializedName("Address1")
    private String address1;

    @SerializedName("Address2")
    private String address2;

    @SerializedName("City")
    private String city;

    @SerializedName("Comments")
    private String comments;

    @SerializedName("CostCenter")
    private String costCenter;

    @SerializedName("Country")
    private String country;

    @SerializedName("CountryCode")
    private String countryCode;

    @SerializedName("Currency")
    private String currency;

    @SerializedName("CustomerNumber")
    private String customerNumber;

    @SerializedName("DeliveryAddress1")
    private String deliveryAddress1;

    @SerializedName("DeliveryAddress2")
    private String deliveryAddress2;

    @SerializedName("DeliveryCity")
    private String deliveryCity;

    @SerializedName("DeliveryCountry")
    private String deliveryCountry;

    @SerializedName("DeliveryCountryCode")
    private String deliveryCountryCode;

    @SerializedName("DeliveryFax")
    private String deliveryFax;

    @SerializedName("DeliveryName")
    private String deliveryName;

    @SerializedName("DeliveryPhone1")
    private String deliveryPhone1;

    @SerializedName("DeliveryPhone2")
    private String deliveryPhone2;

    @SerializedName("DeliveryZipCode")
    private String deliveryZipCode;

    @SerializedName("Email")
    private String email;

    @SerializedName("EmailInvoice")
    private String emailInvoice;

    @SerializedName("EmailInvoiceBCC")
    private String emailInvoiceBcc;

    @SerializedName("EmailInvoiceCC")
    private String emailInvoiceCc;

    @SerializedName("EmailOffer")
    private String emailOffer;

    @SerializedName("EmailOfferBCC")
    private String emailOfferBcc;

    @SerializedName("EmailOfferCC")
    private String emailOfferCc;

    @SerializedName("EmailOrder")
    private String emailOrder;

    @SerializedName("EmailOrderBCC")
    private String emailOrderBcc;

    @SerializedName("EmailOrderCC")
    private String emailOrderCc;

    @SerializedName("ExternalReference")
    private String externalReference;

    @SerializedName("Fax")
    private String fax;

    @SerializedName("GLN")
    private String gln;

    @SerializedName("GLNDelivery")
    private String glnDelivery;

    @SerializedName("InvoiceAdministrationFee")
    private String invoiceAdministrationFee;

    // Using double for floating point number
    @SerializedName("InvoiceDiscount")
    private double invoiceDiscount;

    @SerializedName("InvoiceFreight")
    private String invoiceFreight;

    @SerializedName("InvoiceRemark")
    private String invoiceRemark;

    @SerializedName("Name")
    private String name;

    @SerializedName("OrganisationNumber")
    private String organisationNumber;

    @SerializedName("OurReference")
    private String ourReference;

    @SerializedName("Phone1")
    private String phone1;

    @SerializedName("Phone2")
    private String phone2;

    @SerializedName("PriceList")
    private String priceList;

    @SerializedName("Project")
    private String project;

    @SerializedName("SalesAccount")
    private String salesAccount;

    @SerializedName("ShowPriceVATIncluded")
    private boolean showPriceVatIncluded;

    @SerializedName("TermsOfDelivery")
    private String termsOfDelivery;

    @SerializedName("TermsOfPayment")
    private String termsOfPayment;

    @SerializedName("Type")
    private String type;

    @SerializedName("VATNumber")
    private String vatNumber;

    @SerializedName("VATType")
    private String vatType;

    @SerializedName("VisitingAddress")
    private String visitingAddress;

    @SerializedName("VisitingCity")
    private String visitingCity;

    @SerializedName("VisitingCountry")
    private String visitingCountry;

    @SerializedName("VisitingCountryCode")
    private String visitingCountryCode;

    @SerializedName("VisitingZipCode")
    private String visitingZipCode;

    @SerializedName("WWW")
    private String www;

    @SerializedName("WayOfDelivery")
    private String wayOfDelivery;

    @SerializedName("YourReference")
    private String yourReference;

    @SerializedName("ZipCode")
    private String zipCode;

    public String toString() {
        return this.name; // Or whatever field name holds your Invoice ID
    }
    public CustomerResponse(){}
    public CustomerResponse(boolean active, String address1, String address2, String city, String comments,
                            String costCenter, String country, String countryCode, String currency,
                            String customerNumber, String deliveryAddress1, String deliveryAddress2,
                            String deliveryCity, String deliveryCountry, String deliveryCountryCode,
                            String deliveryFax, String deliveryName, String deliveryPhone1,
                            String deliveryPhone2, String deliveryZipCode, String email, String emailInvoice,
                            String emailInvoiceBcc, String emailInvoiceCc, String emailOffer,
                            String emailOfferBcc, String emailOfferCc, String emailOrder, String emailOrderBcc,
                            String emailOrderCc, String externalReference, String fax, String gln,
                            String glnDelivery, String invoiceAdministrationFee, double invoiceDiscount,
                            String invoiceFreight, String invoiceRemark, String name, String organisationNumber,
                            String ourReference, String phone1, String phone2, String priceList, String project,
                            String salesAccount, boolean showPriceVatIncluded, String termsOfDelivery,
                            String termsOfPayment, String type, String vatNumber, String vatType,
                            String visitingAddress, String visitingCity, String visitingCountry,
                            String visitingCountryCode, String visitingZipCode, String www,
                            String wayOfDelivery, String yourReference, String zipCode) {
        this.active = active;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.comments = comments;
        this.costCenter = costCenter;
        this.country = country;
        this.countryCode = countryCode;
        this.currency = currency;
        this.customerNumber = customerNumber;
        this.deliveryAddress1 = deliveryAddress1;
        this.deliveryAddress2 = deliveryAddress2;
        this.deliveryCity = deliveryCity;
        this.deliveryCountry = deliveryCountry;
        this.deliveryCountryCode = deliveryCountryCode;
        this.deliveryFax = deliveryFax;
        this.deliveryName = deliveryName;
        this.deliveryPhone1 = deliveryPhone1;
        this.deliveryPhone2 = deliveryPhone2;
        this.deliveryZipCode = deliveryZipCode;
        this.email = email;
        this.emailInvoice = emailInvoice;
        this.emailInvoiceBcc = emailInvoiceBcc;
        this.emailInvoiceCc = emailInvoiceCc;
        this.emailOffer = emailOffer;
        this.emailOfferBcc = emailOfferBcc;
        this.emailOfferCc = emailOfferCc;
        this.emailOrder = emailOrder;
        this.emailOrderBcc = emailOrderBcc;
        this.emailOrderCc = emailOrderCc;
        this.externalReference = externalReference;
        this.fax = fax;
        this.gln = gln;
        this.glnDelivery = glnDelivery;
        this.invoiceAdministrationFee = invoiceAdministrationFee;
        this.invoiceDiscount = invoiceDiscount;
        this.invoiceFreight = invoiceFreight;
        this.invoiceRemark = invoiceRemark;
        this.name = name;
        this.organisationNumber = organisationNumber;
        this.ourReference = ourReference;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.priceList = priceList;
        this.project = project;
        this.salesAccount = salesAccount;
        this.showPriceVatIncluded = showPriceVatIncluded;
        this.termsOfDelivery = termsOfDelivery;
        this.termsOfPayment = termsOfPayment;
        this.type = type;
        this.vatNumber = vatNumber;
        this.vatType = vatType;
        this.visitingAddress = visitingAddress;
        this.visitingCity = visitingCity;
        this.visitingCountry = visitingCountry;
        this.visitingCountryCode = visitingCountryCode;
        this.visitingZipCode = visitingZipCode;
        this.www = www;
        this.wayOfDelivery = wayOfDelivery;
        this.yourReference = yourReference;
        this.zipCode = zipCode;

    }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getCostCenter() { return costCenter; }
    public void setCostCenter(String costCenter) { this.costCenter = costCenter; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getDeliveryAddress1() { return deliveryAddress1; }
    public void setDeliveryAddress1(String deliveryAddress1) { this.deliveryAddress1 = deliveryAddress1; }

    public String getDeliveryAddress2() { return deliveryAddress2; }
    public void setDeliveryAddress2(String deliveryAddress2) { this.deliveryAddress2 = deliveryAddress2; }

    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }

    public String getDeliveryCountry() { return deliveryCountry; }
    public void setDeliveryCountry(String deliveryCountry) { this.deliveryCountry = deliveryCountry; }

    public String getDeliveryCountryCode() { return deliveryCountryCode; }
    public void setDeliveryCountryCode(String deliveryCountryCode) { this.deliveryCountryCode = deliveryCountryCode; }

    public String getDeliveryFax() { return deliveryFax; }
    public void setDeliveryFax(String deliveryFax) { this.deliveryFax = deliveryFax; }

    public String getDeliveryName() { return deliveryName; }
    public void setDeliveryName(String deliveryName) { this.deliveryName = deliveryName; }

    public String getDeliveryPhone1() { return deliveryPhone1; }
    public void setDeliveryPhone1(String deliveryPhone1) { this.deliveryPhone1 = deliveryPhone1; }

    public String getDeliveryPhone2() { return deliveryPhone2; }
    public void setDeliveryPhone2(String deliveryPhone2) { this.deliveryPhone2 = deliveryPhone2; }

    public String getDeliveryZipCode() { return deliveryZipCode; }
    public void setDeliveryZipCode(String deliveryZipCode) { this.deliveryZipCode = deliveryZipCode; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmailInvoice() { return emailInvoice; }
    public void setEmailInvoice(String emailInvoice) { this.emailInvoice = emailInvoice; }

    public String getEmailInvoiceBcc() { return emailInvoiceBcc; }
    public void setEmailInvoiceBcc(String emailInvoiceBcc) { this.emailInvoiceBcc = emailInvoiceBcc; }

    public String getEmailInvoiceCc() { return emailInvoiceCc; }
    public void setEmailInvoiceCc(String emailInvoiceCc) { this.emailInvoiceCc = emailInvoiceCc; }

    public String getEmailOffer() { return emailOffer; }
    public void setEmailOffer(String emailOffer) { this.emailOffer = emailOffer; }

    public String getEmailOfferBcc() { return emailOfferBcc; }
    public void setEmailOfferBcc(String emailOfferBcc) { this.emailOfferBcc = emailOfferBcc; }

    public String getEmailOfferCc() { return emailOfferCc; }
    public void setEmailOfferCc(String emailOfferCc) { this.emailOfferCc = emailOfferCc; }

    public String getEmailOrder() { return emailOrder; }
    public void setEmailOrder(String emailOrder) { this.emailOrder = emailOrder; }

    public String getEmailOrderBcc() { return emailOrderBcc; }
    public void setEmailOrderBcc(String emailOrderBcc) { this.emailOrderBcc = emailOrderBcc; }

    public String getEmailOrderCc() { return emailOrderCc; }
    public void setEmailOrderCc(String emailOrderCc) { this.emailOrderCc = emailOrderCc; }

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getGln() { return gln; }
    public void setGln(String gln) { this.gln = gln; }

    public String getGlnDelivery() { return glnDelivery; }
    public void setGlnDelivery(String glnDelivery) { this.glnDelivery = glnDelivery; }

    public String getInvoiceAdministrationFee() { return invoiceAdministrationFee; }
    public void setInvoiceAdministrationFee(String invoiceAdministrationFee) { this.invoiceAdministrationFee = invoiceAdministrationFee; }

    public double getInvoiceDiscount() { return invoiceDiscount; }
    public void setInvoiceDiscount(double invoiceDiscount) { this.invoiceDiscount = invoiceDiscount; }

    public String getInvoiceFreight() { return invoiceFreight; }
    public void setInvoiceFreight(String invoiceFreight) { this.invoiceFreight = invoiceFreight; }

    public String getInvoiceRemark() { return invoiceRemark; }
    public void setInvoiceRemark(String invoiceRemark) { this.invoiceRemark = invoiceRemark; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOrganisationNumber() { return organisationNumber; }
    public void setOrganisationNumber(String organisationNumber) { this.organisationNumber = organisationNumber; }

    public String getOurReference() { return ourReference; }
    public void setOurReference(String ourReference) { this.ourReference = ourReference; }

    public String getPhone1() { return phone1; }
    public void setPhone1(String phone1) { this.phone1 = phone1; }

    public String getPhone2() { return phone2; }
    public void setPhone2(String phone2) { this.phone2 = phone2; }

    public String getPriceList() { return priceList; }
    public void setPriceList(String priceList) { this.priceList = priceList; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public String getSalesAccount() { return salesAccount; }
    public void setSalesAccount(String salesAccount) { this.salesAccount = salesAccount; }

    public boolean isShowPriceVatIncluded() { return showPriceVatIncluded; }
    public void setShowPriceVatIncluded(boolean showPriceVatIncluded) { this.showPriceVatIncluded = showPriceVatIncluded; }

    public String getTermsOfDelivery() { return termsOfDelivery; }
    public void setTermsOfDelivery(String termsOfDelivery) { this.termsOfDelivery = termsOfDelivery; }

    public String getTermsOfPayment() { return termsOfPayment; }
    public void setTermsOfPayment(String termsOfPayment) { this.termsOfPayment = termsOfPayment; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getVatNumber() { return vatNumber; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }

    public String getVatType() { return vatType; }
    public void setVatType(String vatType) { this.vatType = vatType; }

    public String getVisitingAddress() { return visitingAddress; }
    public void setVisitingAddress(String visitingAddress) { this.visitingAddress = visitingAddress; }

    public String getVisitingCity() { return visitingCity; }
    public void setVisitingCity(String visitingCity) { this.visitingCity = visitingCity; }

    public String getVisitingCountry() { return visitingCountry; }
    public void setVisitingCountry(String visitingCountry) { this.visitingCountry = visitingCountry; }

    public String getVisitingCountryCode() { return visitingCountryCode; }
    public void setVisitingCountryCode(String visitingCountryCode) { this.visitingCountryCode = visitingCountryCode; }

    public String getVisitingZipCode() { return visitingZipCode; }
    public void setVisitingZipCode(String visitingZipCode) { this.visitingZipCode = visitingZipCode; }

    public String getWww() { return www; }
    public void setWww(String www) { this.www = www; }

    public String getWayOfDelivery() { return wayOfDelivery; }
    public void setWayOfDelivery(String wayOfDelivery) { this.wayOfDelivery = wayOfDelivery; }

    public String getYourReference() { return yourReference; }
    public void setYourReference(String yourReference) { this.yourReference = yourReference; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}
