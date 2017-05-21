package de.chribi.predictable.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.ConfidenceStatement;

/**
 * Interface for reading and writing {@link Prediction predictions}.
 */
public interface PredictionStorage {
    /**
     * Get a list of stored {@link Prediction predictions}.
     *
     * @return A list of {@link Prediction}s.
     */
    @NonNull List<Prediction> getPredictions();

    /**
     * Get a stored {@link Prediction} by id.
     *
     * @param id  The id of a Prediction.
     * @return The stored Prediction or null if no Prediction with the id was stored.
     */
    @Nullable
    Prediction getPredictionById(long id);

    /**
     * Create and store a new {@link Prediction}.
     *
     * @param title  The title of the prediction
     * @param description  A more detailed description of the prediction
     * @param dueDate  The time it should be possible to decide the prediction.
     * @param confidenceStatements  A list of confidences for the prediction
     * @return The created {@link Prediction}.
     */
    @NonNull
    Prediction createPrediction(@NonNull String title, @Nullable String description,
                                @NonNull Date dueDate,
                                @NonNull List<ConfidenceStatement> confidenceStatements);

    /**
     * Update a prediction.
     *
     * @param id The id of the prediction to update.  The behaviour is unspecified when
     *                no prediction with this id exists.
     * @param newPrediction The new values.  The id property of this prediction is ignored!
     *
     */
    void updatePrediction(long id, @NonNull Prediction newPrediction);

    /**
     * Delete a {@link Prediction}.
     * @param id  The id of the {@link Prediction} to delete.
     */
    void deletePrediction(long id);
}
