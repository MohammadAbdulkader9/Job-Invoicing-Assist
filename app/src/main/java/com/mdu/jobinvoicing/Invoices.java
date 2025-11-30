package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mdu.jobinvoicing.data.network.financeApi.ApiCallback;
import com.mdu.jobinvoicing.data.network.financeApi.ApiServiceManager;
import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;
import com.mdu.jobinvoicing.data.network.financeApi.fortnox.FortnoxInstance;

public class Invoices extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        textView = findViewById(R.id.api_text);
    }

    public void TestApi(View v){
        ApiServiceManager sm = new ApiServiceManager(ApiServiceManager.ApiProvider.FORTNOX);
        sm.getInvoiceById("4", new ApiCallback<InvoiceResponse>() {
            @Override
            public void onSuccess(InvoiceResponse response) {
                System.out.println(response.getAddress1());
            }
        });
    }
}
