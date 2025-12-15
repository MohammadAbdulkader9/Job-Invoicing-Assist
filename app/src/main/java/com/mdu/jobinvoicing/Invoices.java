package com.mdu.jobinvoicing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mdu.jobinvoicing.data.network.financeApi.ApiCallback;
import com.mdu.jobinvoicing.data.network.financeApi.ApiServiceManager;
import com.mdu.jobinvoicing.data.network.financeApi.ErrorResponse;
import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;

public class Invoices extends AppCompatActivity {
    TextView textView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        textView = findViewById(R.id.api_text);


        // Apply padding for system bars (status bar / navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.invoices), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
    }

    public void TestApi(View v){
        ApiServiceManager asm = ApiServiceManager.getInstance();
        asm.getInvoiceById("4", new ApiCallback<InvoiceResponse>() {
            @Override
            public void onSuccess(InvoiceResponse response) {
                System.out.println(response.getAddress1());
            }
            @Override
            public void onError(String errorMessage, ErrorResponse err){
                System.out.println("this is a custom error callback function");
            }
        });
        asm.postInvoice(new InvoiceResponse("wow","e","se",
                "","sek", "Marcus", "33",
                3200,3100), new ApiCallback<InvoiceResponse>(){
            @Override
            public void onSuccess(InvoiceResponse response){
                System.out.println(response.getCustomerName());
            }
            @Override
            public void onError(String errorMessage, ErrorResponse err){
                System.out.println(errorMessage);
                System.out.println(err.getCode());

            }
        });
    }
}
