package de.chribi.predictable.statistics;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class AverageCrossEntropyStatisticTest {

    private double calculateStatistic(Prediction... predictions) {
        Statistic<Double> entropyStatistic = AverageCrossEntropyStatistic.create();
        for (Prediction prediction : predictions) {
            entropyStatistic.collect(prediction);
        }
        return entropyStatistic.value();
    }

    private Prediction newPrediction(PredictionState state, double... confidences) {
        List<ConfidenceStatement> statements = new ArrayList<>(confidences.length);
        for (int i = 0; i < confidences.length; i++) {
            statements.add(ConfidenceStatement.create(confidences[i], new Date(i * 1000)));
        }

        return Prediction.builder()
                .setId(0)
                .setTitle("")
                .setDueDate(new Date(0))
                .setJudgement(Judgement.create(state, new Date(0)))
                .setConfidences(statements)
                .build();
    }

    @Test
    public void noConfidences_valueIs0() {
        double value = calculateStatistic(
                newPrediction(PredictionState.Correct),
                newPrediction(PredictionState.Incorrect),
                newPrediction(PredictionState.Open),
                newPrediction(PredictionState.Invalid));

        assertThat(value, is(closeTo(0.0, 1e-8)));
    }

    @Test
    public void openPrediction_valueIs0() {
        double value = calculateStatistic(newPrediction(PredictionState.Open, 0.5, 0.4, 0.1, 0.7));

        assertThat(value, is(closeTo(0.0, 1e-8)));
    }

    @Test
    public void invalidPrediction_valueIs0() {
        double value = calculateStatistic(newPrediction(PredictionState.Invalid, 0.5, 0.4, 0.1, 0.7));

        assertThat(value, is(closeTo(0.0, 1e-8)));
    }

    @Test
    public void correctPrediction_valueIsNegativeBase2LogOfConfidence() {
        double value = calculateStatistic(newPrediction(PredictionState.Correct, 0.25));

        assertThat(value, is(closeTo(2.0, 1e-8))); // 2 = -log_2(0.25)
    }

    @Test
    public void incorrectPrediction_valueIsNegativeBase2LogOfInvertedConfidence() {
        double value = calculateStatistic(newPrediction(PredictionState.Incorrect, 0.25));

        assertThat(value, is(closeTo(0.415037499, 1e-8))); // -log_2(1 - 0.25)
    }

    @Test
    public void multiplePredictions_valueIsAverage() {
        double value = calculateStatistic(
                newPrediction(PredictionState.Correct, 0.25, 0.5), // 2, 1
                newPrediction(PredictionState.Incorrect, 0.5, 0.5, 0.75), // 1, 1, 2
                newPrediction(PredictionState.Open, 0.25, 0.5, 0.75), // ignored
                newPrediction(PredictionState.Invalid, 0.4, 0.3, 0.2, 0.1), // ignored
                newPrediction(PredictionState.Correct, 0.125, 0.5, 0.5)); // 3, 1, 1

        // (2 + 1 + 1 + 1 + 2 + 3 + 1 + 1) / 8 = 1.5
        assertThat(value, is(closeTo(1.5, 1e-8)));
    }
}