package com.mdu.jobinvoicing.vm;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mdu.jobinvoicing.data.local.entities.Operator;
import com.mdu.jobinvoicing.data.repositories.OperatorRepository;

import java.util.List;

/**
 * ViewModel for managing and providing a list of Operators to the UI.
 */
public class OperatorViewModel extends AndroidViewModel {

    private final OperatorRepository repository;
    private final LiveData<List<Operator>> allOperators;

    public OperatorViewModel(@NonNull Application application) {
        super(application);
        repository = new OperatorRepository(application);
        allOperators = repository.getAllOperators();
    }

    // --- Public API for the UI ---

    /**
     * Exposes the LiveData list of all operators to the UI.
     */
    public LiveData<List<Operator>> getAllOperators() {
        return allOperators;
    }

    /**
     * Gets a single operator by their ID.
     */
    public LiveData<Operator> getOperatorById(String id) {
        return repository.getOperatorById(id);
    }

    /**
     * Inserts a new operator.
     * The enqueueSync flag determines if this change should be synced with a remote server.
     */
    public void insert(Operator operator, boolean enqueueSync) {
        repository.insert(operator, enqueueSync);
    }

    /**
     * Updates an existing operator.
     * The enqueueSync flag determines if this change should be synced with a remote server.
     */
    public void update(Operator operator, boolean enqueueSync) {
        repository.update(operator, enqueueSync);
    }

    /**
     * Deletes an operator.
     * The enqueueSync flag determines if this change should be synced with a remote server.
     */
    public void delete(Operator operator, boolean enqueueSync) {
        repository.delete(operator, enqueueSync);
    }
}
