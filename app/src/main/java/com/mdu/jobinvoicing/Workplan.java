package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Workplan extends AppCompatActivity {

    // ===============================
    // Views
    // ===============================
    private EditText clientNameEt, jobTitleEt, jobDescriptionEt, jobLocationEt, startTimeEt, endTimeEt;
    private AutoCompleteTextView billingDropdown, materialDropdown;
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
        jobTitleEt = findViewById(R.id.inputJobTitle);
        jobDescriptionEt = findViewById(R.id.inputJobDescription);
        jobLocationEt = findViewById(R.id.inputJobLocation);
        startTimeEt = findViewById(R.id.inputStartTime);
        endTimeEt = findViewById(R.id.inputEndTime);
        billingDropdown = findViewById(R.id.mySpinner);
        materialDropdown = findViewById(R.id.my_material);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);
        backBtn = findViewById(R.id.back_button);

        // ===============================
        // Back & Cancel Button Listeners
        // ===============================
        backBtn.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        // ===============================
        // Billing Model Dropdown
        // ===============================
        ArrayAdapter<CharSequence> billingAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.Billing_List,
                android.R.layout.simple_list_item_1
        );
        billingDropdown.setAdapter(billingAdapter);

        // ===============================
        // Materials Used Dropdown
        // ===============================
        ArrayAdapter<CharSequence> materialAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.Material_List,
                android.R.layout.simple_list_item_1
        );
        materialDropdown.setAdapter(materialAdapter);

        // ===============================
        // Disable direct typing in Time EditTexts
        // ===============================
        startTimeEt.setFocusable(false);
        endTimeEt.setFocusable(false);

        startTimeEt.setOnClickListener(v -> showTimePicker(startTimeEt));
        endTimeEt.setOnClickListener(v -> showTimePicker(endTimeEt));

        // ===============================
        // Create Button Listener
        // ===============================
        btnCreate.setOnClickListener(v -> saveWorkplan());
    }

    // ===============================
    // Save Workplan to Firestore
    // ===============================
    private void saveWorkplan() {
        String clientName = clientNameEt.getText().toString().trim();
        String jobTitle = jobTitleEt.getText().toString().trim();
        String jobDescription = jobDescriptionEt.getText().toString().trim();
        String jobLocation = jobLocationEt.getText().toString().trim();
        String billingModel = billingDropdown.getText().toString().trim();
        String materialsUsed = materialDropdown.getText().toString().trim();
        String startTime = startTimeEt.getText().toString();
        String endTime = endTimeEt.getText().toString();

        // ===============================
        // Validate required fields
        // ===============================
        if (clientName.isEmpty() || jobTitle.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===============================
        // Prepare data map
        // ===============================
        Map<String, Object> workplan = new HashMap<>();
        workplan.put("clientName", clientName);
        workplan.put("jobTitle", jobTitle);
        workplan.put("jobDescription", jobDescription);
        workplan.put("jobLocation", jobLocation);
        workplan.put("billingModel", billingModel);
        workplan.put("materialsUsed", materialsUsed);
        workplan.put("startTime", startTime);
        workplan.put("endTime", endTime);
        workplan.put("createdAt", FieldValue.serverTimestamp());

        // ===============================
        // Save to Firestore
        // ===============================
        FirebaseFirestore.getInstance()
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
        jobTitleEt.setText("");
        jobDescriptionEt.setText("");
        jobLocationEt.setText("");
        billingDropdown.setText("", false);
        materialDropdown.setText("", false);
        startTimeEt.setText("");
        endTimeEt.setText("");
    }

    // ===============================
    // Show MaterialTimePicker
    // ===============================
    private void showTimePicker(EditText targetEditText) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTitleText("Select time")
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build();

        picker.show(getSupportFragmentManager(), "time");

        picker.addOnPositiveButtonClickListener(v -> {
            // Use explicit Locale to avoid warnings
            String hour = String.format(Locale.US, "%02d", picker.getHour());
            String minute = String.format(Locale.US, "%02d", picker.getMinute());
            targetEditText.setText(hour + ":" + minute);
        });
    }
}
