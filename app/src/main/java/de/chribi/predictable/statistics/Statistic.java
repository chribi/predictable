package de.chribi.predictable.statistics;


import de.chribi.predictable.data.Prediction;

/**
 * A class to calculate a single Statistic on a set of {@link Prediction Predictions}
 *
 * @param <T> The type of the Statistic
 */
public interface Statistic<T> {
    /**
     * Collect the given prediction into the statistic
     * @param prediction A prediction that should be included in the statistic
     */
    void collect(Prediction prediction);

    /**
     * Calculate and return the value for this Statistic
     * @return The value of the statistic over all collected Predictions
     */
    T value();
}
