package com.mdu.jobinvoicing;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    TextInputEditText emailEText;
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
        emailEText = findViewById(R.id.etEmail);
        currencyEText = findViewById(R.id.etCurrency);
        totalEText = findViewById(R.id.etTotal);
        totaltpEText = findViewById(R.id.etTotalToPay);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_create_invoice);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(Invoices.this, HomePage.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_create_invoice) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(Invoices.this, OperatorProfile.class);
                startActivity(intent);
                return true;
            }
            return false;
        });



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
                                emailEText.setText(activeCustomer.getEmail());
                            }
                        }
                    );
                    fillForm();
                }
                @Override
                public void onError(String errorMessage, ErrorResponse err){
                    Toast toast = Toast.makeText(Invoices.this, "Could not reach the server", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                    toast.show();
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
                                saveButton.setText("Update");
                                sendButton.setEnabled(true);
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
                    if (activeCustomer == null){
                        return;
                    }
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
                        asm.sendInvoiceById(activeInvoice.getId(), new ApiCallback<InvoiceResponse>() {
                            @Override
                            public void onSuccess(InvoiceResponse response) {
                                System.out.println("Email sent");
                            }
                        });
                    }
                }
        );
        sendButton.setEnabled(false);

    }

    private void createInvoice() {
        if (activeCustomer == null) {
            return;
        }
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

    private void fillForm(InvoiceResponse invoice) {
        String targetCustomerId = invoice.getCustomerId();
        for (int i = 0; i < customerAdapter.getCount(); i++) {
            CustomerResponse item = customerAdapter.getItem(i);
            if (
                item != null &&
                item.getCustomerNumber().equals(targetCustomerId)
            ) {
                customer_dropdown.setText(item.toString(), false);
                activeCustomer = item;
                break;
            }
        }

        addrEText.setText(invoice.getAddress1());
        cityEText.setText(invoice.getCity());
        countryEText.setText(invoice.getCountry());
        commentsEText.setText(invoice.getComments());
        emailEText.setText(activeCustomer.getEmail());
        currencyEText.setText(invoice.getCurrency());
        totalEText.setText(String.valueOf(invoice.getTotal()));
        totaltpEText.setText(String.valueOf(invoice.getTotalToPay()));
    }
    private void fillForm() {
        Intent intent = getIntent();
        if (!intent.getBooleanExtra("workplan", false)){
            return;
        }
        findViewById(R.id.existingInvoiceLayout).setEnabled(false);
        String clientName = intent.getStringExtra("clientName");

        addrEText.setText(intent.getStringExtra("location"));
        for (int i = 0; i < customerAdapter.getCount(); i++) {
            CustomerResponse item = customerAdapter.getItem(i);
            if (
                    item != null &&
                            item.getName().equals(clientName)
            ) {
                customer_dropdown.setText(item.toString(), false);
                activeCustomer = item;
                break;
            }
        }
        if (activeCustomer == null) {
            CustomerResponse customer = new CustomerResponse();
            customer.setName(clientName);
            asm.postCustomer(customer, new ApiCallback<CustomerResponse>() {
                @Override
                public void onSuccess(CustomerResponse response) {
                    activeCustomer = response;
                    customer_dropdown.setText(activeCustomer.getName().toString(), false);
                }
                @Override
                public void onError(String errorMessage, ErrorResponse err){
                    Toast toast = Toast.makeText(Invoices.this, "Could not reach the server", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
        else {
            emailEText.setText(activeCustomer.getEmail());
        }
        currencyEText.setText("SEK");
        totalEText.setText(String.valueOf(intent.getFloatExtra("total", 0.0F)));
        totaltpEText.setText(String.valueOf(intent.getFloatExtra("total", 0.0F)));
    }
}
