package com.mdu.jobinvoicing;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mdu.jobinvoicing.data.local.entities.Material;
import com.mdu.jobinvoicing.data.local.entities.ServiceItem;
import com.mdu.jobinvoicing.data.local.entities.TimerItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class WorkPlanDetailsActivity extends AppCompatActivity {

    private static final String TAG = "WorkPlanDetailsActivity";

    private EditText customerNameEditText, jobDescriptionEditText, addressEditText, dateEditText;
    private Button saveButton, addTimeEntryButton, addMaterialButton, addServiceItemButton;
    private ImageView backButton;
    private LinearLayout navNewJobs, navCreateInvoices, navSyncNow;

    private FirebaseFirestore db;
    private String currentJobId;

    private RelativeLayout timeEntryHeader, materialHeader, serviceItemHeader;
    private LinearLayout timeEntryContent, materialContent, serviceItemContent;
    private ImageView timeEntryToggle, materialToggle, serviceItemToggle;

    private RecyclerView timeEntriesRecyclerView, materialsRecyclerView, serviceItemsRecyclerView;
    private TimeEntryAdapter timeEntryAdapter;
    private MaterialAdapter materialAdapter;
    private ServiceItemAdapter serviceItemAdapter;
    private EditText timeEntryHoursEditText, timeEntryHourRateEditText, timeEntryNotesEditText;
    private EditText materialNameEditText, materialQuantityEditText, materialUnitEditText, materialUnitPriceEditText, materialNotesEditText;
    private EditText serviceItemDescriptionEditText, serviceItemQuantityEditText, serviceItemUnitEditText, serviceItemUnitPriceEditText, serviceItemNotesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workplan_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find views from the layout
        customerNameEditText = findViewById(R.id.customerNameEditText);
        jobDescriptionEditText = findViewById(R.id.jobDescriptionEditText);
        addressEditText = findViewById(R.id.addressEditText);
        dateEditText = findViewById(R.id.dateEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.back_button);

        navNewJobs = findViewById(R.id.navNewJobs);
        navCreateInvoices = findViewById(R.id.navCreateInvoices);
        navSyncNow = findViewById(R.id.navSyncNow);

        timeEntryHeader = findViewById(R.id.timeEntryHeader);
        timeEntryContent = findViewById(R.id.timeEntryContent);
        timeEntryToggle = findViewById(R.id.timeEntryToggle);
        materialHeader = findViewById(R.id.materialHeader);
        materialContent = findViewById(R.id.materialContent);
        materialToggle = findViewById(R.id.materialToggle);
        serviceItemHeader = findViewById(R.id.serviceItemHeader);
        serviceItemContent = findViewById(R.id.serviceItemContent);
        serviceItemToggle = findViewById(R.id.serviceItemToggle);

        addTimeEntryButton = findViewById(R.id.addTimeEntryButton);
        addMaterialButton = findViewById(R.id.addMaterialButton);
        addServiceItemButton = findViewById(R.id.addServiceItemButton);

        timeEntryHoursEditText = findViewById(R.id.timeEntryHoursEditText);
        timeEntryHourRateEditText = findViewById(R.id.timeEntryHourRateEditText);
        timeEntryNotesEditText = findViewById(R.id.timeEntryNotesEditText);

        materialNameEditText = findViewById(R.id.materialNameEditText);
        materialQuantityEditText = findViewById(R.id.materialQuantityEditText);
        materialUnitEditText = findViewById(R.id.materialUnitEditText);
        materialUnitPriceEditText = findViewById(R.id.materialUnitPriceEditText);
        materialNotesEditText = findViewById(R.id.materialNotesEditText);

        serviceItemDescriptionEditText = findViewById(R.id.serviceItemDescriptionEditText);
        serviceItemQuantityEditText = findViewById(R.id.serviceItemQuantityEditText);
        serviceItemUnitEditText = findViewById(R.id.serviceItemUnitEditText);
        serviceItemUnitPriceEditText = findViewById(R.id.serviceItemUnitPriceEditText);
        serviceItemNotesEditText = findViewById(R.id.serviceItemNotesEditText);

        timeEntriesRecyclerView = findViewById(R.id.timeEntriesRecyclerView);
        timeEntryAdapter = new TimeEntryAdapter(new ArrayList<>());
        timeEntriesRecyclerView.setAdapter(timeEntryAdapter);

        materialsRecyclerView = findViewById(R.id.materialsRecyclerView);
        materialAdapter = new MaterialAdapter(new ArrayList<>());
        materialsRecyclerView.setAdapter(materialAdapter);

        serviceItemsRecyclerView = findViewById(R.id.serviceItemsRecyclerView);
        serviceItemAdapter = new ServiceItemAdapter(new ArrayList<>());
        serviceItemsRecyclerView.setAdapter(serviceItemAdapter);


        // Set OnClickListener for the back button
        backButton.setOnClickListener(v -> finish());

        // Set OnClickListener for the date EditText to show a DatePickerDialog
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Set OnClickListener for the save button
        saveButton.setOnClickListener(v -> {
            // TODO: Implement save logic here to update the document in Firestore
            Toast.makeText(WorkPlanDetailsActivity.this, "Save Changes Clicked", Toast.LENGTH_SHORT).show();
        });

        timeEntryHeader.setOnClickListener(v -> toggleSection(timeEntryContent, timeEntryToggle));
        materialHeader.setOnClickListener(v -> toggleSection(materialContent, materialToggle));
        serviceItemHeader.setOnClickListener(v -> toggleSection(serviceItemContent, serviceItemToggle));

        addTimeEntryButton.setOnClickListener(v -> {
            Log.d(TAG, "Add Time Entry button clicked");
            String notes = timeEntryNotesEditText.getText().toString();
            if (!notes.isEmpty()) {
                TimerItem newTimeEntry = new TimerItem(notes);
                timeEntryAdapter.addTimeEntry(newTimeEntry);
                timeEntryNotesEditText.setText("");
                timeEntryHoursEditText.setText("");
                timeEntryHourRateEditText.setText("");

            }
        });

        addMaterialButton.setOnClickListener(v -> {
            Log.d(TAG, "Add Material button clicked");
            String name = materialNameEditText.getText().toString();
            String quantityStr = materialQuantityEditText.getText().toString();
            String unit = materialUnitEditText.getText().toString();
            String unitPriceStr = materialUnitPriceEditText.getText().toString();
            String notes = materialNotesEditText.getText().toString();

            if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty() && !unitPriceStr.isEmpty()) {
                double quantity = Double.parseDouble(quantityStr);
                double unitPrice = Double.parseDouble(unitPriceStr);

                Material newMaterial = new Material(UUID.randomUUID().toString(), currentJobId, name, quantity, unit, unitPrice, notes);
                materialAdapter.addMaterial(newMaterial);

                materialNameEditText.setText("");
                materialQuantityEditText.setText("");
                materialUnitEditText.setText("");
                materialUnitPriceEditText.setText("");
                materialNotesEditText.setText("");
            }
        });

        addServiceItemButton.setOnClickListener(v -> {
            Log.d(TAG, "Add Service Item button clicked");
            String description = serviceItemDescriptionEditText.getText().toString();
            String quantityStr = serviceItemQuantityEditText.getText().toString();
            String unit = serviceItemUnitEditText.getText().toString();
            String unitPriceStr = serviceItemUnitPriceEditText.getText().toString();
            String notes = serviceItemNotesEditText.getText().toString();

            if (!description.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty() && !unitPriceStr.isEmpty()) {
                double quantity = Double.parseDouble(quantityStr);
                double unitPrice = Double.parseDouble(unitPriceStr);

                ServiceItem newServiceItem = new ServiceItem(UUID.randomUUID().toString(), currentJobId, description, quantity, unit, unitPrice, notes);
                serviceItemAdapter.addServiceItem(newServiceItem);

                serviceItemDescriptionEditText.setText("");
                serviceItemQuantityEditText.setText("");
                serviceItemUnitEditText.setText("");
                serviceItemUnitPriceEditText.setText("");
                serviceItemNotesEditText.setText("");
            }
        });

        // Get the intent that started this activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("JOB_ID")) {
            currentJobId = intent.getStringExtra("JOB_ID");
            if (currentJobId != null && !currentJobId.isEmpty()) {
                loadWorkPlanData(currentJobId);
            }
        }
    }

    private void toggleSection(LinearLayout content, ImageView toggle) {
        if (content.getVisibility() == View.VISIBLE) {
            content.setVisibility(View.GONE);
            toggle.animate().rotation(0).setDuration(200).start();
        } else {
            content.setVisibility(View.VISIBLE);
            toggle.animate().rotation(180).setDuration(200).start();
        }
    }

    private void loadWorkPlanData(String jobId) {
        DocumentReference docRef = db.collection("workplans").document(jobId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    // Populate the EditText fields
                    customerNameEditText.setText(document.getString("clientName"));
                    jobDescriptionEditText.setText(document.getString("jobDescription"));
                    addressEditText.setText(document.getString("jobLocation"));
                    dateEditText.setText(document.getString("date"));
                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(this, "Job details not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(this, "Error fetching job details.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDatePickerDialog() {
        // Get current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Set selected date to EditText
                    dateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                }, year, month, day);
        datePickerDialog.show();
    }
}