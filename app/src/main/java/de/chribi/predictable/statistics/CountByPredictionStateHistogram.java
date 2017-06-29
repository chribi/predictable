package de.chribi.predictable.statistics;


import com.google.auto.value.AutoValue;

import de.chribi.predictable.data.Prediction;

/**
 * Histogram of the count of predictions by
 * {@link de.chribi.predictable.data.PredictionState prediction state}
 */
@AutoValue
public abstract class CountByPredictionStateHistogram {
    static CountByPredictionStateHistogram create(int open, int invalid, int correct, int incorrect) {
        int total = open + invalid + correct + incorrect;
        return new AutoValue_CountByPredictionStateHistogram(total, open, invalid, correct, incorrect);
    }

    /**
     * @return Count of all predictions
     */
    public abstract int getTotalCount();

    /**
     * @return Count of open predictions
     */
    public abstract int getOpenCount();

    /**
     * @return Count of invalid predictions
     */
    public abstract int getInvalidCount();

    /**
     * @return Count of correct predictions
     */
    public abstract int getCorrectCount();

    /**
     * @return Count of incorrect predictions
     */
    public abstract int getIncorrectCount();
}
