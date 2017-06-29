package de.chribi.predictable.statistics;


import de.chribi.predictable.data.Prediction;

/**
 * Statistic that counts {@link Prediction predictions} grouped by their state and returns
 * the result as a PredictionStateCounts
 */
class PredictionStateStatistic implements Statistic<PredictionStateCounts> {
    public static Statistic<PredictionStateCounts> create() {
        return new PredictionStateStatistic();
    }

    private PredictionStateStatistic() {}

    private int open = 0;
    private int invalid = 0;
    private int correct = 0;
    private int incorrect = 0;

    @Override public void collect(Prediction prediction) {
        switch (prediction.getJudgement().getState()) {
            case Open:
                open++;
                break;
            case Invalid:
                invalid++;
                break;
            case Correct:
                correct++;
                break;
            case Incorrect:
                incorrect++;
                break;
            default:
                throw new AssertionError("Unknown prediction state for prediction: "
                        + prediction.toString());
        }
    }

    @Override public PredictionStateCounts value() {
        return PredictionStateCounts.create(open, invalid, correct, incorrect);
    }
}
