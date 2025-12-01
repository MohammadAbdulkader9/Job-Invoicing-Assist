package com.mdu.jobinvoicing.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mdu.jobinvoicing.data.local.daos.*;
import com.mdu.jobinvoicing.data.local.entities.*;

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
        TimerItem.class
}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract WorkPlanDao workPlanDao();
    public abstract TimeEntryDao timeEntryDao();
    public abstract MaterialDao materialDao();
    public abstract ServiceItemDao serviceItemDao();
    public abstract InvoiceDao invoiceDao();
    public abstract InvoiceLineItemDao invoiceLineItemDao();
    public abstract SyncQueueDao syncQueueDao();
    public abstract OperatorDao operatorDao();
    public abstract TimerItemDao timerItemDao();

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
