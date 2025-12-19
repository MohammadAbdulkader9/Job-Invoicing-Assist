package com.mdu.jobinvoicing;

import static com.google.firebase.firestore.FieldValue.serverTimestamp;
import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;

import java.util.HashMap;
import java.util.Map;

public class WorkPlanActivity extends AppCompatActivity {

    // ===============================
    // Views
    // ===============================
    private EditText clientNameEt, workTitleEt, workDescriptionEt, workLocationEt;
    private MaterialButton btnCreate, btnCancel;
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
        clientNameEt = findViewById(R.id.inputClientName);
        workTitleEt = findViewById(R.id.inputWorkPlanTitle);
        workDescriptionEt = findViewById(R.id.inputJobDescription);
        workLocationEt = findViewById(R.id.inputJobLocation);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);
        if (getIntent() != null) {
            String clientName = getIntent().getStringExtra("clientName");
            String workTitle = getIntent().getStringExtra("workTitle");
            String workDescription = getIntent().getStringExtra("workDescription");
            String workLocation = getIntent().getStringExtra("workLocation");

            if (clientName != null) clientNameEt.setText(clientName);
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
        String clientName = clientNameEt.getText().toString().trim();
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
        getInstance().collection("users").document(userId)
                .collection("operators")
                .whereEqualTo("isActive", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String operatorId = document.getId();
                            getInstance()
                                    .collection("users").document(userId)
                                    .collection("operators").document(operatorId)
                                    .collection("workplans")
                                    .add(workplan)
                                    .addOnSuccessListener(doc -> {
                                        Toast.makeText(this, "Work Plan Created Successfully!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(WorkPlanActivity.this, WorkPlanDetailsActivity.class);
                                        intent.putExtra("WORK_PLAN_ID", doc.getId());
                                        LoadTicket ticket = (LoadTicket) getIntent().getSerializableExtra("loadTicket");
                                        if (ticket != null) {
                                            intent.putExtra("loadTicket", ticket);
                                            intent.putExtra("fromLoadTicket", true);
                                        }
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    } else {
                        Toast.makeText(WorkPlanActivity.this, "No active operator found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ===============================
    // Reset Form Fields
    // ===============================
    private void resetForm() {
        clientNameEt.setText("");
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
