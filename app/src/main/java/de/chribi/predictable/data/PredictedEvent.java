package de.chribi.predictable.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Class for a event with predictions.
 */
@AutoValue
public abstract class PredictedEvent {
    public abstract long getId();

    public abstract @NonNull String getTitle();

    public abstract @Nullable String getDescription();

    public abstract @NonNull Judgement getJudgement();

    public abstract @NonNull Date getDueDate();

    /**
     * Unmodifiable list of predictions for this event.
     *
     * @return The list of predictions for this event, sorted ascending by creation date.
     */
    public abstract @NonNull List<Prediction> getPredictions();

    /**
     * Create a Builder for creating a {@link PredictedEvent}
     *
     * @return A new {@link PredictedEvent.Builder} instance.
     */
    public static Builder builder() {
        return new AutoValue_PredictedEvent.Builder().setJudgement(Judgement.Open);
    }

    /**
     * Convert this {@link PredictedEvent} into a builder.  Use this to create
     * modified copies of a PredictedEvent.
     *
     * @return A Builder initialized from this prediction.
     */
    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        /**
         * @param id A unique id.
         */
        public abstract Builder setId(long id);

        /**
         * @param title A short descriptive title.
         */
        public abstract Builder setTitle(@NonNull String title);

        /**
         * @param description An optional, more detailed description.
         */
        public abstract Builder setDescription(@Nullable String description);

        /**
         * Set the judgement for this Builder.  This is an optional parameter,
         * it defaults to {@link Judgement#Open}.
         *
         * @param judgement How/when the prediction was judged, or Judgement.Open when unjudged.
         */
        public abstract Builder setJudgement(@NonNull Judgement judgement);

        /**
         * @param dueDate The date/time it should be possible to judge this prediction.
         */
        public abstract Builder setDueDate(@NonNull Date dueDate);

        /**
         * @param predictions List giving statements of subjective confidence at different points in
         *                    time.
         */
        public abstract Builder setPredictions(@NonNull List<Prediction> predictions);

        /**
         * @param predictions List giving statements of subjective confidence at different points in
         *                    time.
         */
        public Builder setPredictions(Prediction... predictions) {
            setPredictions(Arrays.asList(predictions));
            return this;
        }

        /**
         * Add a single prediction.
         *
         * @param prediction A single prediction to add.
         */
        public Builder addPrediction(@NonNull Prediction prediction) {
            List<Prediction> predictionsCopy;
            if (getPredictions() == null) {
                predictionsCopy = new ArrayList<>();
            } else {
                // defensive copy to avoid unwanted side effects, also ensure that list is not null
                predictionsCopy = new ArrayList<>(getPredictions());
            }
            predictionsCopy.add(prediction);
            setPredictions(predictionsCopy);
            return this;
        }

        abstract List<Prediction> getPredictions();

        abstract @NonNull PredictedEvent autoBuild();

        /**
         * Build the specified {@link PredictedEvent}.
         *
         * @return A new {@link PredictedEvent} as specified with this Builder.
         */
        public @NonNull PredictedEvent build() {
            // replace user defined list of predictions with
            // sorted immutable copy before invoking the real build() method
            // defined by auto-value
            List<Prediction> predictionsCopy = new ArrayList<>(getPredictions());
            Collections.sort(predictionsCopy, Prediction.byDateComparator);
            setPredictions(Collections.unmodifiableList(predictionsCopy));
            return autoBuild();
        }
    }
}
