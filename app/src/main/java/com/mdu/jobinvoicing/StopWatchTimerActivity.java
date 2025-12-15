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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.StopWatchTimerItemDao;
import com.mdu.jobinvoicing.data.local.entities.StopWatchTimerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class StopWatchTimerActivity extends AppCompatActivity {

    private TextView stopwatchTimer, entryName;
    private MaterialButton startPauseButton, resetButton;
    private FloatingActionButton addEntryFab;
    private RecyclerView timersRecyclerView;

    private Handler handler;
    private List<StopWatchTimerItem> timers = new ArrayList<>();
    private TimerAdapter timerAdapter;
    private int currentIndex = -1;

    private ExecutorService executor;
    private StopWatchTimerItemDao stopWatchTimerItemDao;
    private final AtomicLong newTimerId = new AtomicLong(-1);

    private NetworkHelper networkHelper;
    private TextView statusText;
    private ImageView statusDot;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Update the main timer display (authoritative elapsed comes from entity)
            if (currentIndex != -1 && currentIndex < timers.size() && timers.get(currentIndex).isRunning()) {
                updateTimerText(timers.get(currentIndex));
            }
            // Update visible list items
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

        executor = Executors.newSingleThreadExecutor();
        AppDatabase db = AppDatabase.getInstance(this);
        stopWatchTimerItemDao = db.timerItemDao();

        // Initialize top-half views
        stopwatchTimer = findViewById(R.id.stopwatch_timer);
        entryName = findViewById(R.id.entry_name);
        startPauseButton = findViewById(R.id.stopwatch_start_pause);
        resetButton = findViewById(R.id.stopwatch_reset);

        // Set Top Bar Title
        MaterialToolbar toolbar = findViewById(R.id.top_bar);
        if (toolbar != null) {
            toolbar.setTitle("Timers");
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Apply padding for system bars (status bar / navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.stopwatch), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize bottom-half views
        timersRecyclerView = findViewById(R.id.timers_recycler_view);
        addEntryFab = findViewById(R.id.add_entry_fab);

        // offline-online functionality
        statusText = findViewById(R.id.SyncText);
        statusDot = findViewById(R.id.statusDot);

        networkHelper = new NetworkHelper(this, statusDot, statusText);
        networkHelper.startMonitoring();


        // Set up RecyclerView
        timersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timerAdapter = new TimerAdapter();
        timersRecyclerView.setAdapter(timerAdapter);

        // Observe DB - whenever the list changes, update local list and UI
        stopWatchTimerItemDao.getAllTimerItems().observe(this, timerItems -> {
            long newId = newTimerId.getAndSet(-1);
            int previousSelectedId = -1;
            if (currentIndex != -1 && currentIndex < timers.size()) {
                previousSelectedId = timers.get(currentIndex).getId();
            }

            this.timers = timerItems != null ? timerItems : new ArrayList<>();
            timerAdapter.notifyDataSetChanged();

            if (newId != -1) {
                // select newly created item
                for (int i = 0; i < this.timers.size(); i++) {
                    if (this.timers.get(i).getId() == newId) {
                        selectTimer(i);
                        break;
                    }
                }
            } else if (previousSelectedId != -1) {
                // try to restore previous selection
                currentIndex = -1;
                for (int i = 0; i < this.timers.size(); i++) {
                    if (this.timers.get(i).getId() == previousSelectedId) {
                        currentIndex = i;
                        break;
                    }
                }
                updateUI();
            } else {
                // NO new item & NO previous selection -> try to auto-select a running timer
                boolean selected = false;
                for (int i = 0; i < this.timers.size(); i++) {
                    if (this.timers.get(i).isRunning()) {
                        selectTimer(i);
                        selected = true;
                        break;
                    }
                }
                if (!selected) {
                    // nothing running — just update UI (no selection)
                    updateUI();
                }
            }
        });

        // Handler for UI refresh — created here, started/stopped in onResume/onPause
        handler = new Handler(Looper.getMainLooper());

        // Set up button listeners
        addEntryFab.setOnClickListener(v -> showAddEntryDialog());

        startPauseButton.setOnClickListener(v -> {
            if (currentIndex == -1) {
                Toast.makeText(this, "Please select an entry first", Toast.LENGTH_SHORT).show();
                return;
            }
            StopWatchTimerItem t = timers.get(currentIndex);
            if (t.isRunning()) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        resetButton.setOnClickListener(v -> {
            if (currentIndex != -1) {
                // Pause then reset
                StopWatchTimerItem currentTimer = timers.get(currentIndex);
                currentTimer.resetNow();
                executor.execute(() -> stopWatchTimerItemDao.update(currentTimer));
                stopwatchTimer.setText("00:00:00");
                updateUI();
            }
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkHelper != null) {
            networkHelper.stopMonitoring();
        }
        handler.removeCallbacks(runnable);
        executor.shutdown();
    }

    private void startTimer() {
        if (currentIndex != -1) {
            StopWatchTimerItem t = timers.get(currentIndex);
            if (!t.isRunning()) {
                t.startNow();
                executor.execute(() -> stopWatchTimerItemDao.update(t));
                updateUI();
            }
        }
    }

    private void pauseTimer() {
        if (currentIndex != -1) {
            StopWatchTimerItem t = timers.get(currentIndex);
            if (t.isRunning()) {
                t.pauseNow();
                executor.execute(() -> stopWatchTimerItemDao.update(t));
                updateUI();
            }
        }
    }

    private void selectTimer(int index) {
        // preserve behavior: pause running timer when selecting a different one
        if (currentIndex != -1 && currentIndex != index) {
            StopWatchTimerItem prev = timers.get(currentIndex);
            if (prev.isRunning()) {
                // pause previous and persist
                prev.pauseNow();
                executor.execute(() -> stopWatchTimerItemDao.update(prev));
            }
        }
        currentIndex = index;
        updateUI();
        if (currentIndex != -1 && currentIndex < timers.size()) {
            updateTimerText(timers.get(currentIndex));
        }
    }

    private void updateTimerText(StopWatchTimerItem stopWatchTimerItem) {
        long elapsedMillis = stopWatchTimerItem.getElapsedMillis();
        int hours = (int) (elapsedMillis / (1000L * 60L * 60L));
        int minutes = (int) ((elapsedMillis / (1000L * 60L)) % 60L);
        int seconds = (int) ((elapsedMillis / 1000L) % 60L);
        stopwatchTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void updateUI() {
        if (currentIndex == -1 || currentIndex >= timers.size()) {
            entryName.setText("No entry selected");
            stopwatchTimer.setText("00:00:00");
            startPauseButton.setEnabled(false);
            resetButton.setEnabled(false);
        } else {
            StopWatchTimerItem current = timers.get(currentIndex);
            entryName.setText(current.getName());
            startPauseButton.setEnabled(true);
            resetButton.setEnabled(true);

            // update main timer text immediately
            updateTimerText(current);
        }

        // Update play/pause button icon based on current selected timer's running state
        boolean running = (currentIndex != -1 && currentIndex < timers.size() && timers.get(currentIndex).isRunning());
        if (running) {
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
                executor.execute(() -> {
                    long insertedId = stopWatchTimerItemDao.insert(new StopWatchTimerItem(name));
                    newTimerId.set(insertedId);
                });
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
                    executor.execute(() -> stopWatchTimerItemDao.delete(timers.get(position)));
                    Toast.makeText(this, "Entry deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
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
            StopWatchTimerItem timer = timers.get(position);
            holder.name.setText(timer.getName());

            long elapsedMillis = timer.getElapsedMillis();
            int hours = (int) (elapsedMillis / (1000L * 60L * 60L));
            int minutes = (int) ((elapsedMillis / (1000L * 60L)) % 60L);
            int seconds = (int) ((elapsedMillis / 1000L) % 60L);
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
