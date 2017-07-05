package de.chribi.predictable.statistics;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.BR;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.storage.PredictionStorage;

public class StatisticsViewModel extends BaseObservable {

    // Default boundary values for the calibration histogram groups
    // This needs to be symmetric around 0.5f for the current implementation
    // of calculating statistics with inverted low-confidence predictions
    private static final float[] defaultBoundaries = new float[] {
            0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f
    };

    private final PredictionStorage storage;

    private PredictionStateCounts stateCounts;
    private double averageCrossEntropy;
    private List<CalibrationHistogramGroup> calibrationHistogramData;
    private List<CalibrationHistogramGroup> reducedCalibrationHistogramData;
    private boolean invertLowConfidencePredictions;

    @Inject
    public StatisticsViewModel(PredictionStorage storage) {
        this.storage = storage;
        calculateStatistics(storage.getPredictions());
    }

    private void calculateStatistics(List<Prediction> predictions) {
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

        stateCounts = countStatistic.value();
        averageCrossEntropy = avgCrossEntropy.value();
        calibrationHistogramData = calibration.value();
        invertLowConfidencePredictions = false;

        int halfHistogram = calibrationHistogramData.size() / 2;
        reducedCalibrationHistogramData = new ArrayList<>(halfHistogram);
        for (int i = 0; i < halfHistogram; i++) {
            CalibrationHistogramGroup probableGroup
                    = calibrationHistogramData.get(halfHistogram + i);
            CalibrationHistogramGroup improbableGroup
                    = calibrationHistogramData.get(halfHistogram - 1 - i);
            CalibrationHistogramGroup group = CalibrationHistogramGroup.create(
                    probableGroup.getLowerBound(), probableGroup.getUpperBound(),
                    probableGroup.getCorrectCount() + improbableGroup.getIncorrectCount(),
                    probableGroup.getIncorrectCount() + improbableGroup.getCorrectCount());
            reducedCalibrationHistogramData.add(group);
        }
    }

    /**
     * Statistics for number of predictions per prediction state
     * @return A {@link PredictionStateCounts} containing count statistics for all predictions
     */
    @Bindable
    public PredictionStateCounts getStateCounts() {
        return stateCounts;
    }

    /**
     * Average cross entropy
     * @return The average cross entropy over all predictions
     */
    @Bindable
    public double getAverageCrossEntropy() {
        return averageCrossEntropy;
    }

    /**
     * The data for the calibration histogram and table.  Grouping changes depending on
     * {@link #isInvertLowConfidencePredictions()}
     * @return Statistics about accuracy over all predictions, grouped by confidence.
     */
    @Bindable
    public List<CalibrationHistogramGroup> getCalibrationHistogram() {
        if(invertLowConfidencePredictions) {
            return reducedCalibrationHistogramData;
        } else {
            return calibrationHistogramData;
        }
    }

    /**
     * Whether predictions with confidence c < 0.5 should be grouped with predictions
     * with confidence 1 - c >= 0.5 by inverting the result.  This affects the
     * {@link #getCalibrationHistogram()} calibration histogram
     * @return Whether predictions with confidence < 0.5 should be inverted for the
     * calibration histogram
     */
    @Bindable
    public boolean isInvertLowConfidencePredictions() {
        return invertLowConfidencePredictions;
    }

    /**
     * Set the option to invert predictions with confidence < 0.5 for the calibration histogram
     * @param invertLowConfidencePredictions The new value for the option
     */
    public void setInvertLowConfidencePredictions(boolean invertLowConfidencePredictions) {
        if(this.invertLowConfidencePredictions != invertLowConfidencePredictions) {
            this.invertLowConfidencePredictions = invertLowConfidencePredictions;
            notifyPropertyChanged(BR.calibrationHistogram);
            notifyPropertyChanged(BR.invertLowConfidencePredictions);
        }
    }
}
