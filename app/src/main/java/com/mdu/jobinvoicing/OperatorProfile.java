package com.mdu.jobinvoicing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.INVISIBLE);

        db = FirebaseFirestore.getInstance();

        // ---------------------------
        // PHONE AUTO +46 SETUP
        // ---------------------------
        etPhone.setText("+46");
        etPhone.setSelection(etPhone.getText().length());

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("+46")) {
                    etPhone.setText("+46");
                    etPhone.setSelection(etPhone.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // ---------------------------
        // ADAPTER
        // ---------------------------
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
    // HELPERS: PHONE NORMALIZATION + VALIDATION
    // ---------------------------------------------------
    private String normalizeSwedishPhone(String phone) {
        phone = phone.replaceAll("\\s+", ""); // remove spaces
        if (phone.startsWith("07")) {
            return "+46" + phone.substring(1);
        }
        return phone;
    }

    private boolean isValidSwedishMobile(String phone) {
        return phone.matches("^\\+467\\d{8}$");
    }

    // ---------------------------------------------------
    // VALIDATION (EMAIL + PHONE)
    // ---------------------------------------------------
    private boolean isValidInput() {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = normalizeSwedishPhone(
                etPhone.getText().toString().trim()
        );

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (!isValidSwedishMobile(phone)) {
            etPhone.setError("Enter valid Swedish mobile (+46 7XXXXXXXX or 07XXXXXXXX)");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }

    // ---------------------------------------------------
    // LOAD OPERATORS
    // ---------------------------------------------------
    private void loadOperatorsFromFirestore() {
        db.collection("operators")
                .get()
                .addOnSuccessListener((QuerySnapshot snapshots) -> {
                    operatorList.clear();

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        boolean isActive = doc.getBoolean("isActive") != null
                                && doc.getBoolean("isActive");

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
                    Log.e(TAG, "Load error: " + e.getMessage());
                });
    }

    // ---------------------------------------------------
    // SAVE OPERATOR
    // ---------------------------------------------------
    private void saveProfile() {

        if (!isValidInput()) return;

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = normalizeSwedishPhone(
                etPhone.getText().toString().trim()
        );

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

                    Operator operator = new Operator(
                            doc.getId(),
                            fullName,
                            phone,
                            email,
                            "Operator",
                            false
                    );

                    operatorList.add(operator);
                    adapter.notifyItemInserted(operatorList.size() - 1);

                    resetForm();
                    Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show());
    }

    // ---------------------------------------------------
    // EDIT
    // ---------------------------------------------------
    private void startEditing(Operator operator) {
        editingOperatorId = operator.id;

        etFullName.setText(operator.name);
        etEmail.setText(operator.email);
        etPhone.setText(operator.phone);

        btnSave.setText("Update Profile");
        btnCancel.setVisibility(View.VISIBLE);
    }

    private void cancelEditing() {
        editingOperatorId = null;
        resetForm();

        btnSave.setText("Save Profile");
        btnCancel.setVisibility(View.INVISIBLE);

        Toast.makeText(this, "Editing cancelled", Toast.LENGTH_SHORT).show();
    }

    // ---------------------------------------------------
    // UPDATE OPERATOR
    // ---------------------------------------------------
    private void updateOperator() {

        if (!isValidInput()) return;

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = normalizeSwedishPhone(
                etPhone.getText().toString().trim()
        );

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
    // RESET FORM
    // ---------------------------------------------------
    private void resetForm() {
        etFullName.setText("");
        etEmail.setText("");
        etPhone.setText("+46");
        etPhone.setSelection(etPhone.getText().length());
    }

    // ---------------------------------------------------
    // SET ACTIVE OPERATOR
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
    // DELETE OPERATOR
    // ---------------------------------------------------
    private void deleteOperator(Operator operator) {

        db.collection("operators").document(operator.id)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    int index = operatorList.indexOf(operator);
                    operatorList.remove(index);
                    adapter.notifyItemRemoved(index);

                    if (editingOperatorId != null
                            && editingOperatorId.equals(operator.id)) {
                        cancelEditing();
                    }

                    Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show());
    }
}
