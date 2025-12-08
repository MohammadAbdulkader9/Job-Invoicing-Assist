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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mdu.jobinvoicing.data.local.entities.Material;
import com.mdu.jobinvoicing.data.local.entities.ServiceItem;
import com.mdu.jobinvoicing.data.local.entities.TimeEntry;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WorkPlanDetailsActivity extends AppCompatActivity {

    private static final String TAG = "WorkPlanDetailsActivity";

    private EditText customerNameEditText, workDescriptionEditText, locationEditText, dateEditText;
    private Button saveButton, addTimeEntryButton, addMaterialButton, addServiceItemButton;
    private ImageView backButton;
    private RadioGroup statusRadioGroup;
    private RadioButton statusOpen, statusInProgress, statusClosed;

    private FirebaseFirestore db;
    private String currentWorkPlanId;

    private RelativeLayout timeEntryHeader, materialHeader, serviceItemHeader;
    private LinearLayout timeEntryContent, materialContent, serviceItemContent;
    private ImageView timeEntryToggle, materialToggle, serviceItemToggle;

    private RecyclerView timeEntriesRecyclerView, materialsRecyclerView, serviceItemsRecyclerView;
    private TimeEntryAdapter timeEntryAdapter;
    private MaterialAdapter materialAdapter;
    private ServiceItemAdapter serviceItemAdapter;
    private EditText timeEntryHoursEditText, timeEntryHourRateEditText, timeEntryNotesEditText;
    private EditText materialNameEditText, materialQuantityEditText, materialUnitEditText, materialUnitPriceEditText, materialNotesEditText;
    private EditText serviceItemDescriptionEditText, serviceItemFeeEditText, serviceItemNotesEditText;

    private ArrayList<TimeEntry> timeEntryList = new ArrayList<>();
    private ArrayList<Material> materialList = new ArrayList<>();
    private ArrayList<ServiceItem> serviceItemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplan_details);

        db = FirebaseFirestore.getInstance();

        customerNameEditText = findViewById(R.id.customerNameEditText);
        workDescriptionEditText = findViewById(R.id.workDescriptionEditText);
        locationEditText = findViewById(R.id.addressEditText);
        dateEditText = findViewById(R.id.dateEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.back_button);
        statusRadioGroup = findViewById(R.id.statusRadioGroup);
        statusOpen = findViewById(R.id.statusOpen);
        statusInProgress = findViewById(R.id.statusInProgress);
        statusClosed = findViewById(R.id.statusClosed);

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
        serviceItemFeeEditText = findViewById(R.id.serviceItemFeeEditText);
        serviceItemNotesEditText = findViewById(R.id.serviceItemNotesEditText);

        setupRecyclerViews();

        backButton.setOnClickListener(v -> finish());
        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        saveButton.setOnClickListener(v -> saveWorkPlanChanges());

        timeEntryHeader.setOnClickListener(v -> toggleSection(timeEntryContent, timeEntryToggle));
        materialHeader.setOnClickListener(v -> toggleSection(materialContent, materialToggle));
        serviceItemHeader.setOnClickListener(v -> toggleSection(serviceItemContent, serviceItemToggle));

        addTimeEntryButton.setOnClickListener(v -> addTimeEntry());
        addMaterialButton.setOnClickListener(v -> addMaterial());
        addServiceItemButton.setOnClickListener(v -> addServiceItem());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("WORK_PLAN_ID")) {
            currentWorkPlanId = intent.getStringExtra("WORK_PLAN_ID");
            if (currentWorkPlanId != null && !currentWorkPlanId.isEmpty()) {
                loadWorkPlanData(currentWorkPlanId);
                fetchSubCollections(currentWorkPlanId);
            }
        }
    }

    private void setupRecyclerViews() {
        timeEntriesRecyclerView = findViewById(R.id.timeEntriesRecyclerView);
        timeEntriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeEntryAdapter = new TimeEntryAdapter(timeEntryList);
        timeEntriesRecyclerView.setAdapter(timeEntryAdapter);

        materialsRecyclerView = findViewById(R.id.materialsRecyclerView);
        materialsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        materialAdapter = new MaterialAdapter(materialList);
        materialsRecyclerView.setAdapter(materialAdapter);

        serviceItemsRecyclerView = findViewById(R.id.serviceItemsRecyclerView);
        serviceItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceItemAdapter = new ServiceItemAdapter(serviceItemList);
        serviceItemsRecyclerView.setAdapter(serviceItemAdapter);
    }

    private void loadWorkPlanData(String workPlanId) {
        DocumentReference docRef = db.collection("workplans").document(workPlanId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    customerNameEditText.setText(document.getString("clientName"));
                    workDescriptionEditText.setText(document.getString("workDescription"));
                    locationEditText.setText(document.getString("workLocation"));

                    Timestamp startDateTimestamp = document.getTimestamp("startDate");
                    if (startDateTimestamp != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateEditText.setText(sdf.format(startDateTimestamp.toDate()));
                    }

                    String statusStr = document.getString("status");
                    if (statusStr != null) {
                        try {
                            WorkPlanStatus status = WorkPlanStatus.valueOf(statusStr);
                            if (status == WorkPlanStatus.OPEN) statusOpen.setChecked(true);
                            else if (status == WorkPlanStatus.IN_PROGRESS) statusInProgress.setChecked(true);
                            else if (status == WorkPlanStatus.CLOSED) statusClosed.setChecked(true);
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Invalid status value: " + statusStr, e);
                        }
                    }
                } else {
                    Toast.makeText(this, "Work plan details not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error fetching work plan details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveWorkPlanChanges() {
        if (currentWorkPlanId == null || currentWorkPlanId.isEmpty()) {
            Toast.makeText(this, "Cannot save changes, work plan ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        String clientName = customerNameEditText.getText().toString().trim();
        String workDescription = workDescriptionEditText.getText().toString().trim();
        String workLocation = locationEditText.getText().toString().trim();
        String dateStr = dateEditText.getText().toString().trim();

        int selectedStatusId = statusRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedStatusId);
        String statusStr = selectedRadioButton.getText().toString().toUpperCase().replace(" ", "_");
        WorkPlanStatus status = WorkPlanStatus.valueOf(statusStr);

        Map<String, Object> updatedWorkPlan = new HashMap<>();
        updatedWorkPlan.put("clientName", clientName);
        updatedWorkPlan.put("workDescription", workDescription);
        updatedWorkPlan.put("workLocation", workLocation);
        updatedWorkPlan.put("status", status);

        if (!dateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(dateStr);
                if (date != null) {
                    updatedWorkPlan.put("startDate", new Timestamp(date));
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + dateStr, e);
            }
        }

        db.collection("workplans").document(currentWorkPlanId)
                .update(updatedWorkPlan)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(WorkPlanDetailsActivity.this, "Work plan updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(WorkPlanDetailsActivity.this, "Error updating work plan: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void addTimeEntry() {
        String hoursStr = timeEntryHoursEditText.getText().toString();
        String hourRateStr = timeEntryHourRateEditText.getText().toString();

        if (hoursStr.isEmpty() || hourRateStr.isEmpty()) {
            Toast.makeText(this, "Hours and rate cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> timeEntry = new HashMap<>();
        timeEntry.put("workPlanId", currentWorkPlanId);
        timeEntry.put("hours", Double.parseDouble(hoursStr));
        timeEntry.put("hourRate", Double.parseDouble(hourRateStr));
        timeEntry.put("notes", timeEntryNotesEditText.getText().toString());
        timeEntry.put("createdAt", new Timestamp(new Date()));

        db.collection("time_entries").add(timeEntry).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Time entry added", Toast.LENGTH_SHORT).show();
            timeEntryHoursEditText.setText("");
            timeEntryHourRateEditText.setText("");
            timeEntryNotesEditText.setText("");
        }).addOnFailureListener(e -> Toast.makeText(this, "Error adding time entry", Toast.LENGTH_SHORT).show());
    }

    private void addMaterial() {
        String name = materialNameEditText.getText().toString();
        String quantityStr = materialQuantityEditText.getText().toString();
        String unitPriceStr = materialUnitPriceEditText.getText().toString();

        if (name.isEmpty() || quantityStr.isEmpty() || unitPriceStr.isEmpty()) {
            Toast.makeText(this, "Material name, quantity, and unit price are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> material = new HashMap<>();
        material.put("workPlanId", currentWorkPlanId);
        material.put("name", name);
        material.put("quantity", Double.parseDouble(quantityStr));
        material.put("unit", materialUnitEditText.getText().toString());
        material.put("unitPrice", Double.parseDouble(unitPriceStr));
        material.put("notes", materialNotesEditText.getText().toString());
        material.put("createdAt", new Timestamp(new Date()));

        db.collection("materials").add(material).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Material added", Toast.LENGTH_SHORT).show();
            materialNameEditText.setText("");
            materialQuantityEditText.setText("");
            materialUnitEditText.setText("");
            materialUnitPriceEditText.setText("");
            materialNotesEditText.setText("");
        }).addOnFailureListener(e -> Toast.makeText(this, "Error adding material", Toast.LENGTH_SHORT).show());
    }

    private void addServiceItem() {
        String description = serviceItemDescriptionEditText.getText().toString();
        String feeStr = serviceItemFeeEditText.getText().toString();

        if (description.isEmpty() || feeStr.isEmpty()) {
            Toast.makeText(this, "Service description and fee are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> serviceItem = new HashMap<>();
        serviceItem.put("workPlanId", currentWorkPlanId);
        serviceItem.put("description", description);
        serviceItem.put("serviceFee", Double.parseDouble(feeStr));
        serviceItem.put("notes", serviceItemNotesEditText.getText().toString());
        serviceItem.put("createdAt", new Timestamp(new Date()));

        db.collection("service_items").add(serviceItem).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Service item added", Toast.LENGTH_SHORT).show();
            serviceItemDescriptionEditText.setText("");
            serviceItemFeeEditText.setText("");
            serviceItemNotesEditText.setText("");
        }).addOnFailureListener(e -> Toast.makeText(this, "Error adding service item", Toast.LENGTH_SHORT).show());
    }

    private void fetchSubCollections(String workPlanId) {
        fetchTimeEntries(workPlanId);
        fetchMaterials(workPlanId);
        fetchServiceItems(workPlanId);
    }

    private void fetchTimeEntries(String workPlanId) {
        db.collection("time_entries").whereEqualTo("workPlanId", workPlanId).orderBy("createdAt", Query.Direction.DESCENDING).addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            timeEntryList.clear();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots) {
                    TimeEntry entry = doc.toObject(TimeEntry.class);
                    if (entry != null) {
                        entry.id = doc.getId();
                        timeEntryList.add(entry);
                    }
                }
            }
            timeEntryAdapter.notifyDataSetChanged();
        });
    }

    private void fetchMaterials(String workPlanId) {
        db.collection("materials").whereEqualTo("workPlanId", workPlanId).orderBy("createdAt", Query.Direction.DESCENDING).addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            materialList.clear();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots) {
                    Material material = doc.toObject(Material.class);
                    if (material != null) {
                        material.id = doc.getId();
                        materialList.add(material);
                    }
                }
            }
            materialAdapter.notifyDataSetChanged();
        });
    }

    private void fetchServiceItems(String workPlanId) {
        db.collection("service_items").whereEqualTo("workPlanId", workPlanId).orderBy("createdAt", Query.Direction.DESCENDING).addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            serviceItemList.clear();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots) {
                    ServiceItem item = doc.toObject(ServiceItem.class);
                    if (item != null) {
                        item.id = doc.getId();
                        serviceItemList.add(item);
                    }
                }
            }
            serviceItemAdapter.notifyDataSetChanged();
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

    private void toggleSection(LinearLayout content, ImageView toggle) {
        if (content.getVisibility() == View.VISIBLE) {
            content.setVisibility(View.GONE);
            toggle.animate().rotation(0).setDuration(200).start();
        } else {
            content.setVisibility(View.VISIBLE);
            toggle.animate().rotation(180).setDuration(200).start();
        }
    }
}
