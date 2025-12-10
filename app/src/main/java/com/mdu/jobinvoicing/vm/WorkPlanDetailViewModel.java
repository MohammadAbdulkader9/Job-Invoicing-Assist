package com.mdu.jobinvoicing.vm;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mdu.jobinvoicing.data.local.entities.Distance;
import com.mdu.jobinvoicing.data.local.entities.Material;
import com.mdu.jobinvoicing.data.local.entities.ServiceItem;
import com.mdu.jobinvoicing.data.local.entities.TimeEntry;
import com.mdu.jobinvoicing.data.local.entities.Weight;
import com.mdu.jobinvoicing.data.local.entities.WorkPlan;
import com.mdu.jobinvoicing.data.repositories.DistanceRepository;
import com.mdu.jobinvoicing.data.repositories.MaterialRepository;
import com.mdu.jobinvoicing.data.repositories.ServiceItemRepository;
import com.mdu.jobinvoicing.data.repositories.TimeEntryRepository;
import com.mdu.jobinvoicing.data.repositories.WeightRepository;
import com.mdu.jobinvoicing.data.repositories.WorkPlanRepository;

import java.util.List;

/**
 * A composite ViewModel for the Work Plan Detail screen.
 * It provides the data for a single WorkPlan and all of its related children
 * (TimeEntries, Materials, and ServiceItems).
 */
public class WorkPlanDetailViewModel extends AndroidViewModel {

    // Repositories for all the data this screen needs
    private final WorkPlanRepository workPlanRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final MaterialRepository materialRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final DistanceRepository distanceRepository;
    private final WeightRepository weightRepository;

    // The ID of the currently displayed WorkPlan. This acts as the trigger for loading data.
    private final MutableLiveData<String> workPlanId = new MutableLiveData<>();

    // LiveData for the WorkPlan itself and its children, all derived from the workPlanId.
    public final LiveData<WorkPlan> workPlan;
    public final LiveData<List<TimeEntry>> timeEntries;
    public final LiveData<List<Material>> materials;
    public final LiveData<List<ServiceItem>> serviceItems;
    public final LiveData<List<Distance>> distances;
    public final LiveData<List<Weight>> weights;


    public WorkPlanDetailViewModel(@NonNull Application application) {
        super(application);
        // Initialize all necessary repositories
        workPlanRepository = new WorkPlanRepository(application);
        timeEntryRepository = new TimeEntryRepository(application);
        materialRepository = new MaterialRepository(application);
        serviceItemRepository = new ServiceItemRepository(application);
        distanceRepository = new DistanceRepository(application);
        weightRepository = new WeightRepository(application);

        // Use Transformations.switchMap to react to changes in workPlanId.
        // When workPlanId changes, these switchMap functions will call the repository
        // with the new ID and update the LiveData accordingly.
        workPlan = Transformations.switchMap(workPlanId, id ->
                workPlanRepository.getWorkPlanById(id));

        timeEntries = Transformations.switchMap(workPlanId, id ->
                timeEntryRepository.getForWorkPlan(id));

        materials = Transformations.switchMap(workPlanId, id ->
                materialRepository.getForWorkPlan(id));

        serviceItems = Transformations.switchMap(workPlanId, id ->
                serviceItemRepository.getForWorkPlan(id));

        distances = Transformations.switchMap(workPlanId, id ->
                distanceRepository.getDistancesForWorkPlan(id));

        weights = Transformations.switchMap(workPlanId, id ->
                weightRepository.getWeightsForWorkPlan(id));
    }

    // --- Public API for the UI ---

    /**
     * The UI calls this method to tell the ViewModel which WorkPlan to display.
     * This triggers all the LiveData objects to update.
     * @param id The UUID of the work plan.
     */
    public void setWorkPlanId(String id) {
        workPlanId.setValue(id);
    }

    // --- Methods to modify child entities ---

    public void insertTimeEntry(TimeEntry timeEntry, boolean enqueueSync) {
        timeEntryRepository.insert(timeEntry, enqueueSync);
    }

    public void deleteTimeEntry(TimeEntry timeEntry, boolean enqueueSync) {
        timeEntryRepository.delete(timeEntry, enqueueSync);
    }

    public void insertMaterial(Material material, boolean enqueueSync) {
        materialRepository.insert(material, enqueueSync);
    }

    public void deleteMaterial(Material material, boolean enqueueSync) {
        materialRepository.delete(material, enqueueSync);
    }

    public void insertServiceItem(ServiceItem serviceItem, boolean enqueueSync) {
        serviceItemRepository.insert(serviceItem, enqueueSync);
    }

    public void deleteServiceItem(ServiceItem serviceItem, boolean enqueueSync) {
        serviceItemRepository.delete(serviceItem, enqueueSync);
    }

    public void insertDistance(Distance distance) {
        distanceRepository.insert(distance);
    }

    public void deleteDistance(Distance distance) {
        distanceRepository.delete(distance);
    }

    public void insertWeight(Weight weight) {
        weightRepository.insert(weight);
    }

    public void deleteWeight(Weight weight) {
        weightRepository.delete(weight);
    }
}
