package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.mdu.jobinvoicing.data.network.financeApi.ApiCallback;
import com.mdu.jobinvoicing.data.network.financeApi.ApiServiceManager;
import com.mdu.jobinvoicing.data.network.financeApi.ErrorResponse;
import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;
import com.mdu.jobinvoicing.data.network.financeApi.fortnox.FortnoxInstance;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Invoices extends AppCompatActivity {

    TextView textView;
    AutoCompleteTextView invoice_dropdown;
    ApiServiceManager asm;
    List<InvoiceResponse> invoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        asm = ApiServiceManager.getInstance();
        asm.getAllInvoices(
            new ApiCallback<List<InvoiceResponse>>() {
                @Override
                public void onSuccess(List<InvoiceResponse> response) {
                    invoice_dropdown = findViewById(
                        R.id.existingInvoiceDropdown
                    );
                    ArrayAdapter<InvoiceResponse> adapter = new ArrayAdapter<
                        InvoiceResponse
                    >(
                        Invoices.this,
                        android.R.layout.simple_dropdown_item_1line,
                        response
                    );
                    invoice_dropdown.setAdapter(adapter);
                    invoice_dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            InvoiceResponse invoice = (InvoiceResponse) parent.getItemAtPosition(position);
                            fillForm(invoice);
                        }
                    });
                }
            }
        );
    }

    private void fillForm(InvoiceResponse invoice) {
        TextInputEditText addrEText = findViewById(R.id.etAddress);
        TextInputEditText cityEText = findViewById(R.id.etCity);
        TextInputEditText countryEText = findViewById(R.id.etCountry);
        TextInputEditText commentsEText = findViewById(R.id.etComments);
        TextInputEditText totalEText = findViewById(R.id.etTotal);
        TextInputEditText totaltpEText = findViewById(R.id.etTotalToPay);

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
