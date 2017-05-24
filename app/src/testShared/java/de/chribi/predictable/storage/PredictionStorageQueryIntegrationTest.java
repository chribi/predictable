package de.chribi.predictable.storage;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.queries.Combine;
import de.chribi.predictable.storage.queries.PredictionQuery;

import static de.chribi.predictable.storage.queries.PredictionField.DUE_DATE;
import static de.chribi.predictable.storage.queries.PredictionField.ID;
import static de.chribi.predictable.storage.queries.PredictionField.STATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Tests for checking that {@link PredictionQuery PredictionQueries} are correctly interpreted by an
 * implementation of PredictionStorage.
 * @param <Storage> The PredictionStorage implementation under test.
 */
abstract class PredictionStorageQueryIntegrationTest<Storage extends PredictionStorage> {
    private Storage storage;

    public Storage getStorage() { return storage; }

    abstract Storage createStorage();

    private Prediction[] insertPredictions(Prediction... predictions)
    {
        Prediction[] result = new Prediction[predictions.length];
        int i = 0;
        for (Prediction prediction : predictions) {
            long realId = storage.createPrediction("", null, new Date(0),
                    new ArrayList<ConfidenceStatement>()).getId();
            storage.updatePrediction(realId, prediction);
            result[i] = prediction.toBuilder().setId(realId).build();
            i++;
        }
        return result;
    }

    private Prediction.Builder initializedPredictionBuilder() {
        return Prediction.builder()
                .setId(-1)
                .setDueDate(new Date(0))
                .setTitle("")
                .setDescription(null)
                .setJudgement(Judgement.Open)
                .setConfidences();
    }

    private Prediction predictionWithState(PredictionState state) {
        return initializedPredictionBuilder().setJudgement(Judgement.create(state, new Date(0))).build();
    }

    private Prediction predictionWithDueDate(long dueDate) {
        return initializedPredictionBuilder().setDueDate(new Date(dueDate)).build();
    }

    private Prediction[] predictionStateTestPredictions = new Prediction[] {
            predictionWithState(PredictionState.Open),
            predictionWithState(PredictionState.Correct),
            predictionWithState(PredictionState.Invalid),
            predictionWithState(PredictionState.Incorrect),
            predictionWithState(PredictionState.Invalid),
            predictionWithState(PredictionState.Open),
            predictionWithState(PredictionState.Correct)
    };

    private Prediction[] dueDateTestPredictions = new Prediction[] {
            predictionWithDueDate(1000),
            predictionWithDueDate(5000),
            predictionWithDueDate(2000),
            predictionWithDueDate(4000),
            predictionWithDueDate(8000),
            predictionWithDueDate(3000),
            predictionWithDueDate(7000),
    };

    @Before
    public void setUp() {
        storage = createStorage();
    }

    @Test
    public void whereIdEqualTo_validId_returnsExpectedPrediction() {
        Prediction[] predictions = insertPredictions(
                initializedPredictionBuilder().build(),
                initializedPredictionBuilder().build(),
                initializedPredictionBuilder().build());
        PredictionQuery query = PredictionQuery.where(ID.equalTo(predictions[1].getId()));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[1]));
    }

    @Test
    public void whereIdEqualTo_invalidId_returnsNoPrediction() {
        Prediction[] predictions = insertPredictions(
                initializedPredictionBuilder().build(),
                initializedPredictionBuilder().build(),
                initializedPredictionBuilder().build());
        PredictionQuery query = PredictionQuery.where(ID.equalTo(predictions[2].getId() + 1));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, is(empty()));
    }

    @Test
    public void wherePredictionStateIsOpen_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(STATE.equalTo(PredictionState.Open));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[0], predictions[5]));
    }

    @Test
    public void wherePredictionStateIsNotOpen_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(STATE.notEqualTo(PredictionState.Open));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[1], predictions[2], predictions[3],
                predictions[4], predictions[6]));
    }

    @Test
    public void wherePredictionStateIsInvalid_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(STATE.equalTo(PredictionState.Invalid));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[2], predictions[4]));
    }

    @Test
    public void whereDueDateAfter_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(dueDateTestPredictions);
        PredictionQuery query = PredictionQuery.where(DUE_DATE.after(new Date(4000)));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[1], predictions[4], predictions[6]));
    }

    @Test
    public void whereDueDateBefore_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(dueDateTestPredictions);
        PredictionQuery query = PredictionQuery.where(DUE_DATE.before(new Date(4000)));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[0], predictions[2], predictions[5]));
    }

    @Test
    public void whereEmptyConjunction_returnsAllPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(Combine.and());

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions));
    }

    @Test
    public void whereEmptyDisjunction_returnsNoPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(Combine.or());

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, is(empty()));
    }

    @Test
    public void whereSingletonConjunction_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(
                Combine.and(STATE.equalTo(PredictionState.Invalid)));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[2], predictions[4]));
    }

    @Test
    public void whereSingletonDisjunction_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(
                Combine.or(STATE.equalTo(PredictionState.Invalid)));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[2], predictions[4]));
    }

    @Test
    public void whereConjunction_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(
                Combine.and(
                        STATE.notEqualTo(PredictionState.Open),
                        STATE.notEqualTo(PredictionState.Invalid)));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[1],  predictions[3], predictions[6]));
    }

    @Test
    public void whereDisjunction_returnsExpectedPredictions() {
        Prediction[] predictions = insertPredictions(predictionStateTestPredictions);
        PredictionQuery query = PredictionQuery.where(
                Combine.or(
                        STATE.equalTo(PredictionState.Invalid),
                        STATE.equalTo(PredictionState.Incorrect)));

        List<Prediction> queryResult = storage.getPredictions(query);

        assertThat(queryResult, contains(predictions[2], predictions[3], predictions[4]));
    }
}
