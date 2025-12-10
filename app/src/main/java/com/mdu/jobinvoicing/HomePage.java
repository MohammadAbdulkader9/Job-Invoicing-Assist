package com.mdu.jobinvoicing;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.mdu.jobinvoicing.data.network.financeApi.ApiServiceManager;

public class HomePage extends AppCompatActivity {


    private TextView statusText;
    private ImageView statusDot;
    private NetworkHelper networkHelper;

    // Middle cards
    private MaterialCardView cardCreateWorkPlan, cardViewJobs, cardTimeTracking;

    // Bottom navigation items
    private LinearLayout navNewJobs, navCreateInvoices, navProfile;

    private MockLoadAssistService service;
    private boolean bound = false;
    private final ServiceConnection conn = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MockLoadAssistService.LocalBinder b = (MockLoadAssistService.LocalBinder) binder;
            service = b.getService();

            // Set listener to feed tickets into repository in real-time
            service.setOnReceiptCompletedListener(ticket -> TicketRepository.addTicket(ticket));

            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        // Init ApiService Manager, This should only be done ones through out the app
        // Refer to README.md under FinanceApi for examples of how to make api calls
        ApiServiceManager.createInstance(ApiServiceManager.ApiProvider.FORTNOX);

        // Apply padding for system bars (status bar / navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.workplan_details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set Top Bar Title
        MaterialToolbar toolbar = findViewById(R.id.top_bar);
        if (toolbar != null) {
            toolbar.setTitle("Home");
            toolbar.setNavigationIcon(null);
        }

        // ----- Find views -----
        cardCreateWorkPlan = findViewById(R.id.cardCreateWorkPlan);
        cardViewJobs       = findViewById(R.id.cardViewWorkPlans);
        //cardTimeTracking   = findViewById(R.id.cardTimeTracking);

        navNewJobs         = findViewById(R.id.navNewJobs);
        navCreateInvoices  = findViewById(R.id.navCreateInvoices);
        navProfile        = findViewById(R.id.navOperatorProfile);
        //navSyncNow         = findViewById(R.id.navSyncNow);Commenting it out as no longer needed


        //offline-online functionality
        statusText = findViewById(R.id.SyncText);
        statusDot = findViewById(R.id.statusDot);

        networkHelper = new NetworkHelper(this, statusDot, statusText);
        networkHelper.startMonitoring();


        // ----- Middle cards: main actions -----

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

        MaterialCardView cardViewJobs = findViewById(R.id.cardViewWorkPlans);
        cardViewJobs.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, ViewWorkPlans.class);
            startActivity(intent);
        });

        MaterialCardView cardLoadTicket = findViewById(R.id.cardLoadTicket);
        cardLoadTicket.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, TicketListActivity.class);
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
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, OperatorProfile.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Bind the MockLoadAssistService to start reading CSV
        Intent intent = new Intent(this, MockLoadAssistService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) unbindService(conn);
        bound = false;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkHelper != null) {
            networkHelper.stopMonitoring();
        }
    }
}
