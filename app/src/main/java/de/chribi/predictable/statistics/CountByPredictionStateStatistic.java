package de.chribi.predictable.statistics;


import de.chribi.predictable.data.Prediction;

/**
 * Statistic that counts {@link Prediction predictions} grouped by their state and returns
 * the result as a CountByPredictionStateHistogram
 */
class CountByPredictionStateStatistic implements Statistic<CountByPredictionStateHistogram> {
    public static Statistic<CountByPredictionStateHistogram> create() {
        return new CountByPredictionStateStatistic();
    }

    private CountByPredictionStateStatistic() {}

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

    @Override public CountByPredictionStateHistogram value() {
        return CountByPredictionStateHistogram.create(open, invalid, correct, incorrect);
    }
}
