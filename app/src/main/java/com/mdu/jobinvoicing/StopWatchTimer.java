package com.mdu.jobinvoicing;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mdu.jobinvoicing.data.local.entities.TimerItem;

import java.util.ArrayList;
import java.util.Locale;

public class StopWatchTimer extends AppCompatActivity {

    private TextView stopwatchTimer, entryName;
    private MaterialButton startPauseButton, resetButton;
    private FloatingActionButton addEntryFab;
    private RecyclerView timersRecyclerView;

    private Handler handler;
    private final ArrayList<TimerItem> timers = new ArrayList<>();
    private TimerAdapter timerAdapter;
    private int currentIndex = -1;
    private boolean isRunning = false;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Update the main timer display if a timer is active
            if (isRunning && currentIndex != -1) {
                updateTimerText(timers.get(currentIndex));
            }
            // Periodically update the list to show elapsed times
            if (timerAdapter != null) {
                timerAdapter.notifyDataSetChanged();
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        // Initialize top-half views
        stopwatchTimer = findViewById(R.id.stopwatch_timer);
        entryName = findViewById(R.id.entry_name);
        startPauseButton = findViewById(R.id.stopwatch_start_pause);
        resetButton = findViewById(R.id.stopwatch_reset);

        // Initialize bottom-half views
        timersRecyclerView = findViewById(R.id.timers_recycler_view);
        addEntryFab = findViewById(R.id.add_entry_fab);

        // Set up RecyclerView
        timersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timerAdapter = new TimerAdapter();
        timersRecyclerView.setAdapter(timerAdapter);

        // Set up handler
        handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);

        // Set up button listeners
        addEntryFab.setOnClickListener(v -> showAddEntryDialog());

        startPauseButton.setOnClickListener(v -> {
            if (currentIndex == -1) {
                Toast.makeText(this, "Please select an entry first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        resetButton.setOnClickListener(v -> {
            if (currentIndex != -1) {
                pauseTimer();
                timers.get(currentIndex).reset();
                stopwatchTimer.setText("00:00:00");
                updateUI();
            }
        });

        updateUI();
    }

    private void startTimer() {
        if (currentIndex != -1 && !isRunning) {
            timers.get(currentIndex).start();
            isRunning = true;
            updateUI();
        }
    }

    private void pauseTimer() {
        if (currentIndex != -1 && isRunning) {
            timers.get(currentIndex).pause();
            isRunning = false;
            updateUI();
        }
    }

    private void selectTimer(int index) {
        if (isRunning) {
            pauseTimer();
        }
        currentIndex = index;
        updateUI();
        updateTimerText(timers.get(currentIndex));
    }

    private void updateTimerText(TimerItem timerItem) {
        long elapsedMillis = timerItem.getElapsedMillis();
        int hours = (int) (elapsedMillis / (1000 * 60 * 60));
        int minutes = (int) ((elapsedMillis / (1000 * 60)) % 60);
        int seconds = (int) (elapsedMillis / 1000) % 60;
        stopwatchTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void updateUI() {
        if (currentIndex == -1) {
            entryName.setText("No entry selected");
            stopwatchTimer.setText("00:00:00");
            startPauseButton.setEnabled(false);
            resetButton.setEnabled(false);
        } else {
            startPauseButton.setEnabled(true);
            resetButton.setEnabled(true);
            TimerItem current = timers.get(currentIndex);
            entryName.setText(current.getName());
        }

        // Update play/pause button icon
        if (isRunning) {
            startPauseButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_pause_24));
        } else {
            startPauseButton.setIcon(ContextCompat.getDrawable(this, R.drawable.baseline_play_arrow_24));
        }

        // Refresh the list
        timerAdapter.notifyDataSetChanged();
    }

    private void showAddEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Timer Entry");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                timers.add(new TimerItem(name));
                // Automatically select the new timer
                selectTimer(timers.size() - 1);
            } else {
                Toast.makeText(this, "Entry name cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete '" + timers.get(position).getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    timers.remove(position);
                    if (currentIndex == position) {
                        currentIndex = -1; // Reset selection if the current one is deleted
                    } else if (currentIndex > position) {
                        currentIndex--; // Adjust index if an item before it was deleted
                    }
                    updateUI();
                    Toast.makeText(this, "Entry deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    // --- RecyclerView Adapter --- //

    private class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TimerItem timer = timers.get(position);
            holder.name.setText(timer.getName());

            long elapsedMillis = timer.getElapsedMillis();
            int hours = (int) (elapsedMillis / (1000 * 60 * 60));
            int minutes = (int) ((elapsedMillis / (1000 * 60)) % 60);
            int seconds = (int) (elapsedMillis / 1000) % 60;
            holder.time.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));

            holder.itemView.setOnClickListener(v -> selectTimer(position));

            holder.deleteButton.setOnClickListener(v -> {
                showDeleteConfirmationDialog(position);
            });
        }

        @Override
        public int getItemCount() {
            return timers.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, time;
            ImageButton deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.item_entry_name);
                time = itemView.findViewById(R.id.item_entry_time);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }
    }
}
