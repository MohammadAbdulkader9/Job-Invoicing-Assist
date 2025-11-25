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
        FortnoxInstance instance = new FortnoxInstance("");
        InvoiceResponse in = instance.getInvoice("6");
    }
}
