package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_work_plans);

        db = FirebaseFirestore.getInstance();

        workPlansRecyclerView = findViewById(R.id.workPlansRecyclerView);
        workPlansRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        workPlanList = new ArrayList<>();
        workPlanAdapter = new WorkPlanAdapter(workPlanList);
        workPlansRecyclerView.setAdapter(workPlanAdapter);

        fetchWorkPlans();
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
}
