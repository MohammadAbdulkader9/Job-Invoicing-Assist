package com.mdu.jobinvoicing;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.IBinder;
import android.util.Log;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mdu.jobinvoicing.data.network.financeApi.ApiServiceManager;

public class HomePage extends AppCompatActivity {


    private TextView statusText;
    private ImageView statusDot;
    private NetworkHelper networkHelper;
    private TextView greetingTextView;

    // Middle cards
    private MaterialCardView cardCreateWorkPlan, cardViewJobs, cardTimeTracking;

    // Bottom navigation items
    private BottomNavigationView bottomNavigationView;

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

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "HomePage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        greetingTextView = findViewById(R.id.greetingTextView);
        cardCreateWorkPlan = findViewById(R.id.cardCreateWorkPlan);
        cardViewJobs       = findViewById(R.id.cardViewWorkPlans);
        //cardTimeTracking   = findViewById(R.id.cardTimeTracking);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

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
            Intent intent = new Intent(HomePage.this, StopWatchTimerActivity.class);
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

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already on Home, do nothing
                return true;
            } else if (itemId == R.id.nav_create_invoice) {
                Intent intent = new Intent(HomePage.this, Invoices.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(HomePage.this, OperatorProfile.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void fetchOperatorName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d(TAG, "Fetching operator for user ID: " + userId);
            db.collection("users").document(userId)
                    .collection("operators")
                    .whereEqualTo("isActive", true)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Log.d(TAG, "No active operator found for user ID: " + userId);
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String fullName = document.getString("fullName");
                                    Log.d(TAG, "Found operator: " + fullName);
                                    greetingTextView.setText("Hello, " + fullName);
                                }
                            }
                        } else {
                            greetingTextView.setText("Please create or select a profile");
                            Log.w(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            greetingTextView.setText("Please create or select a profile");
            Log.d(TAG, "Cannot fetch operator, user is not signed in.");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind the MockLoadAssistService to start reading CSV
        Intent intent = new Intent(this, MockLoadAssistService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            signInAnonymously();
        } else {
            fetchOperatorName();
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success");
                        fetchOperatorName();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.getException());
                        Toast.makeText(HomePage.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
