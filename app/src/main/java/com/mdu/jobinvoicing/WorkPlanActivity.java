package com.mdu.jobinvoicing;

import static com.google.firebase.firestore.FieldValue.*;
import static com.google.firebase.firestore.FirebaseFirestore.*;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;

import java.util.HashMap;
import java.util.Map;

public class WorkPlanActivity extends AppCompatActivity {

    // ===============================
    // Views
    // ===============================
    private EditText clientNameEt, workTitleEt, workDescriptionEt, workLocationEt;
    private MaterialButton btnCreate, btnCancel;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplan);

        // ===============================
        // Initialize Views
        // ===============================
        clientNameEt = findViewById(R.id.inputClientName);
        workTitleEt = findViewById(R.id.inputWorkPlanTitle);
        workDescriptionEt = findViewById(R.id.inputJobDescription);
        workLocationEt = findViewById(R.id.inputJobLocation);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);
        backBtn = findViewById(R.id.back_button);

        // ===============================
        // Back & Cancel Button Listeners
        // ===============================
        backBtn.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        //==============
        // Create Button Listener
        // ===============================
        btnCreate.setOnClickListener(v -> saveWorkplan());
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
        Task<DocumentReference> workplans = getInstance()
                .collection("workplans")
                .add(workplan)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Work Plan Created Successfully!", Toast.LENGTH_SHORT).show();
                    resetForm(); // <-- reset all fields after save
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
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

}
