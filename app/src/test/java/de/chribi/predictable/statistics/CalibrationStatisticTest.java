package de.chribi.predictable.statistics;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.*;

public class CalibrationStatisticTest {
    private List<CalibrationHistogramGroup> calculateStatistic(
            float[] boundaries,
            Prediction... predictions) {
        CalibrationStatistic statistic =
                CalibrationStatistic.create(boundaries);
        for (Prediction prediction : predictions) {
            statistic.collect(prediction);
        }
        return statistic.value();
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
    public void noBoundaries_valueHasSingleGroupFrom0To1() {
        List<CalibrationHistogramGroup> groups =
                calculateStatistic(new float[] {});

        assertThat(groups.size(), is(1));
        assertThat(groups.get(0), is(groupWithBounds(0.f, 1.f)));
    }

    @Test
    public void value_hasGroupsWithGivenBoundaries() {
        List<CalibrationHistogramGroup> groups =
                calculateStatistic(new float[] { 0.2f, 0.8f });
        assertThat(groups, contains(
                groupWithBounds(0.0f, 0.2f), groupWithBounds(0.2f, 0.8f), groupWithBounds(0.8f, 1.f)));
    }

    @Test
    public void value_ignoresOpenAndInvalidPredictions() {
        List<CalibrationHistogramGroup> groups =
                calculateStatistic(new float[] { 0.5f },
                        newPrediction(PredictionState.Invalid, 0.1, 0.2, 0.7),
                        newPrediction(PredictionState.Open, 0.25, 0.6, 0.9, 0.4));
        assertThat(groups, contains(
                groupWithValues(0, 0), groupWithValues(0, 0)));
    }

    @Test
    public void value_countsAndGroupsCorrectly() {
        List<CalibrationHistogramGroup> groups =
                calculateStatistic(new float[] { 0.5f },
                        newPrediction(PredictionState.Correct, 0.2, 0.1, 0.7, 0.6),
                        newPrediction(PredictionState.Incorrect, 0.15, 0.9, 0.2, 0.6),
                        newPrediction(PredictionState.Correct, 1.0, 0.8, 0.2, 0.7, 0.3),
                        newPrediction(PredictionState.Incorrect, 0.0));

        assertThat(groups, contains(
                groupWithValues(4, 3),
                groupWithValues(5, 2)));
    }


    private static Matcher<Object> groupWithBounds(float lower, float upper) {
        return both(hasProperty("lowerBound", equalTo(lower)))
                .and(hasProperty("upperBound", equalTo(upper)));
    }

    private static Matcher<Object> groupWithValues(int correct, int incorrect) {
        return both(hasProperty("correctCount", equalTo(correct)))
                .and(hasProperty("incorrectCount", equalTo(incorrect)));
    }
}

