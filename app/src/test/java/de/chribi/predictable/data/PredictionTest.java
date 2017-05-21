package de.chribi.predictable.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class PredictionTest {

    @Test
    public void getJudgement_defaultsToOpen() {
        Prediction prediction = Prediction.builder()
                .setId(0)
                .setTitle("Title")
                .setDescription("Description")
                .setDueDate(new Date())
                .setConfidences()
                .build();

        assertThat("Judgement should be 'Open' when not specified",
                prediction.getJudgement(), equalTo(Judgement.Open));
    }

    @Test
    public void getPredictions_isSortedByDate() {
        ConfidenceStatement confidenceStatement1 = ConfidenceStatement.create(0.1, new Date(1000L));
        ConfidenceStatement confidenceStatement2 = ConfidenceStatement.create(0.2, new Date(2000L));
        ConfidenceStatement confidenceStatement3 = ConfidenceStatement.create(0.3, new Date(3000L));
        ConfidenceStatement confidenceStatement4 = ConfidenceStatement.create(0.4, new Date(4000L));

        Prediction prediction = Prediction.builder()
                .setId(0)
                .setTitle("Title")
                .setDescription("Description")
                .setDueDate(new Date())
                .setConfidences(confidenceStatement2, confidenceStatement3, confidenceStatement1, confidenceStatement4)
                .build();

        assertThat("Confidence statements of a prediction have to be in order of creation date",
                prediction.getConfidences(), contains(
                        equalTo(confidenceStatement1),
                        equalTo(confidenceStatement2),
                        equalTo(confidenceStatement3),
                        equalTo(confidenceStatement4)));

    }

    @Test
    public void builder_createsCopyOfPredictions() {
        List<ConfidenceStatement> confidenceStatements = new ArrayList<>();
        confidenceStatements.add(ConfidenceStatement.create(0.5, new Date()));

        Prediction prediction = Prediction.builder()
                .setId(0)
                .setTitle("Title")
                .setDescription("Description")
                .setDueDate(new Date())
                .setConfidences(confidenceStatements)
                .build();

        confidenceStatements.add(ConfidenceStatement.create(0.4, new Date()));

        assertThat("Modifying the confidences passed in the builder should not " +
                        "affect the created prediction",
                prediction.getConfidences().size(), equalTo(1));
    }
}