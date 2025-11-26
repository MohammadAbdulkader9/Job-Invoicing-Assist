package com.mdu.jobinvoicing.data.local.entities;

import android.os.SystemClock;

public class TimerItem {
    private String name;
    private long timeBuff;    // accumulated millis while paused
    private long startTime;   // SystemClock.uptimeMillis() when started
    private boolean running;

    public TimerItem(String name) {
        this.name = name;
        this.timeBuff = 0L;
        this.startTime = 0L;
        this.running = false;
    }

    public void start() {
        if (!running) {
            startTime = SystemClock.uptimeMillis();
            running = true;
        }
    }

    public void pause() {
        if (running) {
            timeBuff += SystemClock.uptimeMillis() - startTime;
            running = false;
        }
    }

    public void reset() {
        timeBuff = 0L;
        startTime = 0L;
        running = false;
    }

    public String getName() {
        return name;
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
