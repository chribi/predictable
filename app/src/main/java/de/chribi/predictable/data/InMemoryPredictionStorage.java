package de.chribi.predictable.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * An in-memory storage of predictions.
 */
public class InMemoryPredictionStorage implements PredictionStorage {
    private @NonNull HashMap<Integer, PredictionSet> storage;
    private int nextId;

    /**
     * Create a new empty InMemoryPredictionStorage.
     */
    public InMemoryPredictionStorage() {
        storage = new HashMap<>();
        nextId = 0;
    }

    /**
     * Create a new InMemoryPredictionStorage containing some predictions.
     *
     * @param initialPredictions  Some initial predictions.
     */
    public InMemoryPredictionStorage(List<PredictionSet> initialPredictions) {
        this();
        for (PredictionSet prediction : initialPredictions) {
            storage.put(prediction.getId(), prediction);
        }
    }

    @NonNull
    @Override
    public List<PredictionSet> getPredictions() {
        return new ArrayList<>(storage.values());
    }

    @Nullable
    @Override
    public PredictionSet getPredictionById(int id) {
        return storage.get(id);
    }

    @NonNull
    @Override
    public PredictionSet createPredictionSet(@NonNull String title, @Nullable String description,
                                             @NonNull Calendar dueDate,
                                             @NonNull List<Prediction> predictions) {
        PredictionSet newPredictionSet = new PredictionSet(nextId, title, description,
                PredictionState.Open, dueDate, predictions);
        storage.put(nextId, newPredictionSet);
        nextId++;
        return newPredictionSet;
    }

    @Override
    public void updatePredictionSet(int id, PredictionSet newPredictionSet) {
        storage.put(id, newPredictionSet);
    }

    @Override
    public void deletePredictionSet(int id) {
        storage.remove(id);
    }

    @Override
    public void addPredictionToPredictionSet(int id, Prediction prediction) {
        storage.get(id).getPredictions().add(prediction);
    }
}
