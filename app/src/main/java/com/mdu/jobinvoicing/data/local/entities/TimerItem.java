package com.mdu.jobinvoicing.data.local.entities;

import android.os.SystemClock;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "timer_items")
public class TimerItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private long timeBuff;    // accumulated millis while paused

    @Ignore
    private long startTime;   // SystemClock.uptimeMillis() when started
    @Ignore
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeBuff() {
        return timeBuff;
    }

    public void setTimeBuff(long timeBuff) {
        this.timeBuff = timeBuff;
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
