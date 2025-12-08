package com.mdu.jobinvoicing;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mdu.jobinvoicing.data.local.entities.Material;
import com.mdu.jobinvoicing.data.local.entities.ServiceItem;
import com.mdu.jobinvoicing.data.local.entities.TimeEntry;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class WorkPlanDetailsActivity extends AppCompatActivity {

    private static final String TAG = "WorkPlanDetailsActivity";

    private EditText customerNameEditText, jobDescriptionEditText, addressEditText, dateEditText;
    private Button saveButton, addTimeEntryButton, addMaterialButton, addServiceItemButton;
    private ImageView backButton;
    private RadioGroup statusRadioGroup;
    private RadioButton statusOpen, statusInProgress, statusClosed;

    private FirebaseFirestore db;
    private String currentWorkPlanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplan_details);

        db = FirebaseFirestore.getInstance();

        customerNameEditText = findViewById(R.id.customerNameEditText);
        jobDescriptionEditText = findViewById(R.id.jobDescriptionEditText);
        addressEditText = findViewById(R.id.addressEditText);
        dateEditText = findViewById(R.id.dateEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.back_button);
        statusRadioGroup = findViewById(R.id.statusRadioGroup);
        statusOpen = findViewById(R.id.statusOpen);
        statusInProgress = findViewById(R.id.statusInProgress);
        statusClosed = findViewById(R.id.statusClosed);

        backButton.setOnClickListener(v -> finish());

        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        saveButton.setOnClickListener(v -> {
            // TODO: Implement save logic here to update the document in Firestore
            Toast.makeText(WorkPlanDetailsActivity.this, "Save Changes Clicked", Toast.LENGTH_SHORT).show();
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("WORK_PLAN_ID")) {
            currentWorkPlanId = intent.getStringExtra("WORK_PLAN_ID");
            if (currentWorkPlanId != null && !currentWorkPlanId.isEmpty()) {
                loadWorkPlanData(currentWorkPlanId);
            }
        }
    }

    private void loadWorkPlanData(String workPlanId) {
        DocumentReference docRef = db.collection("workplans").document(workPlanId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    customerNameEditText.setText(document.getString("clientName"));
                    jobDescriptionEditText.setText(document.getString("workDescription"));
                    addressEditText.setText(document.getString("workLocation"));

                    Timestamp startDateTimestamp = document.getTimestamp("startDate");
                    if (startDateTimestamp != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateEditText.setText(sdf.format(startDateTimestamp.toDate()));
                    }

                    String statusStr = document.getString("status");
                    if (statusStr != null) {
                        try {
                            WorkPlanStatus status = WorkPlanStatus.valueOf(statusStr);
                            if (status == WorkPlanStatus.OPEN) {
                                statusOpen.setChecked(true);
                            } else if (status == WorkPlanStatus.IN_PROGRESS) {
                                statusInProgress.setChecked(true);
                            } else if (status == WorkPlanStatus.CLOSED) {
                                statusClosed.setChecked(true);
                            }
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Invalid status value: " + statusStr, e);
                        }
                    }
                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(this, "Work plan details not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(this, "Error fetching work plan details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    dateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                }, year, month, day);
        datePickerDialog.show();
    }
}
