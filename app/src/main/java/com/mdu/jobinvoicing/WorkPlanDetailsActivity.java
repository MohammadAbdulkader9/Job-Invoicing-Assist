package com.mdu.jobinvoicing;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // auto-save feature
import android.os.Looper; // auto-save feature
import android.text.Editable; // auto-save feature
import android.text.TextWatcher; // auto-save feature
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query; // auto-save feature
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.StopWatchTimerItemDao;
import com.mdu.jobinvoicing.data.local.entities.Material;
import com.mdu.jobinvoicing.data.local.entities.ServiceItem;
import com.mdu.jobinvoicing.data.local.entities.StopWatchTimerItem;
import com.mdu.jobinvoicing.data.local.entities.TimeEntry;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WorkPlanDetailsActivity extends AppCompatActivity implements TimeEntryAdapter.OnDeleteClickListener, MaterialAdapter.OnDeleteClickListener, ServiceItemAdapter.OnDeleteClickListener {

    private static final String TAG = "WorkPlanDetailsActivity";

    private EditText customerNameEditText, workTitleEditText, workDescriptionEditText, locationEditText, dateEditText;
    private Button saveButton, saveTimeEntryButton, addMaterialButton, addServiceItemButton, importFromTimerButton;
    private ImageView backButton;
    private RadioGroup statusRadioGroup;
    private RadioButton statusOpen, statusInProgress, statusClosed;

    private FirebaseFirestore db;
    private ListenerRegistration timeEntriesListener;
    private ListenerRegistration materialsListener;
    private ListenerRegistration serviceItemsListener;
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

    // auto-save feature
    private Handler autoSaveHandler = new Handler(Looper.getMainLooper());
    private Runnable autoSaveRunnable;
    private boolean isProgrammaticChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplan_details);

        db = FirebaseFirestore.getInstance();

        customerNameEditText = findViewById(R.id.customerNameEditText);
        workTitleEditText = findViewById(R.id.workTitleEditText);
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

        saveTimeEntryButton = findViewById(R.id.addTimeEntryButton);
        addMaterialButton = findViewById(R.id.addMaterialButton);
        addServiceItemButton = findViewById(R.id.addServiceItemButton);
        importFromTimerButton = findViewById(R.id.importFromTimerButton);

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
        setupAutoSave(); // auto-save feature

        backButton.setOnClickListener(v -> finish());
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        timeEntryHeader.setOnClickListener(v -> toggleSection(timeEntryContent, timeEntryToggle));
        materialHeader.setOnClickListener(v -> toggleSection(materialContent, materialToggle));
        serviceItemHeader.setOnClickListener(v -> toggleSection(serviceItemContent, serviceItemToggle));

        saveTimeEntryButton.setOnClickListener(v -> addTimeEntry());
        addMaterialButton.setOnClickListener(v -> addMaterial());
        addServiceItemButton.setOnClickListener(v -> addServiceItem());
        importFromTimerButton.setOnClickListener(v -> importFromTimer());


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("WORK_PLAN_ID")) {
            currentWorkPlanId = intent.getStringExtra("WORK_PLAN_ID");
            if (currentWorkPlanId != null && !currentWorkPlanId.isEmpty()) {
                setupRecyclerViews(); //Re-initialize adapters with the work plan ID
                loadWorkPlanData(currentWorkPlanId);
                fetchSubCollections(currentWorkPlanId);
            }
        }
    }
    // auto-save feature
    private void setupAutoSave() {
        saveButton.setVisibility(View.GONE);
        autoSaveRunnable = this::saveWorkPlanChanges;

        TextWatcher autoSaveTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Unused */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isProgrammaticChange) {
                    autoSaveHandler.removeCallbacks(autoSaveRunnable);
                    autoSaveHandler.postDelayed(autoSaveRunnable, 2000); // 2-second delay
                }
            }

            @Override
            public void afterTextChanged(Editable s) { /* Unused */ }
        };

        customerNameEditText.addTextChangedListener(autoSaveTextWatcher);
        workTitleEditText.addTextChangedListener(autoSaveTextWatcher);
        workDescriptionEditText.addTextChangedListener(autoSaveTextWatcher);
        locationEditText.addTextChangedListener(autoSaveTextWatcher);

        statusRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isProgrammaticChange) {
                saveWorkPlanChanges();
            }
        });
    }

    private void setupRecyclerViews() {
        timeEntriesRecyclerView = findViewById(R.id.timeEntriesRecyclerView);
        timeEntriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeEntryAdapter = new TimeEntryAdapter(timeEntryList, currentWorkPlanId, this);
        timeEntriesRecyclerView.setAdapter(timeEntryAdapter);

        materialsRecyclerView = findViewById(R.id.materialsRecyclerView);
        materialsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        materialAdapter = new MaterialAdapter(materialList, currentWorkPlanId, this);
        materialsRecyclerView.setAdapter(materialAdapter);

        serviceItemsRecyclerView = findViewById(R.id.serviceItemsRecyclerView);
        serviceItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceItemAdapter = new ServiceItemAdapter(serviceItemList, currentWorkPlanId, this);
        serviceItemsRecyclerView.setAdapter(serviceItemAdapter);
    }

    @Override
    public void onDeleteClick(TimeEntry timeEntry) {
        deleteTimeEntry(timeEntry);
    }

    @Override
    public void onDeleteClick(Material material) {
        deleteMaterial(material);
    }

    @Override
    public void onDeleteClick(ServiceItem serviceItem) {
        deleteServiceItem(serviceItem);
    }

    private void deleteTimeEntry(TimeEntry timeEntry) {
        if (timeEntry.getId() == null || timeEntry.getId().isEmpty()) {
            Toast.makeText(this, "Cannot delete, time entry ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("workplans").document(currentWorkPlanId).collection("time_entries").document(timeEntry.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int indexToRemove = -1;
                    for (int i = 0; i < timeEntryList.size(); i++) {
                        if (timeEntry.getId().equals(timeEntryList.get(i).getId())) {
                            indexToRemove = i;
                            break;
                        }
                    }
                    if (indexToRemove != -1) {
                        timeEntryList.remove(indexToRemove);
                        timeEntryAdapter.notifyItemRemoved(indexToRemove);
                    } else {
                        timeEntryAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(WorkPlanDetailsActivity.this, "Time entry deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(WorkPlanDetailsActivity.this, "Error deleting time entry: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void deleteMaterial(Material material) {
        if (material.getId() == null || material.getId().isEmpty()) {
            Toast.makeText(this, "Cannot delete, material ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("workplans").document(currentWorkPlanId).collection("materials").document(material.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int indexToRemove = -1;
                    for (int i = 0; i < materialList.size(); i++) {
                        if (material.getId().equals(materialList.get(i).getId())) {
                            indexToRemove = i;
                            break;
                        }
                    }
                    if (indexToRemove != -1) {
                        materialList.remove(indexToRemove);
                        materialAdapter.notifyItemRemoved(indexToRemove);
                    } else {
                        materialAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(WorkPlanDetailsActivity.this, "Material deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(WorkPlanDetailsActivity.this, "Error deleting material: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void deleteServiceItem(ServiceItem serviceItem) {
        if (serviceItem.getId() == null || serviceItem.getId().isEmpty()) {
            Toast.makeText(this, "Cannot delete, service item ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("workplans").document(currentWorkPlanId).collection("service_items").document(serviceItem.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int indexToRemove = -1;
                    for (int i = 0; i < serviceItemList.size(); i++) {
                        if (serviceItem.getId().equals(serviceItemList.get(i).getId())) {
                            indexToRemove = i;
                            break;
                        }
                    }
                    if (indexToRemove != -1) {
                        serviceItemList.remove(indexToRemove);
                        serviceItemAdapter.notifyItemRemoved(indexToRemove);
                    } else {
                        serviceItemAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(WorkPlanDetailsActivity.this, "Service item deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(WorkPlanDetailsActivity.this, "Error deleting service item: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadWorkPlanData(String workPlanId) {
        DocumentReference docRef = db.collection("workplans").document(workPlanId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    isProgrammaticChange = true;
                    customerNameEditText.setText(document.getString("clientName"));
                    workTitleEditText.setText(document.getString("workTitle"));
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
                    isProgrammaticChange = false;
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
            return; // Don't save if there's no ID
        }

        String clientName = customerNameEditText.getText().toString().trim();
        String workTitle = workTitleEditText.getText().toString().trim();
        String workDescription = workDescriptionEditText.getText().toString().trim();
        String workLocation = locationEditText.getText().toString().trim();
        String dateStr = dateEditText.getText().toString().trim();

        int selectedStatusId = statusRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedStatusId);
        String statusStr = selectedRadioButton.getText().toString().toUpperCase().replace(" ", "_");
        WorkPlanStatus status = WorkPlanStatus.valueOf(statusStr);

        Map<String, Object> updatedWorkPlan = new HashMap<>();
        updatedWorkPlan.put("clientName", clientName);
        updatedWorkPlan.put("workTitle", workTitle);
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

        double hours = Double.parseDouble(hoursStr);
        double hourRate = Double.parseDouble(hourRateStr);
        double total = hours * hourRate;

        Map<String, Object> timeEntry = new HashMap<>();
        timeEntry.put("workPlanId", currentWorkPlanId);
        timeEntry.put("hours", hours);
        timeEntry.put("hourRate", hourRate);
        timeEntry.put("total", total);
        timeEntry.put("notes", timeEntryNotesEditText.getText().toString());
        timeEntry.put("createdAt", new Timestamp(new Date()));

        db.collection("workplans").document(currentWorkPlanId).collection("time_entries").add(timeEntry).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Time entry added", Toast.LENGTH_SHORT).show();
            timeEntryHoursEditText.setText("");
            timeEntryHourRateEditText.setText("");
            timeEntryNotesEditText.setText("");
        }).addOnFailureListener(e -> Toast.makeText(this, "Error adding time entry", Toast.LENGTH_SHORT).show());
        fetchSubCollections(currentWorkPlanId);
    }

    private void addMaterial() {
        String name = materialNameEditText.getText().toString();
        String quantityStr = materialQuantityEditText.getText().toString();
        String unitPriceStr = materialUnitPriceEditText.getText().toString();

        if (name.isEmpty() || quantityStr.isEmpty() || unitPriceStr.isEmpty()) {
            Toast.makeText(this, "Material name, quantity, and unit price are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity = Double.parseDouble(quantityStr);
        double unitPrice = Double.parseDouble(unitPriceStr);
        double total = quantity * unitPrice;

        Map<String, Object> material = new HashMap<>();
        material.put("workPlanId", currentWorkPlanId);
        material.put("name", name);
        material.put("quantity", quantity);
        material.put("unit", materialUnitEditText.getText().toString());
        material.put("unitPrice", unitPrice);
        material.put("total", total);
        material.put("notes", materialNotesEditText.getText().toString());
        material.put("createdAt", new Timestamp(new Date()));

        db.collection("workplans").document(currentWorkPlanId).collection("materials").add(material).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Material added", Toast.LENGTH_SHORT).show();
            materialNameEditText.setText("");
            materialQuantityEditText.setText("");
            materialUnitEditText.setText("");
            materialUnitPriceEditText.setText("");
            materialNotesEditText.setText("");
        }).addOnFailureListener(e -> Toast.makeText(this, "Error adding material", Toast.LENGTH_SHORT).show());
        fetchSubCollections(currentWorkPlanId);
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

        db.collection("workplans").document(currentWorkPlanId).collection("service_items").add(serviceItem).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Service item added", Toast.LENGTH_SHORT).show();
            serviceItemDescriptionEditText.setText("");
            serviceItemFeeEditText.setText("");
            serviceItemNotesEditText.setText("");
        }).addOnFailureListener(e -> Toast.makeText(this, "Error adding service item", Toast.LENGTH_SHORT).show());
        fetchSubCollections(currentWorkPlanId);
    }

    private void fetchSubCollections(String workPlanId) {
        fetchTimeEntries(workPlanId);
        fetchMaterials(workPlanId);
        fetchServiceItems(workPlanId);
    }

    private void fetchTimeEntries(String workPlanId) {
        if (timeEntriesListener != null) timeEntriesListener.remove();

        timeEntriesListener = db.collection("workplans").document(workPlanId).collection("time_entries")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    timeEntryList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            TimeEntry entry = doc.toObject(TimeEntry.class);
                            if (entry != null) {
                                entry.setId(doc.getId());
                                timeEntryList.add(entry);
                            }
                        }
                    }
                    timeEntryAdapter.notifyDataSetChanged();
                });
    }

    private void fetchMaterials(String workPlanId) {
        if (materialsListener != null) materialsListener.remove();

        materialsListener = db.collection("workplans").document(workPlanId).collection("materials")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    materialList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            Material material = doc.toObject(Material.class);
                            if (material != null) {
                                material.setId(doc.getId());
                                materialList.add(material);
                            }
                        }
                    }
                    materialAdapter.notifyDataSetChanged();
                });
    }

    private void fetchServiceItems(String workPlanId) {
        if (serviceItemsListener != null) serviceItemsListener.remove();

        serviceItemsListener = db.collection("workplans").document(workPlanId).collection("service_items")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    serviceItemList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            ServiceItem item = doc.toObject(ServiceItem.class);
                            if (item != null) {
                                item.setId(doc.getId());
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
                    dateEditText.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, monthOfYear + 1, year1));
                    saveWorkPlanChanges();
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

    private void importFromTimer() {
        AppDatabase localDb = AppDatabase.getInstance(this);
        StopWatchTimerItemDao timerDao = localDb.timerItemDao();
        timerDao.getAllTimerItems().observe(this, timerItems -> {
            if (timerItems != null && !timerItems.isEmpty()) {
                showTimerSelectionDialog(timerItems);
            } else {
                Toast.makeText(this, "No timers available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTimerSelectionDialog(List<StopWatchTimerItem> timerItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Timer");

        TimerAdapter adapter = new TimerAdapter(this, timerItems);

        builder.setAdapter(adapter, (dialog, which) -> {
            StopWatchTimerItem selectedTimer = timerItems.get(which);
            long elapsedMillis = selectedTimer.getElapsedMillis();
            double hours = (double) elapsedMillis / TimeUnit.HOURS.toMillis(1);

            timeEntryHoursEditText.setText(String.format(Locale.getDefault(), "%.2f", hours));
            timeEntryNotesEditText.setText(selectedTimer.getName());
        });

        builder.show();
    }

    private class TimerAdapter extends ArrayAdapter<StopWatchTimerItem> {

        public TimerAdapter(Context context, List<StopWatchTimerItem> timers) {
            super(context, 0, timers);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_stopwatch_timer, parent, false);
            }

            StopWatchTimerItem timer = getItem(position);

            TextView timerName = convertView.findViewById(R.id.timer_name);
            TextView timerValue = convertView.findViewById(R.id.timer_value);

            if (timer != null) {
                timerName.setText(timer.getName());
                long millis = timer.getElapsedMillis();
                long hours = TimeUnit.MILLISECONDS.toHours(millis);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
                timerValue.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            }

            return convertView;
        }
    }


    @Override
    protected void onDestroy() {
        autoSaveHandler.removeCallbacks(autoSaveRunnable);
        if (timeEntriesListener != null) timeEntriesListener.remove();
        if (materialsListener != null) materialsListener.remove();
        if (serviceItemsListener != null) serviceItemsListener.remove();
        super.onDestroy();
    }
}
