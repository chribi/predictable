package de.chribi.predictable.statistics;


import com.google.auto.value.AutoValue;

import java.util.List;

import de.chribi.predictable.data.Prediction;

@AutoValue
public abstract class Statistics {

    // default boundary values for the calibration histogram groups
    private static final float[] defaultBoundaries = new float[] {
            0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f
    };

    public static Statistics of(List<Prediction> predictions) {
        Statistic<PredictionStateCounts> countStatistic
                = PredictionStateStatistic.create();
        Statistic<Double> avgCrossEntropy = AverageCrossEntropyStatistic.create();
        CalibrationStatistic calibration =
                CalibrationStatistic.create(defaultBoundaries);

        for (Prediction prediction : predictions) {
            countStatistic.collect(prediction);
            avgCrossEntropy.collect(prediction);
            calibration.collect(prediction);
        }

        return new AutoValue_Statistics(countStatistic.value(),
                avgCrossEntropy.value(),
                calibration.value());
    }

    public abstract PredictionStateCounts getCountStatistic();
    public abstract double getAverageCrossEntropy();
    public abstract List<CalibrationHistogramGroup> getCalibrationHistogram();
}
