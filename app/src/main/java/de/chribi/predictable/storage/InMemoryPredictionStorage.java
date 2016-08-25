package de.chribi.predictable.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

/**
 * An in-memory storage of predictions.
 */
public class InMemoryPredictionStorage implements PredictionStorage {
    private @NonNull HashMap<Integer, PredictedEvent> storage;
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
    public InMemoryPredictionStorage(List<PredictedEvent> initialPredictions) {
        this();
        for (PredictedEvent prediction : initialPredictions) {
            storage.put(prediction.getId(), prediction);
        }
    }

    @NonNull
    @Override
    public List<PredictedEvent> getPredictedEvents() {
        return new ArrayList<>(storage.values());
    }

    @Nullable
    @Override
    public PredictedEvent getPredictedEventById(int id) {
        return storage.get(id);
    }

    @NonNull
    @Override
    public PredictedEvent createPredictedEvent(@NonNull String title, @Nullable String description,
                                               @NonNull Date dueDate,
                                               @NonNull List<Prediction> predictions) {
        PredictedEvent newPredictedEvent = new PredictedEvent(nextId, title, description,
                PredictionState.Open, dueDate, predictions);
        storage.put(nextId, newPredictedEvent);
        nextId++;
        return newPredictedEvent;
    }

    @Override
    public void updatePredictedEvent(int id, PredictedEvent newPredictedEvent) {
        storage.put(id, newPredictedEvent);
    }

    @Override
    public void deletePredictedEvent(int id) {
        storage.remove(id);
    }

    @Override
    public void addPredictionToPredictedEvent(int id, Prediction prediction) {
        storage.get(id).getPredictions().add(prediction);
    }
}
