package com.mdu.jobinvoicing.data.local.entities;

import android.os.SystemClock;

public class TimerItem {
    public String name;
    public long timeBuff;    // accumulated millis while paused
    public long startTime;   // SystemClock.uptimeMillis() when started
    public boolean running;

    public TimerItem(String name) {
        this.name = name;
        this.timeBuff = 0L;
        this.startTime = 0L;
        this.running = false;
    }

    // convenience to get total elapsed millis depending on running state
    public long getElapsedMillis() {
        if (running) {
            return timeBuff + (SystemClock.uptimeMillis() - startTime);
        } else {
            return timeBuff;
        }
    }
}
