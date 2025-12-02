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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class WorkPlanDetailsActivity extends AppCompatActivity {

    private static final String TAG = "WorkPlanDetailsActivity";

    private EditText customerNameEditText, jobDescriptionEditText, addressEditText, dateEditText;
    private Button saveButton;
    private ImageView backButton;
    private LinearLayout navNewJobs, navCreateInvoices, navSyncNow;

    private FirebaseFirestore db;
    private String currentJobId;


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


        // Set OnClickListener for the back button
        backButton.setOnClickListener(v -> finish());

        // Set OnClickListener for the date EditText to show a DatePickerDialog
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Set OnClickListener for the save button
        saveButton.setOnClickListener(v -> {
            // TODO: Implement save logic here to update the document in Firestore
            Toast.makeText(WorkPlanDetailsActivity.this, "Save Changes Clicked", Toast.LENGTH_SHORT).show();
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