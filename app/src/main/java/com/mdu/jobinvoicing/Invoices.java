package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
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
    AutoCompleteTextView invoice_dropdown;
    AutoCompleteTextView customer_dropdown;
    ArrayAdapter<InvoiceResponse> invoiceAdapter;
    ArrayAdapter<CustomerResponse> customerAdapter;
    InvoiceResponse activeInvoice;
    CustomerResponse activeCustomer;
    ApiServiceManager asm;
    List<InvoiceResponse> invoices;
    List<CustomerResponse> customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
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
    }
    private void createInvoice(){
        TextInputEditText addrEText = findViewById(R.id.etAddress);
        TextInputEditText cityEText = findViewById(R.id.etCity);
        TextInputEditText countryEText = findViewById(
                R.id.etCountry
        );
        TextInputEditText commentsEText = findViewById(
                R.id.etComments
        );
        TextInputEditText totalEText = findViewById(R.id.etTotal);
        TextInputEditText totaltpEText = findViewById(
                R.id.etTotalToPay
        );

        InvoiceResponse invoice = new InvoiceResponse(addrEText.getText().toString(),
                cityEText.getText().toString(), countryEText.getText().toString(),
                commentsEText.getText().toString(), "SEK", activeCustomer.getName(),
                activeCustomer.getCustomerNumber(), Float.parseFloat(totalEText.getText().toString()),
                Float.parseFloat(totaltpEText.getText().toString()));

        asm.postInvoice(invoice, new ApiCallback<InvoiceResponse>() {
            @Override
            public void onSuccess(InvoiceResponse response) {
                System.out.println("Invoice created successfully");
            }
        });

    }

    private void updateInvoice(){
        TextInputEditText addrEText = findViewById(R.id.etAddress);
        TextInputEditText cityEText = findViewById(R.id.etCity);
        TextInputEditText countryEText = findViewById(
                R.id.etCountry
        );
        TextInputEditText commentsEText = findViewById(
                R.id.etComments
        );
        TextInputEditText totalEText = findViewById(R.id.etTotal);
        TextInputEditText totaltpEText = findViewById(
                R.id.etTotalToPay
        );

        activeInvoice.setAddress1(addrEText.getText().toString());
        activeInvoice.setCity(cityEText.getText().toString());
        activeInvoice.setCountry(countryEText.getText().toString());
        activeInvoice.setComments(
                commentsEText.getText().toString()
        );
        //activeInvoice.setTotal(Integer.parseInt(totalEText.getText().toString()));
        activeInvoice.setTotalToPay(
                Float.parseFloat(totaltpEText.getText().toString())
        );

        asm.putInvoiceById(activeInvoice.getId(), activeInvoice, new ApiCallback<InvoiceResponse>() {
            @Override
            public void onSuccess(InvoiceResponse response) {
                System.out.println("Success");
            }
        });
    }

    private void fillForm(InvoiceResponse invoice) {
        TextInputEditText addrEText = findViewById(R.id.etAddress);
        TextInputEditText cityEText = findViewById(R.id.etCity);
        TextInputEditText countryEText = findViewById(R.id.etCountry);
        TextInputEditText commentsEText = findViewById(R.id.etComments);
        TextInputEditText totalEText = findViewById(R.id.etTotal);
        TextInputEditText totaltpEText = findViewById(R.id.etTotalToPay);

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
        totalEText.setText(String.valueOf(invoice.getTotal()));
        totaltpEText.setText(String.valueOf(invoice.getTotalToPay()));
    }

    public void TestApi(View v) {
        ApiServiceManager asm = ApiServiceManager.getInstance();
        asm.getInvoiceById(
            "4",
            new ApiCallback<InvoiceResponse>() {
                @Override
                public void onSuccess(InvoiceResponse response) {
                    System.out.println(response.getAddress1());
                }

                @Override
                public void onError(String errorMessage, ErrorResponse err) {
                    System.out.println(
                        "this is a custom error callback function"
                    );
                }
            }
        );
        asm.postInvoice(
            new InvoiceResponse(
                "wow",
                "e",
                "se",
                "",
                "sek",
                "Marcus",
                "33",
                3200,
                3100
            ),
            new ApiCallback<InvoiceResponse>() {
                @Override
                public void onSuccess(InvoiceResponse response) {
                    System.out.println(response.getCustomerName());
                }

                @Override
                public void onError(String errorMessage, ErrorResponse err) {
                    System.out.println(errorMessage);
                    System.out.println(err.getCode());
                }
            }
        );
    }
}
