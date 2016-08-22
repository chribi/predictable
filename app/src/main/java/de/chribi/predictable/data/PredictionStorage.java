package de.chribi.predictable.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

/**
 * Interface for reading and writing {@link PredictionSet predictions}.
 */
public interface PredictionStorage {
    /**
     * Get a list of stored {@link PredictionSet predictions}.
     *
     * @return A list of {@link PredictionSet}s.
     */
    @NonNull List<PredictionSet> getPredictions();

    /**
     * Get a stored {@link PredictionSet} by id.
     *
     * @param id  The id of a PredictionSet.
     * @return The stored PredictionSet or null if no PredictionSet with the id was stored.
     */
    @Nullable PredictionSet getPredictionById(int id);

    /**
     * Create and store a new {@link PredictionSet}.
     *
     * @param title  The title of the predicted event.
     * @param description  A more detailed description of the predicted event.
     * @param dueDate  The time it should be possible to decide the prediction.
     * @param predictions  A list of predictions for the predicted event.
     * @return The created {@link PredictionSet}.
     */
    @NonNull PredictionSet createPredictionSet(@NonNull String title, @Nullable String description,
                                               @NonNull Calendar dueDate,
                                               @NonNull List<Prediction> predictions);

    /**
     * Update a {@link PredictionSet} with a certain id.
     *
     * @param id  The id of the {@link PredictionSet} to update.
     * @param newPredictionSet  The new {@link PredictionSet}.
     */
    void updatePredictionSet(int id, PredictionSet newPredictionSet);

    /**
     * Delete a {@link PredictionSet}.
     * @param id  The id of the {@link PredictionSet} to delete.
     */
    void deletePredictionSet(int id);

    /**
     * Add a {@link Prediction} to a {@link PredictionSet}.
     * @param id  The id of the {@link PredictionSet} to update.
     * @param prediction  The {@link Prediction} to add.
     */
    void addPredictionToPredictionSet(int id, Prediction prediction);
}
