package com.mdu.jobinvoicing;

import static com.google.firebase.firestore.FieldValue.*;
import static com.google.firebase.firestore.FirebaseFirestore.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;
import com.mdu.jobinvoicing.data.network.financeApi.ApiCallback;
import com.mdu.jobinvoicing.data.network.financeApi.ApiServiceManager;
import com.mdu.jobinvoicing.data.network.financeApi.CustomerResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkPlanActivity extends AppCompatActivity {

    // ===============================
    // Views
    // ===============================
    private EditText clientNameEt, workTitleEt, workDescriptionEt, workLocationEt;
    private AutoCompleteTextView clientNameDropdown;
    private ArrayAdapter<CustomerResponse> customerAdapter;
    private  CustomerResponse activeClient;
    private ApiServiceManager asm;
    private MaterialButton btnCreate, btnCancel;
    private ImageView backBtn;
    private NetworkHelper networkHelper;
    private TextView statusText;
    private ImageView statusDot;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplan);

        // ===============================
        // Initialize Firebase Auth
        // ===============================
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // ===============================
        // Initialize Views
        // ===============================
        //clientNameEt = findViewById(R.id.inputClientName);
        workTitleEt = findViewById(R.id.inputWorkPlanTitle);
        workDescriptionEt = findViewById(R.id.inputJobDescription);
        workLocationEt = findViewById(R.id.inputJobLocation);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);

        asm = ApiServiceManager.getInstance();
        clientNameDropdown = findViewById(R.id.clientNameDropdown);
        asm.getAllCustomers(new ApiCallback<List<CustomerResponse>>() {
            @Override
            public void onSuccess(List<CustomerResponse> response) {
                customerAdapter = new ArrayAdapter<>(
                        WorkPlanActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        response
                );
                clientNameDropdown.setAdapter(customerAdapter);
                clientNameDropdown.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(
                                    AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id
                            ) {
                                activeClient =
                                        (CustomerResponse) parent.getItemAtPosition(
                                                position
                                        );
                            }
                        }
                );
            }
        });

        if (getIntent() != null) {
            String clientName = getIntent().getStringExtra("clientName");
            String workTitle = getIntent().getStringExtra("workTitle");
            String workDescription = getIntent().getStringExtra("workDescription");
            String workLocation = getIntent().getStringExtra("workLocation");

            if (clientName != null) clientNameDropdown.setText(clientName);
            if (workTitle != null) workTitleEt.setText(workTitle);
            if (workDescription != null) workDescriptionEt.setText(workDescription);
            if (workLocation != null) workLocationEt.setText(workLocation);
        }

        // Apply padding for system bars (status bar / navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.workplan), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // ===============================
        // Back & Cancel Button Listeners
        // ===============================
        // Set Top Bar Title
        MaterialToolbar toolbar = findViewById(R.id.top_bar);
        if (toolbar != null) {
            toolbar.setTitle("Create Work Plan");
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        btnCancel.setOnClickListener(v -> finish());

        //==============
        // Create Button Listener
        // ===============================
        btnCreate.setOnClickListener(v -> saveWorkplan());


        // offline-online functionality
        statusText = findViewById(R.id.SyncText);
        statusDot = findViewById(R.id.statusDot);

        networkHelper = new NetworkHelper(this, statusDot, statusText);
        networkHelper.startMonitoring();
    }

    // ===============================
    // Save Workplan to Firestore
    // ===============================
    private void saveWorkplan() {
        String clientName = clientNameDropdown.getText().toString().trim();
        String workTitle = workTitleEt.getText().toString().trim();
        String workDescription = workDescriptionEt.getText().toString().trim();
        String workLocation = workLocationEt.getText().toString().trim();



        // ===============================
        // Validate required fields
        // ===============================
        if (clientName.isEmpty() || workTitle.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===============================
        // Prepare data map
        // ===============================

        // TODO: add client id field if existing customer is used
        Map<String, Object> workplan = new HashMap<>();
        workplan.put("clientName", clientName);
        workplan.put("workTitle", workTitle);
        workplan.put("workDescription", workDescription);
        workplan.put("workLocation", workLocation);
        workplan.put("startDate", serverTimestamp());
        workplan.put("createdAt", serverTimestamp());
        workplan.put("status", WorkPlanStatus.OPEN);

        // ===============================
        // Save to Firestore
        // ===============================
        Task<DocumentReference> workplans = getInstance()
                .collection("users").document(userId).collection("workplans")
                .add(workplan)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Work Plan Created Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorkPlanActivity.this, WorkPlanDetailsActivity.class);
                    intent.putExtra("WORK_PLAN_ID", doc.getId());
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // ===============================
    // Reset Form Fields
    // ===============================
    private void resetForm() {
        clientNameDropdown.setText("");
        activeClient = null;
        workTitleEt.setText("");
        workDescriptionEt.setText("");
        workLocationEt.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkHelper != null) {
            networkHelper.stopMonitoring();
        }
    }

}
