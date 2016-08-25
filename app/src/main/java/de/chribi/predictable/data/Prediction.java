package de.chribi.predictable.data;

import android.support.annotation.NonNull;

import java.util.Date;


/**
 * A single prediction.  A prediction always belongs to a {@link PredictedEvent}.
 */
public class Prediction {
    private final double confidence;
    private final @NonNull Date creationDate;
    /**
     * Create a {@link Prediction}.
     * @param confidence The subjective confidence that the predicted event happens.
     * @param creationDate The date the prediction was made.
     */
    public Prediction(double confidence, @NonNull Date creationDate) {
        this.confidence = confidence;
        this.creationDate = creationDate;
    }

    public double getConfidence() {
        return confidence;
    }

    @NonNull
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prediction that = (Prediction) o;

        if (Double.compare(that.confidence, confidence) != 0) return false;
        return creationDate.equals(that.creationDate);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(confidence);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + creationDate.hashCode();
        return result;
    }
}
