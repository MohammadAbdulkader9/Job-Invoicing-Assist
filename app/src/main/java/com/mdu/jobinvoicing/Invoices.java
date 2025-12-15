package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.mdu.jobinvoicing.data.network.financeApi.ApiCallback;
import com.mdu.jobinvoicing.data.network.financeApi.ApiServiceManager;
import com.mdu.jobinvoicing.data.network.financeApi.CustomerResponse;
import com.mdu.jobinvoicing.data.network.financeApi.ErrorResponse;
import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;
import java.util.List;

public class Invoices extends AppCompatActivity {

    Button saveButton;
    Button sendButton;

    TextInputEditText addrEText;
    TextInputEditText cityEText;
    TextInputEditText countryEText;
    TextInputEditText commentsEText;
    TextInputEditText currencyEText;
    TextInputEditText totalEText;
    TextInputEditText totaltpEText;

    AutoCompleteTextView invoice_dropdown;
    AutoCompleteTextView customer_dropdown;
    ArrayAdapter<InvoiceResponse> invoiceAdapter;
    ArrayAdapter<CustomerResponse> customerAdapter;
    InvoiceResponse activeInvoice;
    CustomerResponse activeCustomer;
    ApiServiceManager asm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        addrEText = findViewById(R.id.etAddress);
        cityEText = findViewById(R.id.etCity);
        countryEText = findViewById(R.id.etCountry);
        commentsEText = findViewById(R.id.etComments);
        currencyEText = findViewById(R.id.etCurrency);
        totalEText = findViewById(R.id.etTotal);
        totaltpEText = findViewById(R.id.etTotalToPay);
        asm = ApiServiceManager.getInstance();
        asm.getAllCustomers(
            new ApiCallback<List<CustomerResponse>>() {
                @Override
                public void onSuccess(List<CustomerResponse> response) {
                    customer_dropdown = findViewById(R.id.customerDropdown);
                    // customers.addAll(response);
                    customerAdapter = new ArrayAdapter<>(
                        Invoices.this,
                        android.R.layout.simple_dropdown_item_1line,
                        response
                    );
                    customer_dropdown.setAdapter(customerAdapter);
                    customer_dropdown.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(
                                AdapterView<?> parent,
                                View view,
                                int position,
                                long id
                            ) {
                                activeCustomer =
                                    (CustomerResponse) parent.getItemAtPosition(
                                        position
                                    );
                            }
                        }
                    );
                }
            }
        );
        asm.getAllInvoices(
            new ApiCallback<List<InvoiceResponse>>() {
                @Override
                public void onSuccess(List<InvoiceResponse> response) {
                    invoice_dropdown = findViewById(
                        R.id.existingInvoiceDropdown
                    );
                    // invoices.addAll(response);
                    invoiceAdapter = new ArrayAdapter<InvoiceResponse>(
                        Invoices.this,
                        android.R.layout.simple_dropdown_item_1line,
                        response
                    );
                    invoice_dropdown.setAdapter(invoiceAdapter);
                    invoice_dropdown.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(
                                AdapterView<?> parent,
                                View view,
                                int position,
                                long id
                            ) {
                                activeInvoice =
                                    (InvoiceResponse) parent.getItemAtPosition(
                                        position
                                    );
                                fillForm(activeInvoice);
                            }
                        }
                    );
                }
            }
        );
        saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activeInvoice == null) {
                        createInvoice();
                        return;
                    }
                    updateInvoice();
                }
            }
        );
        sendButton = findViewById(R.id.btnSend);
        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (activeInvoice == null) {
                            return;
                        }
                        // asm.sendInvoiceById();
                    }
                }
        );
    }

    private void createInvoice() {
        InvoiceResponse invoice = new InvoiceResponse(
            addrEText.getText().toString(),
            cityEText.getText().toString(),
            countryEText.getText().toString(),
            commentsEText.getText().toString(),
            currencyEText.getText().toString(),
            activeCustomer.getName(),
            activeCustomer.getCustomerNumber(),
            Float.parseFloat(totalEText.getText().toString()),
            Float.parseFloat(totaltpEText.getText().toString())
        );

        asm.postInvoice(
            invoice,
            new ApiCallback<InvoiceResponse>() {
                @Override
                public void onSuccess(InvoiceResponse response) {
                    System.out.println("Invoice created successfully");
                }
            }
        );
    }

    private void updateInvoice() {
        activeInvoice.setAddress1(addrEText.getText().toString());
        activeInvoice.setCity(cityEText.getText().toString());
        activeInvoice.setCountry(countryEText.getText().toString());
        activeInvoice.setComments(commentsEText.getText().toString());
        activeInvoice.setCurrency(currencyEText.getText().toString());
        activeInvoice.setTotal(
            Float.parseFloat(totalEText.getText().toString())
        );
        activeInvoice.setTotalToPay(
            Float.parseFloat(totaltpEText.getText().toString())
        );

        activeInvoice.setCustomerId(activeCustomer.getCustomerNumber());
        activeInvoice.setCustomerName(activeCustomer.getName());

        asm.putInvoiceById(
            activeInvoice.getId(),
            activeInvoice,
            new ApiCallback<InvoiceResponse>() {
                @Override
                public void onSuccess(InvoiceResponse response) {
                    System.out.println("Success");
                }
            }
        );
    }

    public void fillForm(InvoiceResponse invoice) {
        String targetCustomerId = invoice.getCustomerId();
        for (int i = 0; i < customerAdapter.getCount(); i++) {
            CustomerResponse item = customerAdapter.getItem(i);
            if (
                item != null &&
                item.getCustomerNumber().equals(targetCustomerId)
            ) {
                customer_dropdown.setText(item.toString(), false);
                break;
            }
        }

        addrEText.setText(invoice.getAddress1());
        cityEText.setText(invoice.getCity());
        countryEText.setText(invoice.getCountry());
        commentsEText.setText(invoice.getComments());
        currencyEText.setText(invoice.getCurrency());
        totalEText.setText(String.valueOf(invoice.getTotal()));
        totaltpEText.setText(String.valueOf(invoice.getTotalToPay()));
    }
}
