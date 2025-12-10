package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
import java.util.List;

public class ViewWorkPlans extends AppCompatActivity {

    private static final String TAG = "ViewWorkPlans";

    private RecyclerView workPlansRecyclerView;
    private WorkPlanAdapter workPlanAdapter;
    private List<WorkPlan> workPlanList;
    private FirebaseFirestore db;
    private NetworkHelper networkHelper;
    private TextView statusText;
    private ImageView statusDot;

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
        workPlanAdapter = new WorkPlanAdapter(this, workPlanList);
        workPlansRecyclerView.setAdapter(workPlanAdapter);

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

                    workPlanList.clear();
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
                            workPlanList.add(workPlan);
                        }
                    }
                    workPlanAdapter.notifyDataSetChanged();
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkHelper != null) {
            networkHelper.stopMonitoring();
        }
    }
}
