package com.mdu.jobinvoicing.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mdu.jobinvoicing.data.local.daos.*;
import com.mdu.jobinvoicing.data.local.entities.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AppDatabase defines the Room database and provides DAOs.
 */
@Database(entities = {
        WorkPlan.class,
        TimeEntry.class,
        Material.class,
        ServiceItem.class,
        Invoice.class,
        InvoiceLineItem.class,
        SyncQueue.class,
        Operator.class,
        StopWatchTimerItem.class,
        Distance.class,
        Weight.class
}, version = 11, exportSchema = false)
@TypeConverters({WorkPlanStatusConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract WorkPlanDao workPlanDao();
    public abstract TimeEntryDao timeEntryDao();
    public abstract MaterialDao materialDao();
    public abstract ServiceItemDao serviceItemDao();
    public abstract InvoiceDao invoiceDao();
    public abstract InvoiceLineItemDao invoiceLineItemDao();
    public abstract SyncQueueDao syncQueueDao();
    public abstract OperatorDao operatorDao();
    public abstract StopWatchTimerItemDao timerItemDao();
    public abstract DistanceDao distanceDao();
    public abstract WeightDao weightDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "job_invoicing_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
