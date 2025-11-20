package com.mdu.jobinvoicing;

import static java.text.MessageFormat.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.text.MessageFormat;
import java.util.Locale;

public class StopWatchTimer extends AppCompatActivity {
TextView textView;
MaterialButton reset, start, stop;
int seconds, minutes, milliSeconds;
long millisecond, startTime, timeBuff, updateTime = 0L;
Handler handler;
private final Runnable runnable = new Runnable() {
    @Override
    public void run() {
        millisecond = SystemClock.uptimeMillis() - startTime;
        updateTime = timeBuff + millisecond;
        minutes = seconds / 60;
        seconds = seconds % 60;
        milliSeconds = (int) (updateTime % 1000);

        textView.setText(MessageFormat.format(pattern: {0}:{1}:{2}, String.format(Locale.getDefault(), )));

    }
}
@Override
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    }

}
