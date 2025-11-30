package com.mdu.jobinvoicing;

import android.content.Intent;
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

        //Arrays: Id, Cost, Title, Status, StartTime, EndTime
        final String jobbId[] = {"12345", "54321", "98765", "56789", "13579"};

        final String cost[] = {"$1000,00",
        "$20.000",
        "$30.000",
        "$40.000",
        "$50.000"};

        final String title[] = { "Snow Cleaning",
        "Brick Moving",
        "Brick Moving",
        "Digging",
        "Lifting"};

        final String status[] = {"In Progress",
        "Closed",
        "In Progress",
        "Closed",
        "Closed"};

        final String startTime[] = {"01.01.2021 11:21",
        "02.01.2021 09:00",
        "03.01.2021 13:00",
        "04.01.2021 08:30",
        "05.01.2021 10:00"};

        final String endTime[] = {"01.01.2021 17:30",
        "02.01.2021 16:00",
        "03.01.2021 18:00",
        "04.01.2021 17:00",
        "05.01.2021 15:30"};


        //Create/add custom adapter to list view
        jobsList = findViewById(R.id.JobsList);
        // Pass all arrays to the adapter, including start and end times, though they are not used in the list view itself
        MyListAdapter customAdapter = new MyListAdapter(this, jobbId, cost, title, status, startTime);
        jobsList.setAdapter(customAdapter);

        jobsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create an Intent to start JobDetailsActivity
                Intent intent = new Intent(ViewJobs.this, JobDetailsActivity.class);

                // Put the data of the clicked job into the Intent
                intent.putExtra("JOB_ID", jobbId[position]);
                intent.putExtra("JOB_TITLE", title[position]);
                intent.putExtra("JOB_COST", cost[position]);
                intent.putExtra("JOB_STATUS", status[position]);
                intent.putExtra("JOB_START_TIME", startTime[position]);
                intent.putExtra("JOB_END_TIME", endTime[position]);

                // Start the activity
                startActivity(intent);
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