package de.chribi.predictable.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class PredictedEventTest {

    @Test
    public void getJudgement_defaultsToOpen() {
        PredictedEvent event = PredictedEvent.builder()
                .setId(0)
                .setTitle("Title")
                .setDescription("Description")
                .setDueDate(new Date())
                .setPredictions()
                .build();

        assertThat("Judgement should be 'Open' when not specified",
                event.getJudgement(), equalTo(Judgement.Open));
    }

    @Test
    public void getPredictions_isSortedByDate() {
        Prediction prediction1 = Prediction.create(0.1, new Date(1000L));
        Prediction prediction2 = Prediction.create(0.2, new Date(2000L));
        Prediction prediction3 = Prediction.create(0.3, new Date(3000L));
        Prediction prediction4 = Prediction.create(0.4, new Date(4000L));

        PredictedEvent event = PredictedEvent.builder()
                .setId(0)
                .setTitle("Title")
                .setDescription("Description")
                .setDueDate(new Date())
                .setPredictions(prediction2, prediction3, prediction1, prediction4)
                .build();

        assertThat("Predictions of a predicted event have to be in order of creation date",
                event.getPredictions(), contains(
                        equalTo(prediction1),
                        equalTo(prediction2),
                        equalTo(prediction3),
                        equalTo(prediction4)));

    }

    @Test
    public void builder_createsCopyOfPredictions() {
        List<Prediction> predictions = new ArrayList<>();
        predictions.add(Prediction.create(0.5, new Date()));

        PredictedEvent event = PredictedEvent.builder()
                .setId(0)
                .setTitle("Title")
                .setDescription("Description")
                .setDueDate(new Date())
                .setPredictions(predictions)
                .build();

        predictions.add(Prediction.create(0.4, new Date()));

        assertThat("Modifying the predictions passed in the builder should not " +
                        "affect the created event",
                event.getPredictions().size(), equalTo(1));
    }
}