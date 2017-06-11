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
 * Class something to predict.
 */
@AutoValue
public abstract class Prediction {
    public abstract long getId();

    public abstract @NonNull String getTitle();

    public abstract @Nullable String getDescription();

    public abstract @NonNull Judgement getJudgement();

    public abstract @NonNull Date getDueDate();

    /**
     * Unmodifiable list of confidence statements for this prediction.
     *
     * @return The list of confidence statements for this prediction, sorted ascending by creation date.
     */
    public abstract @NonNull List<ConfidenceStatement> getConfidences();

    /**
     * Create a Builder for creating a {@link Prediction}
     *
     * @return A new {@link Prediction.Builder} instance.
     */
    public static Builder builder() {
        return new AutoValue_Prediction.Builder().setJudgement(Judgement.Open);
    }

    /**
     * Convert this {@link Prediction} into a builder.  Use this to create
     * modified copies of a Prediction.
     *
     * @return A Builder initialized from this prediction.
     */
    public abstract Builder toBuilder();

    @SuppressWarnings("NullableProblems")
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
         * @param confidenceStatements List giving statements of subjective confidence at different
         *                             points in time.
         */
        public abstract Builder setConfidences(@NonNull List<ConfidenceStatement> confidenceStatements);

        /**
         * @param confidenceStatements List giving statements of subjective confidence at different
         *                             points in time.
         */
        public Builder setConfidences(ConfidenceStatement... confidenceStatements) {
            setConfidences(Arrays.asList(confidenceStatements));
            return this;
        }

        /**
         * Add a single confidence statement.
         *
         * @param confidenceStatement A single confidence statement to add.
         */
        public Builder addConfidence(@NonNull ConfidenceStatement confidenceStatement) {
            List<ConfidenceStatement> confidencesCopy;
            if (getConfidences() == null) {
                confidencesCopy = new ArrayList<>();
            } else {
                // defensive copy to avoid unwanted side effects, also ensure that list is not null
                confidencesCopy = new ArrayList<>(getConfidences());
            }
            confidencesCopy.add(confidenceStatement);
            setConfidences(confidencesCopy);
            return this;
        }

        abstract List<ConfidenceStatement> getConfidences();

        abstract @NonNull Prediction autoBuild();

        /**
         * Build the specified {@link Prediction}.
         *
         * @return A new {@link Prediction} as specified with this Builder.
         */
        public @NonNull Prediction build() {
            // replace user defined list of confidences with
            // sorted immutable copy before invoking the real build() method
            // defined by auto-value
            List<ConfidenceStatement> confidencesCopy = new ArrayList<>(getConfidences());
            Collections.sort(confidencesCopy, ConfidenceStatement.byDateComparator);
            setConfidences(Collections.unmodifiableList(confidencesCopy));
            return autoBuild();
        }
    }
}
