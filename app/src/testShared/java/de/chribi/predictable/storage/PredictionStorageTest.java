package de.chribi.predictable.storage;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for all implementations of {@link PredictionStorage}.
 *
 * @param <Storage> The implementation of PredictionStorage under test.
 */
@SuppressWarnings("ConstantConditions")
public abstract class PredictionStorageTest<Storage extends PredictionStorage> {
    private Storage storage;

    public Storage getStorage() {
        return storage;
    }

    /**
     * Create some predictions in the storage.
     *
     * @param count
     */
    private void createTestPredictions(int count) {
        for (int i = 0; i < count; i++) {
            String title = String.format("Test prediction %d", i);
            storage.createPrediction(title, null, new Date(), new ArrayList<ConfidenceStatement>());
        }
    }

    /**
     * Create a new instance of the PredictionStorage implementation under test.
     *
     * @return An empty PredictionStorage.
     */
    abstract Storage createStorage();

    @Before
    public void setUp() {
        storage = createStorage();
    }

    @Test
    public void getPredictionByIdForInvalidIdReturnsNull() {
        createTestPredictions(1);
        long id = storage.getPredictions().get(0).getId();
        assertThat("getPredictionById for invalid Id should return null",
                storage.getPredictionById(id + 1), is(nullValue()));
    }

    @Test
    public void getPredictionByIdForValidIdReturnsMatchingPredictionSet() {
        createTestPredictions(5);
        long id = storage.getPredictions().get(3).getId();
        assertThat("getPredictionById should return Prediction with specified id",
                storage.getPredictionById(id).getId(), is(id));
    }

    @Test
    public void createdPredictionSetHasExpectedValues() {
        String title = "TestTitle";
        String description = "TestDescription";
        Date dueDate = new Date(10000);

        List<ConfidenceStatement> confidenceStatements = new ArrayList<>();
        ConfidenceStatement confidenceStatement1 = ConfidenceStatement.create(0.4, new Date(1000));
        ConfidenceStatement confidenceStatement2 = ConfidenceStatement.create(0.8, new Date(2000));

        confidenceStatements.add(confidenceStatement1);
        confidenceStatements.add(confidenceStatement2);

        Prediction createdPrediction =
                storage.createPrediction("TestTitle", "TestDescription", dueDate, confidenceStatements);

        assertThat("Title should be creation title.",
                createdPrediction.getTitle(), is(equalTo(title)));
        assertThat("Description should be creation description.",
                createdPrediction.getDescription(), is(equalTo(description)));
        assertThat("Due date should be the creation dueDate",
                createdPrediction.getDueDate(), is(equalTo(dueDate)));
        assertThat("New ConfidenceStatement should be unjudged",
                createdPrediction.getJudgement(), is(equalTo(Judgement.Open)));
        assertThat("Confidences from creation should be contained in the confidenceStatements",
                createdPrediction.getConfidences(), containsInAnyOrder(
                        equalTo(confidenceStatement1),
                        equalTo(confidenceStatement2)));
        assertThat("There should be no additional confidenceStatements",
                createdPrediction.getConfidences().size(), is(2));
    }

    @Test
    public void createdPredictionsShouldBeStored() {
        int numberOfPredictions = 5;
        createTestPredictions(numberOfPredictions);
        assertThat("The number of stored predictions should be the number of created predictions",
                storage.getPredictions().size(), is(numberOfPredictions));
    }

    @Test
    public void createdPredictionShouldBeStoredAsReturned() {
        Prediction prediction = storage.createPrediction("Some title", "A description",
                new Date(1000), new ArrayList<ConfidenceStatement>());
        assertThat("Stored prediction should be same as the prediction returned by createPrediction",
                storage.getPredictions().get(0), is(equalTo(prediction)));
    }

    @Test
    public void createdPredictionsHaveUniqueIds() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        Set<Long> ids = new HashSet<>();
        for (Prediction prediction : storage.getPredictions()) {
            ids.add(prediction.getId());
        }
        assertThat("Ids of created predictions have to be unique",
                ids.size(), is(numberOfPredictions));
    }

    @Test
    public void updatePredictionSetDoesNotChangePredictionCount() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        Prediction prediction = storage.getPredictions().get(0);
        Prediction newPrediction = prediction.toBuilder()
                .setTitle("Some title")
                .setDescription("Some description")
                .setDueDate(new Date(5000))
                .build();
        storage.updatePrediction(prediction.getId(), newPrediction);

        assertThat("Updating predictions should not change prediction count",
                storage.getPredictions().size(), is(numberOfPredictions));
    }

    @Test
    public void updatePredictionChangesSinglePrediction() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        List<Prediction> predictions = new ArrayList<>(storage.getPredictions());
        Prediction prediction2 = predictions.get(2);
        predictions.remove(2);
        Prediction newPrediction = prediction2.toBuilder()
                .setTitle("Some title")
                .setDescription("Some description")
                .setDueDate(new Date(3500))
                .build();

        storage.updatePrediction(prediction2.getId(), newPrediction);

        for (Prediction prediction : predictions) {
            assertThat("Predictions not updated are preserved",
                    storage.getPredictions(), hasItem(equalTo(prediction)));
        }
        assertThat("Updated prediction is stored",
                storage.getPredictions(), hasItem(equalTo(newPrediction)));
    }

    @Test
    public void addConfidenceAddsToSinglePredictionSet() {
        createTestPredictions(2);
        long id1 = storage.getPredictions().get(0).getId();
        Prediction prediction2 = storage.getPredictions().get(1);
        long id2 = prediction2.getId();

        Prediction updatedPrediction = prediction2.toBuilder()
                .addConfidence(ConfidenceStatement.create(0.5, new Date()))
                .build();
        storage.updatePrediction(id2, updatedPrediction);

        assertThat("Specified Prediction has prediction added",
                storage.getPredictionById(id2).getConfidences().size(), is(1));
        assertThat("Other Predictions are not affected",
                storage.getPredictionById(id1).getConfidences().size(), is(0));
    }

    @Test
    public void deletePredictionSetReducesPredictionSetCount() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        long id = storage.getPredictions().get(5).getId();

        storage.deletePrediction(id);

        assertThat("Deleting a Prediction should decrease Prediction count by 1",
                storage.getPredictions().size(), is(numberOfPredictions - 1));
    }

    @Test
    public void deletePredictionSetShouldInvalidateId() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        long id = storage.getPredictions().get(5).getId();

        storage.deletePrediction(id);

        assertThat("After deleting a Prediction getPredictionById for that id should return null",
                storage.getPredictionById(id), is(nullValue()));
    }
}
