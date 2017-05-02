package de.chribi.predictable.data;

import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;

import java.util.Comparator;
import java.util.Date;


/**
 * A single prediction.  A prediction always belongs to a {@link PredictedEvent}.
 */
@AutoValue
public abstract class Prediction {
    /**
     * Create a {@link Prediction}.
     * @param confidence The subjective confidence that the predicted event happens.
     * @param creationDate The date the prediction was made.
     */
    public static Prediction create(double confidence, @NonNull Date creationDate) {
        return new AutoValue_Prediction(confidence, creationDate);
    }

    public abstract double getConfidence();
    public abstract @NonNull Date getCreationDate();

    /**
     * Default comparator for {@link Prediction}, ordering by creation date.
     */
    public static final Comparator<Prediction> byDateComparator = new Comparator<Prediction>() {
        @Override
        public int compare(Prediction o1, Prediction o2) {
            return o1.getCreationDate().compareTo(o2.getCreationDate());
        }
    };
}
