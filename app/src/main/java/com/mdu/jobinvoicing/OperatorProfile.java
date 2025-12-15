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
import android.view.View;
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

    private EditText etFullName, etEmail, etPhone;
    private Button btnSave, btnCancel;

    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    private OperatorAdapter adapter;
    private List<Operator> operatorList = new ArrayList<>();

    private String editingOperatorId = null;
    private static final String TAG = "OperatorProfile";

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

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);      // Now from XML
        btnCancel.setVisibility(View.INVISIBLE);        // Keep layout even when hidden

        db = FirebaseFirestore.getInstance();

        adapter = new OperatorAdapter(operatorList, new OperatorAdapter.OnOperatorClickListener() {
            @Override
            public void onSetActive(Operator operator) {
                setActiveOperator(operator);
            }

            @Override
            public void onEdit(Operator operator) {
                startEditing(operator);
            }

            @Override
            public void onDelete(Operator operator) {
                deleteOperator(operator);
            }
        });

        recyclerView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            if (editingOperatorId == null) {
                saveProfile();
            } else {
                updateOperator();
            }
        });

        btnCancel.setOnClickListener(v -> cancelEditing());

        loadOperatorsFromFirestore();
    }

    // ---------------------------------------------------
    // Load all operators
    // ---------------------------------------------------
    private void loadOperatorsFromFirestore() {
        db.collection("operators")
                .get()
                .addOnSuccessListener((QuerySnapshot snapshots) -> {
                    operatorList.clear();

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        boolean isActive = doc.getBoolean("isActive") != null && doc.getBoolean("isActive");

                        Operator operator = new Operator(
                                doc.getId(),
                                doc.getString("fullName"),
                                doc.getString("phone"),
                                doc.getString("email"),
                                doc.getString("role"),
                                isActive
                        );

                        operatorList.add(operator);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load operators", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading: " + e.getMessage());
                });
    }

    // ---------------------------------------------------
    // Save new operator
    // ---------------------------------------------------
    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("fullName", fullName);
        profile.put("role", "Operator");
        profile.put("email", email);
        profile.put("phone", phone);
        profile.put("isActive", false);
        profile.put("createdAt", FieldValue.serverTimestamp());

        db.collection("operators")
                .add(profile)
                .addOnSuccessListener(doc -> {

                    Operator newOperator = new Operator(
                            doc.getId(),
                            fullName,
                            phone,
                            email,
                            "Operator",
                            false
                    );

                    operatorList.add(newOperator);
                    adapter.notifyItemInserted(operatorList.size() - 1);

                    resetForm();
                    Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show());
    }

    // ---------------------------------------------------
    // Start editing
    // ---------------------------------------------------
    private void startEditing(Operator operator) {
        editingOperatorId = operator.id;

        etFullName.setText(operator.name);
        etEmail.setText(operator.email);
        etPhone.setText(operator.phone);

        btnSave.setText("Update Profile");
        btnCancel.setVisibility(View.VISIBLE);
    }

    // ---------------------------------------------------
    // Cancel editing
    // ---------------------------------------------------
    private void cancelEditing() {
        editingOperatorId = null;
        resetForm();

        btnSave.setText("Save Profile");
        btnCancel.setVisibility(View.INVISIBLE);

        Toast.makeText(this, "Editing cancelled", Toast.LENGTH_SHORT).show();
    }

    // ---------------------------------------------------
    // Update operator
    // ---------------------------------------------------
    private void updateOperator() {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("fullName", fullName);
        updatedData.put("email", email);
        updatedData.put("phone", phone);

        db.collection("operators").document(editingOperatorId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {

                    for (Operator op : operatorList) {
                        if (op.id.equals(editingOperatorId)) {
                            op.name = fullName;
                            op.email = email;
                            op.phone = phone;
                            break;
                        }
                    }

                    adapter.notifyDataSetChanged();
                    cancelEditing();

                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    // ---------------------------------------------------
    // Reset form
    // ---------------------------------------------------
    private void resetForm() {
        etFullName.setText("");
        etEmail.setText("");
        etPhone.setText("");
    }

    // ---------------------------------------------------
    // Set active operator
    // ---------------------------------------------------
    private void setActiveOperator(Operator operator) {

        for (Operator op : operatorList) {
            db.collection("operators").document(op.id).update("isActive", false);
        }

        db.collection("operators").document(operator.id)
                .update("isActive", true);

        for (Operator op : operatorList) {
            op.isActive = op.id.equals(operator.id);
        }

        adapter.notifyDataSetChanged();
    }

    // ---------------------------------------------------
    // Delete operator
    // ---------------------------------------------------
    private void deleteOperator(Operator operator) {
        db.collection("operators").document(operator.id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int index = operatorList.indexOf(operator);

                    operatorList.remove(index);
                    adapter.notifyItemRemoved(index);

                    Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();

                    if (editingOperatorId != null && editingOperatorId.equals(operator.id)) {
                        cancelEditing();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
    }
}
