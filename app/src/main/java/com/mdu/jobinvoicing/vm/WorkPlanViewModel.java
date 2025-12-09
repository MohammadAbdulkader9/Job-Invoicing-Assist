package com.mdu.jobinvoicing.vm;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mdu.jobinvoicing.data.local.entities.WorkPlan;
import com.mdu.jobinvoicing.data.local.entities.WorkPlanStatus;
import com.mdu.jobinvoicing.data.repositories.WorkPlanRepository;

import java.util.List;
import java.util.UUID;

public class WorkPlanViewModel extends AndroidViewModel {

    private final WorkPlanRepository repository;
    private final LiveData<List<WorkPlan>> allWorkPlans;

    public WorkPlanViewModel(@NonNull Application application) {
        super(application);
        repository = new WorkPlanRepository(application);
        allWorkPlans = repository.getAllWorkPlans();
    }

    // --- Public API for the UI ---

    /**
     * Exposes the LiveData list of all work plans to the UI.
     */
    public LiveData<List<WorkPlan>> getAllWorkPlans() {
        return allWorkPlans;
    }

    /**
     * Gets a single work plan by its ID.
     */
    public LiveData<WorkPlan> getWorkPlanById(String id) {
        return repository.getWorkPlanById(id);
    }

    /**
     * Inserts a new work plan.
     * The enqueueSync flag determines if this change should be synced with a remote server.
     */
    public void insert(WorkPlan workPlan, boolean enqueueSync) {
        repository.insert(workPlan, enqueueSync);
    }

    /**
     * Updates an existing work plan.
     * The enqueueSync flag determines if this change should be synced with a remote server.
     */
    public void update(WorkPlan workPlan, boolean enqueueSync) {
        repository.update(workPlan, enqueueSync);
    }

    /**
     * Deletes a work plan.
     * The enqueueSync flag determines if this change should be synced with a remote server.
     */
    public void delete(WorkPlan workPlan, boolean enqueueSync) {
        repository.delete(workPlan, enqueueSync);
    }

    /**
     * Creates a new, empty work plan object with a unique ID.
     * This can be used by the UI to start the creation process.
     */
    public WorkPlan createNewWorkPlan() {
        String id = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        // Default values for a new plan
        return new WorkPlan(id, "", "", "", now, now, WorkPlanStatus.OPEN);
    }
}
