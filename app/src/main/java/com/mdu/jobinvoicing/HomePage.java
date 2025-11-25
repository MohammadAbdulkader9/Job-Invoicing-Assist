package com.mdu.jobinvoicing;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class HomePage extends AppCompatActivity {

    // synced
    private TextView syncedUnsynced;
    private ImageView syncedDot;

    // Middle cards
    private MaterialCardView cardCreateWorkPlan, cardViewJobs, cardTimeTracking;

    // Bottom navigation items
    private LinearLayout navNewJobs, navCreateInvoices, navSyncNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        // Apply padding for system bars (status bar / navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ----- Find views -----
        cardCreateWorkPlan = findViewById(R.id.cardCreateWorkPlan);
        cardViewJobs       = findViewById(R.id.cardViewJobs);
        //cardTimeTracking   = findViewById(R.id.cardTimeTracking);

        navNewJobs         = findViewById(R.id.navNewJobs);
        navCreateInvoices  = findViewById(R.id.navCreateInvoices);
        navSyncNow         = findViewById(R.id.navSyncNow);

        syncedUnsynced = findViewById((R.id.SyncText));
        syncedDot = findViewById((R.id.statusDot));

        // ----- Middle cards: main actions -----

        cardCreateWorkPlan.setOnClickListener(v -> {
            // TODO: Navigate to "Create Work Plan" screen.
            // Example when you create the activity:
            // Intent intent = new Intent(HomePage.this, CreateWorkPlanActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "Create Work plan clicked", Toast.LENGTH_SHORT).show();
        });
        MaterialCardView  cardCreateWorkPlan = findViewById(R.id.cardCreateWorkPlan);
        cardCreateWorkPlan.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, Workplan.class);
            startActivity(intent);
        });
        cardViewJobs.setOnClickListener(v -> {
            // TODO: Navigate to "View Jobs" screen.
            // Intent intent = new Intent(HomePage.this, ViewJobsActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "View Jobs clicked", Toast.LENGTH_SHORT).show();
        });

        MaterialCardView cardTimeTracking = findViewById(R.id.cardTimeTracking);
        cardTimeTracking.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, StopWatchTimer.class);
            startActivity(intent);
        });


        // ----- Bottom navigation bar -----

        navNewJobs.setOnClickListener(v -> {
            // TODO: Bottom navigation destination: NewJobsActivity.
            // Steps:
            // 1) Create NewJobsActivity in package com.mdu.jobinvoicing
            // 2) Make sure it is added to AndroidManifest.xml
            // 3) Then use:
            //    Intent intent = new Intent(HomePage.this, NewJobsActivity.class);
            //    startActivity(intent);
            Toast.makeText(this, "Bottom: New Jobs", Toast.LENGTH_SHORT).show();
        });

        navCreateInvoices.setOnClickListener(v -> {
            // TODO: Bottom navigation destination: CreateInvoicesActivity.
            Intent intent = new Intent(HomePage.this, Invoices.class);
            startActivity(intent);
            //Toast.makeText(this, "Bottom: Create Invoices", Toast.LENGTH_SHORT).show();
        });

        navSyncNow.setOnClickListener(v -> {
            // TODO: Call your sync logic here (e.g., ViewModel → Repository → API).
            syncedDot.setImageTintList(ColorStateList.valueOf(Color.GREEN));
            syncedUnsynced.setText("Synced");
            Toast.makeText(this, "Bottom: Sync Now", Toast.LENGTH_SHORT).show();
        });
    }
}
