package com.mdu.jobinvoicing;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ViewJobs extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_viewjobs);

        // Apply padding for system bars (status bar / navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        //set upp the filter drop down
        Spinner filterDropDown=findViewById(R.id.filterDropDown);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.ViewJobsFilters,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterDropDown.setAdapter(adapter);

        //set up the list view, doing it with made up values for now
        //should be filled in from database later
        ListView jobsList;

        //Arrays: Id, Cost, Title, Status
        String jobbId[] = {"01.02.2021",
        "02.43435.23",
        "044.232.546.4",
        "2.1354.453.2",
        "42.5423.32"};

        //should be an int or double?
        String cost[] = {"$1000,00",
        "$20.000,00",
        "$30.000,00",
        "$40.000,00",
        "$50.000,00"};

        String title[] = { "Snow Cleaning",
        "Brick Moving",
        "Brick Moving",
        "Digging",
        "Lifting"};

        String status[] = {"In Progress",
        "Closed",
        "In Progress",
        "Closed",
        "Closed"};


        //Create/add custom adapter to list view
        jobsList = findViewById(R.id.JobsList);
        MyListAdapter customAdapter = new MyListAdapter(this, jobbId, cost, title, status);
        jobsList.setAdapter(customAdapter);

        jobsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedJobId = jobbId[position];
                String selectedJobTitle = title[position];

                // Example action: Toast message, do something with the selected job
                Toast.makeText(ViewJobs.this, "Clicked: " + selectedJobId + " " + selectedJobTitle, Toast.LENGTH_SHORT).show();
            }
        });

        filterDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected item here
                String selectedItem = parent.getItemAtPosition(position).toString();
                // Example action: Toast message, do something with the selected item to use a filter
                 Toast.makeText(ViewJobs.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}
