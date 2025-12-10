package com.mdu.jobinvoicing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TicketListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private TicketAdapter adapter;
    private List<LoadTicket> tickets;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);

        // Back button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // RecyclerView setup
        rv = findViewById(R.id.rvTickets);
        rv.setLayoutManager(new LinearLayoutManager(this));

        tickets = new ArrayList<>();
        adapter = new TicketAdapter(tickets);
        rv.setAdapter(adapter);

        // Item click listener
        adapter.setOnItemClickListener(ticket -> {
            Intent intent = new Intent(TicketListActivity.this, WorkPlanActivity.class);
            intent.putExtra("clientName", ticket.customer);
            intent.putExtra("workTitle", ticket.projectReference);
            intent.putExtra("workDescription", ticket.freeText);
            intent.putExtra("workLocation", ticket.targetName);
            startActivity(intent);
        });

        // Start loading tickets
        startAutoReload();
    }

    private void startAutoReload() {
        tickets.clear(); // Clear old tickets
        final List<LoadTicket> allTickets = new ArrayList<>();

        // Read CSV from assets
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("receipt.csv")))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }

                String[] parts = line.split(",", -1);
                if (parts.length < 11) {
                    Log.e("CSV", "Invalid line: " + line);
                    continue;
                }

                LoadTicket ticket = new LoadTicket(
                        parts[0], parts[1], parts[2], parts[3], parts[4],
                        parts[5], parts[6], parts[7], parts[8], parts[9], parts[10]
                );

                allTickets.add(ticket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CSV", "Failed to read CSV", e);
            return;
        }

        // Show tickets one by one every 2 seconds
        final int[] index = {0};
        runnable = new Runnable() {
            @Override
            public void run() {
                if (index[0] < allTickets.size()) {
                    tickets.add(allTickets.get(index[0]));
                    adapter.notifyItemInserted(tickets.size() - 1);
                    rv.scrollToPosition(tickets.size() - 1); // auto scroll
                    index[0]++;
                    handler.postDelayed(this, 2000); // 2 seconds delay
                } else {
                    // Reload CSV after 2 minutes
                    handler.postDelayed(() -> startAutoReload(), 120000); // 2 minutes
                }
            }
        };

        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
