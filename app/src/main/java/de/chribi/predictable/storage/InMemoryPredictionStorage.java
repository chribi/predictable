package de.chribi.predictable.storage;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.storage.queries.PredictionQuery;

/**
 * An in-memory storage of predictions.
 */
@SuppressLint("UseSparseArrays")
public class InMemoryPredictionStorage implements PredictionStorage {
    @NonNull private final HashMap<Long, Prediction> storage;
    private long nextId;
    private final PredicateConstraintInterpreter queryInterpreter;

    /**
     * Create a new empty InMemoryPredictionStorage.
     */
    public InMemoryPredictionStorage() {
        storage = new HashMap<>();
        nextId = 0;
        queryInterpreter = new PredicateConstraintInterpreter();
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

    @NonNull
    @Override
    public List<Prediction> getPredictions(PredictionQuery query) {
        List<Prediction> result = new ArrayList<>();
        Predicate<Prediction> filter = query.getWhereClause().accept(queryInterpreter);
        for(Prediction prediction : storage.values()) {
            if(filter.apply(prediction)) {
                result.add(prediction);
            }
        }
        Collections.sort(result, PredictionComparator.fromOrderings(query.getOrderByClauses()));
        return result;
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
            storage.put(id, prediction.toBuilder().setId(id).build());
        }
    }

    @Override
    public void deletePrediction(long id) {
        storage.remove(id);
    }
}
