package de.chribi.predictable.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * Class for a event with predictions.
 */
public class PredictedEvent {
    private long id;
    private final @NonNull String title;
    private final @Nullable String description;
    private final PredictionState state;
    private final @NonNull Date dueDate;
    private final @NonNull List<Prediction> predictions;

    /**
     * Create a {@link PredictedEvent}.
     * @param id A unique id.
     * @param title The title of the predicted event.
     * @param description A more detailed description.
     * @param state The state of the predicted event.
     * @param dueDate The time it should be possible to decide if the predicted event
     *                happened or not.
     * @param predictions List of predictions for the predicted event.
     */
    public PredictedEvent(long id, @NonNull String title, String description, PredictionState state,
                          @NonNull Date dueDate, @NonNull List<Prediction> predictions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.state = state;
        this.dueDate = dueDate;
        this.predictions = predictions;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @NonNull
    public Date getDueDate() {
        return dueDate;
    }

    public PredictionState getState() {
        return state;
    }

    @NonNull
    public List<Prediction> getPredictions() {
        return predictions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PredictedEvent that = (PredictedEvent) o;

        if (id != that.id) return false;
        if (!title.equals(that.title)) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (state != that.state) return false;
        if (!dueDate.equals(that.dueDate)) return false;
        return predictions.equals(that.predictions);

    }

    @Override
    public int hashCode() {
        int result = (int)id;
        result = 31 * result + title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + dueDate.hashCode();
        result = 31 * result + predictions.hashCode();
        return result;
    }
}
