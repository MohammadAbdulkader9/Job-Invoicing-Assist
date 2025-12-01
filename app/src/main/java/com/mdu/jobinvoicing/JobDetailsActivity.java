package com.mdu.jobinvoicing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class JobDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views from the layout
        TextView jobTitleTextView = findViewById(R.id.jobDetailsTitle);
        TextView jobIdTextView = findViewById(R.id.jobDetailsId);
        TextView jobStatusTextView = findViewById(R.id.jobDetailsStatus);
        TextView startTimeTextView = findViewById(R.id.startTime);
        TextView endTimeTextView = findViewById(R.id.endTime);
        Button viewInvoiceBtn = findViewById(R.id.viewInvoiceBtn);
        Button generateInvoiceBtn = findViewById(R.id.generateInvoiceBtn);

        // Get the intent that started this activity
        Intent intent = getIntent();
        if (intent != null) {
            // Extract the data from the intent
            String id = intent.getStringExtra("JOB_ID");
            String title = intent.getStringExtra("JOB_TITLE");
            String status = intent.getStringExtra("JOB_STATUS");
            String startTime = intent.getStringExtra("JOB_START_TIME");
            String endTime = intent.getStringExtra("JOB_END_TIME");

            // Set the data to the views
            jobTitleTextView.setText(title);
            jobIdTextView.setText("Job ID: " + id);
            jobStatusTextView.setText(status);
            startTimeTextView.setText(startTime); // Corrected: No prefix
            endTimeTextView.setText(endTime);     // Corrected: No prefix

            // Set status icon dynamically
            if ("In Progress".equalsIgnoreCase(status)) {
                jobStatusTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jobbs_list_status_in_progress, 0, 0, 0);
            } else if ("Closed".equalsIgnoreCase(status)) {
                jobStatusTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jobbs_list_status_closed, 0, 0, 0);
            }

            // Show/hide buttons based on the job status
            if ("Closed".equalsIgnoreCase(status)) {
                viewInvoiceBtn.setVisibility(View.VISIBLE);
                generateInvoiceBtn.setVisibility(View.VISIBLE);
            } else if ("In Progress".equalsIgnoreCase(status)) {
                viewInvoiceBtn.setVisibility(View.GONE);
                generateInvoiceBtn.setVisibility(View.VISIBLE);
            } else {
                viewInvoiceBtn.setVisibility(View.GONE);
                generateInvoiceBtn.setVisibility(View.GONE);
            }
        }
    }
}
