package com.mdu.jobinvoicing;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
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


    private TextView statusText;
    private ImageView statusDot;

    // Middle cards
    private MaterialCardView cardCreateWorkPlan, cardViewJobs, cardTimeTracking;

    // Bottom navigation items
    private LinearLayout navNewJobs, navCreateInvoices, navSyncNow;

    private ConnectivityManager.NetworkCallback networkCallback;

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
        //navSyncNow         = findViewById(R.id.navSyncNow);Commenting it out as no longer needed
         //offline-online functionality
        statusText = findViewById(R.id.SyncText);
        statusDot = findViewById(R.id.statusDot);



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
            Intent intent = new Intent(HomePage.this, WorkPlanActivity.class);
            startActivity(intent);
        });

        MaterialCardView cardTimeTracking = findViewById(R.id.cardTimeTracking);
        cardTimeTracking.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, StopWatchTimer.class);
            startActivity(intent);
        });

        MaterialCardView cardViewJobs = findViewById(R.id.cardViewJobs);
        cardViewJobs.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, ViewWorkPlans.class);
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
        setupNetworkCallback();
    }
    private void setupNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> updateStatus(true));
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> updateStatus(false));
            }
        };

        cm.registerDefaultNetworkCallback(networkCallback);

        // Set initial status
        updateStatus(isOnline());
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    private void updateStatus(boolean online) {
        if (statusDot != null && statusText != null) {
            if (online) {
                statusDot.setColorFilter(Color.parseColor("#4CAF50")); // green
                statusText.setText("Online");
            } else {
                statusDot.setColorFilter(Color.parseColor("#9E9E9E")); // grey
                statusText.setText("Offline");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && networkCallback != null) {
            cm.unregisterNetworkCallback(networkCallback);
        }
    }
}
