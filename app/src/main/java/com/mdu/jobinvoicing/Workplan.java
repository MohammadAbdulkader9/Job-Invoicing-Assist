package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class Workplan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText inputStartTime, inputEndTime;
        setContentView(R.layout.activity_workplan);
        ImageView backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(v -> {
            finish();
        });
        Button cancelBtn = findViewById(R.id.btnCancel);

        cancelBtn.setOnClickListener(v -> {
            finish();
        });

        // ===============================
        // Billing Model Dropdown
        // ===============================
        AutoCompleteTextView billingDropdown = findViewById(R.id.mySpinner);

        ArrayAdapter<CharSequence> billingAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.Billing_List,
                android.R.layout.simple_list_item_1
        );

        billingDropdown.setAdapter(billingAdapter);


        // ===============================
        // Materials Used Dropdown
        // ===============================
        AutoCompleteTextView materialDropdown = findViewById(R.id.my_material);

        ArrayAdapter<CharSequence> materialAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.Material_List,
                android.R.layout.simple_list_item_1
        );

        materialDropdown.setAdapter(materialAdapter);




        inputStartTime = findViewById(R.id.inputStartTime);
        inputEndTime = findViewById(R.id.inputEndTime);

        inputStartTime.setFocusable(false);
        inputEndTime.setFocusable(false);

        inputStartTime.setOnClickListener(v -> showTimePicker(inputStartTime));
        inputEndTime.setOnClickListener(v -> showTimePicker(inputEndTime));


    }
    private void showTimePicker(EditText targetEditText) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTitleText("Select time")
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build();

        picker.show(getSupportFragmentManager(), "time");

        picker.addOnPositiveButtonClickListener(v -> {
            String hour = String.format("%02d", picker.getHour());
            String minute = String.format("%02d", picker.getMinute());
            targetEditText.setText(hour + ":" + minute);
        });
    }
}
