package de.chribi.predictable.statistics;


import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

/**
 * Statistic that calculates the average cross entropy over a set of predictions.
 * This will consider all confidence statements for judged predictions (excluding invalid ones).
 */
class AverageCrossEntropyStatistic implements Statistic<Double> {
    public static Statistic<Double> create() {
        return new AverageCrossEntropyStatistic();
    }

    private AverageCrossEntropyStatistic() {}
    private int count = 0;
    private double accumulatedCrossEntropy = 0;

    @Override public void collect(Prediction prediction) {
        PredictionState state = prediction.getJudgement().getState();
        if (state != PredictionState.Correct && state != PredictionState.Incorrect) {
            // only look at predictions that are judged and valid
            return;
        }

        List<ConfidenceStatement> confidences = prediction.getConfidences();
        count += confidences.size();
        boolean correct = state == PredictionState.Correct;
        for (ConfidenceStatement confidence : confidences) {
            double confidenceForTrueResult = correct
                    ? confidence.getConfidence()
                    : 1.0 - confidence.getConfidence();
            accumulatedCrossEntropy -= Math.log(confidenceForTrueResult);
        }
    }

    @Override public Double value() {
        if (count == 0) {
            return 0.0;
        } else {
            // take average and convert to log_2
            return accumulatedCrossEntropy / (count * Math.log(2));
        }
    }
}
