package de.chribi.predictable.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;

/**
 * Interface for reading and writing {@link PredictedEvent predictions}.
 */
public interface PredictionStorage {
    /**
     * Get a list of stored {@link PredictedEvent predictions}.
     *
     * @return A list of {@link PredictedEvent}s.
     */
    @NonNull List<PredictedEvent> getPredictedEvents();

    /**
     * Get a stored {@link PredictedEvent} by id.
     *
     * @param id  The id of a PredictedEvent.
     * @return The stored PredictedEvent or null if no PredictedEvent with the id was stored.
     */
    @Nullable
    PredictedEvent getPredictedEventById(long id);

    /**
     * Create and store a new {@link PredictedEvent}.
     *
     * @param title  The title of the predicted event.
     * @param description  A more detailed description of the predicted event.
     * @param dueDate  The time it should be possible to decide the prediction.
     * @param predictions  A list of predictions for the predicted event.
     * @return The created {@link PredictedEvent}.
     */
    @NonNull
    PredictedEvent createPredictedEvent(@NonNull String title, @Nullable String description,
                                        @NonNull Date dueDate,
                                        @NonNull List<Prediction> predictions);

    /**
     * Update a predicted event.
     *
     * @param id The id of the event to update.  The behaviour is unspecified when
     *                no event with this id exists.
     * @param event The new values.  The id property of this event is ignored!
     *
     */
    void updatePredictedEvent(long id, @NonNull PredictedEvent event);

    /**
     * Delete a {@link PredictedEvent}.
     * @param id  The id of the {@link PredictedEvent} to delete.
     */
    void deletePredictedEvent(long id);
}
