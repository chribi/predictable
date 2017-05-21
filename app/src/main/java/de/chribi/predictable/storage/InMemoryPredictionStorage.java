package de.chribi.predictable.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Prediction;

/**
 * An in-memory storage of predictions.
 */
public class InMemoryPredictionStorage implements PredictionStorage {
    private @NonNull HashMap<Long, Prediction> storage;
    private long nextId;

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
     * @param initialPredictions Some initial predictions.
     */
    public InMemoryPredictionStorage(List<Prediction> initialPredictions) {
        this();
        for (Prediction prediction : initialPredictions) {
            storage.put(prediction.getId(), prediction);
        }
    }

    @NonNull
    @Override
    public List<Prediction> getPredictions() {
        return new ArrayList<>(storage.values());
    }

    @Nullable
    @Override
    public Prediction getPredictionById(long id) {
        return storage.get(id);
    }

    @NonNull
    @Override
    public Prediction createPrediction(@NonNull String title, @Nullable String description,
                                       @NonNull Date dueDate,
                                       @NonNull List<ConfidenceStatement> confidenceStatements) {
        Prediction newPrediction = Prediction.builder()
                .setId(nextId)
                .setTitle(title)
                .setDescription(description)
                .setDueDate(dueDate)
                .setConfidences(confidenceStatements)
                .build();
        storage.put(nextId, newPrediction);
        nextId++;
        return newPrediction;
    }

    @Override
    public void updatePrediction(long id, @NonNull Prediction prediction) {
        if (storage.get(id) != null) {
            storage.put(id, prediction);
        }
    }

    @Override
    public void deletePrediction(long id) {
        storage.remove(id);
    }
}
