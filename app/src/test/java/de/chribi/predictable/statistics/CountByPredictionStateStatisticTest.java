package de.chribi.predictable.statistics;

import org.junit.Test;

import java.util.Date;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CountByPredictionStateStatisticTest {

    private CountByPredictionStateHistogram calculateStatistic(Prediction... predictions) {
        Statistic<CountByPredictionStateHistogram> countStatistic =
                CountByPredictionStateStatistic.create();
        for (Prediction prediction : predictions) {
            countStatistic.collect(prediction);
        }
        return countStatistic.value();
    }

    private Prediction newPrediction(PredictionState state) {
        return Prediction.builder()
                .setId(0)
                .setTitle("")
                .setDueDate(new Date(0))
                .setJudgement(Judgement.create(state, new Date(0)))
                .setConfidences()
                .build();
    }

    @Test
    public void multiplePredictions_valueIsCorrect() {
        CountByPredictionStateHistogram value = calculateStatistic(
                newPrediction(PredictionState.Correct),
                newPrediction(PredictionState.Invalid),
                newPrediction(PredictionState.Correct),
                newPrediction(PredictionState.Open),
                newPrediction(PredictionState.Incorrect),
                newPrediction(PredictionState.Invalid),
                newPrediction(PredictionState.Incorrect),
                newPrediction(PredictionState.Correct),
                newPrediction(PredictionState.Incorrect),
                newPrediction(PredictionState.Correct));

        assertThat(value.getCorrectCount(), is(4));
        assertThat(value.getIncorrectCount(), is(3));
        assertThat(value.getInvalidCount(), is(2));
        assertThat(value.getOpenCount(), is(1));
        assertThat(value.getTotalCount(), is(10));
    }

}