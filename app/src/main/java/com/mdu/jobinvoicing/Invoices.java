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
import com.mdu.jobinvoicing.data.network.financeApi.ErrorResponse;
import com.mdu.jobinvoicing.data.network.financeApi.InvoiceResponse;
import com.mdu.jobinvoicing.data.network.financeApi.fortnox.FortnoxInstance;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class Invoices extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        textView = findViewById(R.id.api_text);
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
