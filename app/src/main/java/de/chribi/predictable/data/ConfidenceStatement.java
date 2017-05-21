package de.chribi.predictable.data;

import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;

import java.util.Comparator;
import java.util.Date;


/**
 * A statement of confidence.  Always belongs to a {@link Prediction}.
 */
@AutoValue
public abstract class ConfidenceStatement {
    /**
     * Create a {@link ConfidenceStatement}.
     * @param confidence The subjective confidence that the prediction is true.
     * @param creationDate The date the confidence statement was made.
     */
    public static ConfidenceStatement create(double confidence, @NonNull Date creationDate) {
        return new AutoValue_ConfidenceStatement(confidence, creationDate);
    }

    public abstract double getConfidence();
    public abstract @NonNull Date getCreationDate();

    /**
     * Default comparator for {@link ConfidenceStatement}, ordering by creation date.
     */
    public static final Comparator<ConfidenceStatement> byDateComparator = new Comparator<ConfidenceStatement>() {
        @Override
        public int compare(ConfidenceStatement o1, ConfidenceStatement o2) {
            return o1.getCreationDate().compareTo(o2.getCreationDate());
        }
    };
}
