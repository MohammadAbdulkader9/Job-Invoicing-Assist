package com.mdu.jobinvoicing.data.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.DistanceDao;
import com.mdu.jobinvoicing.data.local.entities.Distance;

import java.util.List;

public class DistanceRepository {

    private final DistanceDao distanceDao;

    public DistanceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        distanceDao = db.distanceDao();
    }

    public LiveData<List<Distance>> getDistancesForWorkPlan(String workPlanId) {
        return distanceDao.getDistancesForWorkPlan(workPlanId);
    }

    public void insert(Distance distance) {
        AppDatabase.databaseWriteExecutor.execute(() -> distanceDao.insert(distance));
    }

    public void update(Distance distance) {
        AppDatabase.databaseWriteExecutor.execute(() -> distanceDao.update(distance));
    }

    public void delete(Distance distance) {
        AppDatabase.databaseWriteExecutor.execute(() -> distanceDao.delete(distance));
    }
}
