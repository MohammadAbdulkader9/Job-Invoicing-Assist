package com.mdu.jobinvoicing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.mdu.jobinvoicing.data.local.entities.TimerItem;

import java.util.ArrayList;
import java.util.Locale;

public class StopWatchTimer extends AppCompatActivity {
    TextView textView, entryNameTv;
    MaterialButton reset, start, stop, addEntry, switchEntry;
    Handler handler;

    ArrayList<TimerItem> timers = new ArrayList<>();
    int currentIndex = -1; // no selection at start

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (currentIndex >= 0 && currentIndex < timers.size()) {
                TimerItem current = timers.get(currentIndex);
                long updateTime = current.getElapsedMillis();

                int totalSeconds = (int) (updateTime / 1000);
                int hours = totalSeconds / 3600;
                int minutes = (totalSeconds % 3600) / 60;
                int seconds = totalSeconds % 60;

                textView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",
                        hours, minutes, seconds));
            } else {
                textView.setText("00:00:00");
            }
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        textView = findViewById(R.id.stopwatch_timer);
        entryNameTv = findViewById(R.id.entry_name); // add a TextView in your layout to show entry name
        reset = findViewById(R.id.stopwatch_reset);
        start = findViewById(R.id.stopwatch_start);
        stop = findViewById(R.id.stopwatch_pause);
        addEntry = findViewById(R.id.stopwatch_add);   // new button in layout
        switchEntry = findViewById(R.id.stopwatch_switch); // new button in layout

        handler = new Handler(Looper.getMainLooper());

        // Start timer runnable
        handler.post(runnable);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex < 0) return; // nothing selected

                TimerItem current = timers.get(currentIndex);
                if (!current.running) {
                    current.startTime = SystemClock.uptimeMillis();
                    current.running = true;
                    updateButtons();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex < 0) return;
                TimerItem current = timers.get(currentIndex);
                if (current.running) {
                    current.timeBuff += SystemClock.uptimeMillis() - current.startTime;
                    current.startTime = 0L;
                    current.running = false;
                    updateButtons();
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex < 0) return;
                TimerItem current = timers.get(currentIndex);
                current.timeBuff = 0L;
                current.startTime = 0L;
                current.running = false;
                textView.setText("00:00:00");
                updateButtons();
            }
        });

        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEntryDialog();
            }
        });

        switchEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSwitchEntryDialog();
            }
        });

        // initial UI state
        textView.setText("00:00:00");
        entryNameTv.setText("No entry selected");
        updateButtons();
    }

    private void updateButtons() {
        if (currentIndex < 0) {
            start.setEnabled(false);
            stop.setEnabled(false);
            reset.setEnabled(false);
            return;
        }
        TimerItem current = timers.get(currentIndex);
        start.setEnabled(!current.running);
        stop.setEnabled(current.running);
        reset.setEnabled(true);
        entryNameTv.setText(current.name + (current.running ? " (running)" : ""));
    }

    private void showAddEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add entry name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Create & Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name = input.getText().toString().trim();
                if (name.isEmpty()) name = "Timer " + (timers.size() + 1);

                TimerItem newTimer = new TimerItem(name);
                timers.add(newTimer);
                currentIndex = timers.size() - 1;
                updateButtons();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showSwitchEntryDialog() {
        if (timers.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("No entries")
                    .setMessage("Create an entry first.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] names = new String[timers.size()];
        for (int i = 0; i < timers.size(); i++) names[i] = timers.get(i).name + (timers.get(i).running ? " (running)" : "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose entry");
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                currentIndex = which;
                updateButtons();
            }
        });

        builder.setNeutralButton("Delete...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                showDeleteEntryDialog();
            }
        });

        builder.show();
    }

    private void showDeleteEntryDialog() {
        String[] names = new String[timers.size()];
        for (int i = 0; i < timers.size(); i++) names[i] = timers.get(i).name;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete entry");
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                timers.remove(which);
                if (timers.isEmpty()) {
                    currentIndex = -1;
                    textView.setText("00:00:00");
                    entryNameTv.setText("No entry selected");
                } else {
                    // choose a valid index
                    if (which <= timers.size() - 1) currentIndex = which;
                    else currentIndex = timers.size() - 1;
                }
                updateButtons();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
