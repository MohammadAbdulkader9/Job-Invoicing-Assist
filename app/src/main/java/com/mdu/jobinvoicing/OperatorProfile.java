package com.mdu.jobinvoicing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mdu.jobinvoicing.data.local.entities.Operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorProfile extends AppCompatActivity {

    private EditText etFullName, etRole, etEmail, etPhone;
    private Button btnSave;
    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    private OperatorAdapter adapter;
    private List<Operator> operatorList = new ArrayList<>();

    private static final String TAG = "OperatorProfileDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_profile);

        // Apply padding for system bars (status bar / navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(OperatorProfile.this, HomePage.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_create_invoice) {
                Intent intent = new Intent(OperatorProfile.this, Invoices.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.rvOperators);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new OperatorAdapter(operatorList, new OperatorAdapter.OnOperatorClickListener() {
            @Override
            public void onSetActive(Operator operator) { }

            @Override
            public void onEdit(Operator operator) { }

            @Override
            public void onDelete(Operator operator) { }
        });

        recyclerView.setAdapter(adapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        etFullName = findViewById(R.id.etFullName);
        etRole = findViewById(R.id.etRole);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);

        etRole.setText("Operator");
        etRole.setEnabled(false);

        btnSave.setOnClickListener(v -> saveProfile());

        // Load operators from Firestore
        loadOperatorsFromFirestore();
    }

    private void loadOperatorsFromFirestore() {
        db.collection("operators")
                .get()
                .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                    operatorList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Operator operator = new Operator(
                                doc.getId(),
                                doc.getString("fullName"),
                                doc.getString("phone"),
                                doc.getString("email"),
                                doc.getString("role"),
                                true // or parse if you have a field for active status
                        );
                        operatorList.add(operator);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load operators", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading operators: " + e.getMessage());
                });
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String role = "Operator";
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("fullName", fullName);
        profile.put("role", role);
        profile.put("email", email);
        profile.put("phone", phone);
        profile.put("createdAt", FieldValue.serverTimestamp());

        Log.d(TAG, "Saving profile: " + profile);

        db.collection("operators")
                .add(profile)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Firestore saved with ID: " + doc.getId());

                    // Add newly saved operator directly to the list and update adapter
                    Operator operator = new Operator(
                            doc.getId(),
                            fullName,
                            phone,
                            email,
                            role,
                            true
                    );
                    operatorList.add(operator);
                    adapter.notifyItemInserted(operatorList.size() - 1);

                    resetForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Firestore save failed: " + e.getMessage());
                });
    }

    private void resetForm() {
        etFullName.setText("");
        etEmail.setText("");
        etPhone.setText("");
    }
}
