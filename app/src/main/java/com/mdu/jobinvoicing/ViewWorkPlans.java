package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mdu.jobinvoicing.data.local.entities.WorkPlan;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;

import java.util.ArrayList;
import java.util.List;

public class ViewWorkPlans extends AppCompatActivity {

    private static final String TAG = "ViewWorkPlans";

    private RecyclerView workPlansRecyclerView;
    private WorkPlanAdapter workPlanAdapter;
    private List<WorkPlan> allWorkPlans; // all workplans from Firestore
    private List<WorkPlan> filteredWorkPlanList; // only the worklplans after search/filter
    private FirebaseFirestore db;
    private NetworkHelper networkHelper;
    private TextView statusText;
    private ImageView statusDot;

    private TextInputEditText searchEditText;
    private Spinner filterSpinner;

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

        allWorkPlans = new ArrayList<>();
        filteredWorkPlanList = new ArrayList<>();
        workPlanAdapter = new WorkPlanAdapter(this, filteredWorkPlanList);
        workPlansRecyclerView.setAdapter(workPlanAdapter);

        // --- Search and Filter --- //
        searchEditText = findViewById(R.id.searchEditText);
        filterSpinner = findViewById(R.id.filterSpinner);

        // Spinnern - load filteralternativ
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.work_plan_filter_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        // Spinner listener - updatea list när användare ändrar filter
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchWorkPlans();
            }
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Search text listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString()); // filter local list on change
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        fetchWorkPlans(); //initial fetch
    }

    private void fetchWorkPlans() {
        Query query = db.collection("workplans");

        // firestore query baserad on selected filter
        int selectedPosition = filterSpinner.getSelectedItemPosition();
        switch (selectedPosition) {
            case 0: // Date-newest
                query = query.orderBy("createdAt", Query.Direction.DESCENDING);
                break;
            case 1: // Date-oldest
                query = query.orderBy("createdAt", Query.Direction.ASCENDING);
                break;
            case 2: // Status: In Progress/Open i workplans
                query = query.whereEqualTo("status", "OPEN").orderBy("createdAt", Query.Direction.DESCENDING);
                break;
            case 3: // Status: Closed
                query = query.whereEqualTo("status", "CLOSED").orderBy("createdAt", Query.Direction.DESCENDING);
                break;
        }

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            allWorkPlans.clear();
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
                    WorkPlanStatus status = WorkPlanStatus.OPEN; // default value
                    if (statusStr != null) {
                        try {
                            status = WorkPlanStatus.valueOf(statusStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Invalid status value from Firestore: " + statusStr, e);
                        }
                    }

                    WorkPlan workPlan = new WorkPlan(id, title, clientName, location, startDate, 0, status);
                    workPlan.description = description;
                    allWorkPlans.add(workPlan);
                }
            }
            // efter fetch,apply current search term
            filterList(searchEditText.getText().toString());
        });
    }

    private void filterList(String searchTerm) {
        filteredWorkPlanList.clear();
        if (searchTerm.isEmpty()) {
            filteredWorkPlanList.addAll(allWorkPlans);
        } else {
            String lowerCaseSearchTerm = searchTerm.toLowerCase();
            for (WorkPlan plan : allWorkPlans) {
                boolean titleMatches = plan.title != null && plan.title.toLowerCase().contains(lowerCaseSearchTerm);
                boolean clientMatches = plan.clientName != null && plan.clientName.toLowerCase().contains(lowerCaseSearchTerm);
                if (titleMatches || clientMatches) {
                    filteredWorkPlanList.add(plan);
                }
            }
        }
        workPlanAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkHelper != null) {
            networkHelper.stopMonitoring();
        }
    }
}
