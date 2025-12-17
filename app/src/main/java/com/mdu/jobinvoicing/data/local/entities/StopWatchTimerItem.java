package com.mdu.jobinvoicing.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stop_watch_timer_items")
public class StopWatchTimerItem {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    // persisted single-source-of-truth fields:
    // total millis accumulated while paused/stopped
    private long accumulatedMillis;

    // System.currentTimeMillis() when the timer is running, 0 if not running
    private long startTimestamp;

    public StopWatchTimerItem(String name) {
        this.name = name;
        this.accumulatedMillis = 0L;
        this.startTimestamp = 0L;
    }

    // Room may require an empty constructor depending on your setup
    public StopWatchTimerItem() { }

    // --- getters & setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getAccumulatedMillis() { return accumulatedMillis; }
    public void setAccumulatedMillis(long accumulatedMillis) { this.accumulatedMillis = accumulatedMillis; }

    public long getStartTimestamp() { return startTimestamp; }
    public void setStartTimestamp(long startTimestamp) { this.startTimestamp = startTimestamp; }

    // --- operations (mutate in-memory; persist via DAO.update(...) afterwards) ---
    /**
     * Start the timer now. Caller must persist the entity after calling this (dao.update(...)).
     */
    public void startNow() {
        if (!isRunning()) {
            startTimestamp = System.currentTimeMillis();
        }
    }

    /**
     * Pause the timer now and accumulate elapsed into accumulatedMillis.
     * Caller must persist the entity after calling this (dao.update(...)).
     */
    public void pauseNow() {
        if (isRunning()) {
            long now = System.currentTimeMillis();
            accumulatedMillis += (now - startTimestamp);
            startTimestamp = 0L;
        }
    }

    /**
     * Reset timer to zero (stopped). Caller must persist the entity after calling this.
     */
    public void resetNow() {
        accumulatedMillis = 0L;
        startTimestamp = 0L;
    }

    public boolean isRunning() {
        return startTimestamp != 0L;
    }

    /**
     * Compute elapsed millis at the moment of call.
     * Safe to call at any time (Activity backgrounded, process resumed, etc.)
     */
    public long getElapsedMillis() {
        if (isRunning()) {
            return accumulatedMillis + (System.currentTimeMillis() - startTimestamp);
        } else {
            return accumulatedMillis;
        }
    }
}
