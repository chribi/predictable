package de.chribi.predictable.statistics;


import com.google.auto.value.AutoValue;

import java.util.List;

import de.chribi.predictable.data.Prediction;

@AutoValue
public abstract class Statistics {
    public static Statistics of(List<Prediction> predictions) {
        Statistic<CountByPredictionStateHistogram> countStatistic
                = CountByPredictionStateStatistic.create();
        Statistic<Double> avgCrossEntropy = AverageCrossEntropyStatistic.create();

        for (Prediction prediction : predictions) {
            countStatistic.collect(prediction);
            avgCrossEntropy.collect(prediction);
        }

        return new AutoValue_Statistics(countStatistic.value(),
                avgCrossEntropy.value());
    }

    public abstract CountByPredictionStateHistogram getCountStatistic();
    public abstract double getAverageCrossEntropy();
}
