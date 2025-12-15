package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mdu.jobinvoicing.data.local.entities.WorkPlan;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ViewWorkPlans extends AppCompatActivity {

    private static final String TAG = "ViewWorkPlans";

    private RecyclerView workPlansRecyclerView;
    private WorkPlanAdapter workPlanAdapter;
    private List<WorkPlan> workPlanList;
    private List<WorkPlan> originalWorkPlanList;
    private FirebaseFirestore db;
    private NetworkHelper networkHelper;
    private TextView statusText;
    private ImageView statusDot;
    private EditText searchEditText;
    private ImageView filterButton;

    private String currentSort = "DateDesc"; // Default sort
    private WorkPlanStatus currentStatusFilter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workplans);

        // Apply padding for system bars (status bar / navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewWorkPlans), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        workPlansRecyclerView = findViewById(R.id.workPlansRecyclerView);
        workPlansRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        workPlanList = new ArrayList<>();
        originalWorkPlanList = new ArrayList<>();
        workPlanAdapter = new WorkPlanAdapter(this, workPlanList);
        workPlansRecyclerView.setAdapter(workPlanAdapter);

        searchEditText = findViewById(R.id.searchEditText);
        filterButton = findViewById(R.id.filterButton);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterWorkPlans(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        filterButton.setOnClickListener(this::showFilterMenu);


        fetchWorkPlans();

        // Set Top Bar Title
        MaterialToolbar toolbar = findViewById(R.id.top_bar);
        if (toolbar != null) {
            toolbar.setTitle("Work Plans");
            toolbar.setNavigationOnClickListener(v -> finish());
        }


        // offline-online functionality
        statusText = findViewById(R.id.SyncText);
        statusDot = findViewById(R.id.statusDot);

        networkHelper = new NetworkHelper(this, statusDot, statusText);
        networkHelper.startMonitoring();
    }

    private void fetchWorkPlans() {
        db.collection("workplans")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    originalWorkPlanList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String id = doc.getId();
                            String title = doc.getString("workTitle");
                            String clientName = doc.getString("clientName");
                            String description = doc.getString("workDescription");
                            String location = doc.getString("workLocation");

                            Timestamp startDateTimestamp = doc.getTimestamp("startDate");
                            long startDate = startDateTimestamp != null ? startDateTimestamp.toDate().getTime() : 0;

                            String statusStr = doc.getString("status");
                            WorkPlanStatus status = WorkPlanStatus.OPEN; // Default value
                            if (statusStr != null) {
                                try {
                                    status = WorkPlanStatus.valueOf(statusStr);
                                } catch (IllegalArgumentException e) {
                                    Log.e(TAG, "Invalid status value from Firestore: " + statusStr, e);
                                }
                            }

                            WorkPlan workPlan = new WorkPlan(id, title, clientName, location, startDate, 0, status);
                            workPlan.description = description;
                            originalWorkPlanList.add(workPlan);
                        }
                    }
                    filterWorkPlans(searchEditText.getText().toString());
                });
    }

    private void filterWorkPlans(String text) {
        List<WorkPlan> filteredList = new ArrayList<>(originalWorkPlanList);

        // Filter by status
        if (currentStatusFilter != null) {
            filteredList = filteredList.stream()
                    .filter(wp -> wp.getStatus() == currentStatusFilter)
                    .collect(Collectors.toList());
        }


        // Filter by search text
        if (text != null && !text.isEmpty()) {
            String lowerCaseQuery = text.toLowerCase();
            filteredList = filteredList.stream()
                    .filter(wp -> (wp.getWorkTitle() != null && wp.getWorkTitle().toLowerCase().contains(lowerCaseQuery)) ||
                            (wp.getClientName() != null && wp.getClientName().toLowerCase().contains(lowerCaseQuery)))
                    .collect(Collectors.toList());
        }

        // Sort
        switch (currentSort) {
            case "DateAsc":
                Collections.sort(filteredList, Comparator.comparingLong(WorkPlan::getStartDate));
                break;
            case "DateDesc":
                Collections.sort(filteredList, Comparator.comparingLong(WorkPlan::getStartDate).reversed());
                break;
        }

        workPlanList.clear();
        workPlanList.addAll(filteredList);
        workPlanAdapter.notifyDataSetChanged();
    }

    private void showFilterMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.sort_date_asc) {
                currentSort = "DateAsc";
            } else if (itemId == R.id.sort_date_desc) {
                currentSort = "DateDesc";
            } else if (itemId == R.id.status_open) {
                currentStatusFilter = WorkPlanStatus.OPEN;
            } else if (itemId == R.id.status_in_progress) {
                currentStatusFilter = WorkPlanStatus.IN_PROGRESS;
            } else if (itemId == R.id.status_closed) {
                currentStatusFilter = WorkPlanStatus.CLOSED;
            } else if (itemId == R.id.clear_filter) {
                currentStatusFilter = null;
            }

            filterWorkPlans(searchEditText.getText().toString());
            return true;
        });

        popup.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkHelper != null) {
            networkHelper.stopMonitoring();
        }
    }
}
