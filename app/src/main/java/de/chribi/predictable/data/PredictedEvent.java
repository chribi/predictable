package de.chribi.predictable.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Class for a event with predictions.
 */
public class PredictedEvent {
    /**
     * Interface for modifying a stored {@link PredictedEvent}.
     */
    public interface Editor {
        /**
         * Commit the changes to the underlying storage.
         * @return The modified predicted event.
         */
        PredictedEvent commit();

        void setTile(@NonNull String newTitle);
        @NonNull String getTitle();

        void setDescription(@Nullable String newDescription);
        @Nullable String getDescription();

        void setJudgement(@Nullable Judgement newJudgment);
        @Nullable Judgement getJudgement();

        void setDueDate(@NonNull Date newDueDate);
        @NonNull Date getDueDate();

        void addPrediction(@NonNull Prediction newPrediction);
        void removePrediction(Prediction prediction);
        @NonNull List<Prediction> getPredictions();
    }

    private long id;
    private final @NonNull String title;
    private final @Nullable String description;
    private final @Nullable Judgement judgement;
    private final @NonNull Date dueDate;
    private final @NonNull List<Prediction> predictions;

    /**
     * Create a {@link PredictedEvent}.
     * @param id A unique id.
     * @param title The title of the predicted event.
     * @param description A more detailed description.
     * @param judgement How/when the prediction was judged or null if it is open.
     * @param dueDate The time it should be possible to decide if the predicted event
     *                happened or not.
     * @param predictions List of predictions for the predicted event.
     */
    public PredictedEvent(long id, @NonNull String title, String description,
                          @Nullable Judgement judgement, @NonNull Date dueDate,
                          @NonNull List<Prediction> predictions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.judgement = judgement;
        this.dueDate = dueDate;
        this.predictions = predictions;
        Collections.sort(predictions, new Comparator<Prediction>() {
            @Override
            public int compare(Prediction prediction, Prediction t1) {
                return prediction.getCreationDate().compareTo(t1.getCreationDate());
            }
        });
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

    @Nullable
    public Judgement getJudgement() {
        return judgement;
    }

    /**
     * List of predictions for this event.
     * @return The list of predictions for this event, sorted ascending by creation date.
     */
    @NonNull
    public List<Prediction> getPredictions() {
        return predictions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PredictedEvent event = (PredictedEvent) o;

        if (id != event.id)
            return false;
        if (!title.equals(event.title))
            return false;
        if (description != null ? !description.equals(event.description) : event.description != null)
            return false;
        if (judgement != null ? !judgement.equals(event.judgement) : event.judgement != null)
            return false;
        if (!dueDate.equals(event.dueDate))
            return false;
        return predictions.equals(event.predictions);

    }

    @Override
    public int hashCode() {
        int result = (int)id;
        result = 31 * result + title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (judgement != null ? judgement.hashCode() : 0);
        result = 31 * result + dueDate.hashCode();
        result = 31 * result + predictions.hashCode();
        return result;
    }
}
