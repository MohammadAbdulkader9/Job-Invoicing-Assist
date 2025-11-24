package com.mdu.jobinvoicing;



import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import java.text.MessageFormat;
import java.util.Locale;

public class StopWatchTimer extends AppCompatActivity {
TextView textView;
MaterialButton reset, start, stop;
int seconds, minutes, hours, totalSeconds;
long startTime, timeBuff = 0L;
Handler handler;
private final Runnable runnable = new Runnable() {
    @Override
    public void run() {
        long updateTime = (int) timeBuff + (SystemClock.uptimeMillis() - startTime);

        totalSeconds = (int) (updateTime / 1000);
        hours = totalSeconds / 3600;
        minutes = (totalSeconds % 3600) / 60;
        seconds = totalSeconds % 60;


        textView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
        handler.postDelayed(this, 500);
    }
};
@Override
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stopwatch);

    textView = findViewById(R.id.stopwatch_timer);
    reset = findViewById(R.id.stopwatch_reset);
    start = findViewById(R.id.stopwatch_start);
    stop = findViewById(R.id.stopwatch_pause);

    handler = new Handler(Looper.getMainLooper());

    start.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startTime = SystemClock.uptimeMillis();
            handler.post(runnable);
            start.setEnabled((false));
            reset.setEnabled(true);
            stop.setEnabled(true);
        }
    });

    stop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            timeBuff += SystemClock.uptimeMillis() - startTime;
            handler.removeCallbacks(runnable);
            start.setEnabled(true);
            reset.setEnabled(true);
            stop.setEnabled(false);
        }
    });

    reset.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            handler.removeCallbacks(runnable);
            startTime = 0L;
            timeBuff = 0L;
            textView.setText("00:00:00");
            start.setEnabled(true);
            reset.setEnabled(true);
            stop.setEnabled(false);
        }
    });
    textView.setText("00:00:00");
    start.setEnabled(true);
    reset.setEnabled(true);
    stop.setEnabled(false);
    }

}
