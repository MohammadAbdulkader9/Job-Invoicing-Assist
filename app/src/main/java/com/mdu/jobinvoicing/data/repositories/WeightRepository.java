package com.mdu.jobinvoicing.data.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mdu.jobinvoicing.data.local.AppDatabase;
import com.mdu.jobinvoicing.data.local.daos.WeightDao;
import com.mdu.jobinvoicing.data.local.entities.Weight;

import java.util.List;

public class WeightRepository {

    private final WeightDao weightDao;

    public WeightRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        weightDao = db.weightDao();
    }

    public LiveData<List<Weight>> getWeightsForWorkPlan(String workPlanId) {
        return weightDao.getWeightsForWorkPlan(workPlanId);
    }

    public void insert(Weight weight) {
        AppDatabase.databaseWriteExecutor.execute(() -> weightDao.insert(weight));
    }

    public void update(Weight weight) {
        AppDatabase.databaseWriteExecutor.execute(() -> weightDao.update(weight));
    }

    public void delete(Weight weight) {
        AppDatabase.databaseWriteExecutor.execute(() -> weightDao.delete(weight));
    }
}
