package de.chribi.predictable.newprediction;

/**
 * Interface for callbacks from the {@link NewPredictionViewModel} to the view.
 */
public interface NewPredictionView {
    /**
     * Close the current view and return to the main activity.
     */
    void closeView();
}
