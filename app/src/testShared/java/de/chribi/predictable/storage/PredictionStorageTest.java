package de.chribi.predictable.storage;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for all implementations of {@link PredictionStorage}.
 * @param <Storage> The implementation of PredictionStorage under test.
 */
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
    public void createTestPredictions(int count) {
        for (int i = 0; i < count; i++) {
            String title = String.format("Test event %d", i);
            storage.createPredictedEvent(title, null, new Date(), new ArrayList<Prediction>());
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
        long id = storage.getPredictedEvents().get(0).getId();
        assertThat("getPredictionById for invalid Id should return null",
                storage.getPredictedEventById(id + 1), is(nullValue()));
    }

    @Test
    public void getPredictionByIdForValidIdReturnsMatchingPredictionSet() {
        createTestPredictions(5);
        long id = storage.getPredictedEvents().get(3).getId();
        assertThat("getPredictedEventById should return PredictedEvent with specified id",
                storage.getPredictedEventById(id).getId(), is(id));
    }

    @Test
    public void createdPredictionSetHasExpectedValues() {
        String title = "TestTitle";
        String description = "TestDescription";
        Date dueDate = new Date(10000);

        List<Prediction> predictions = new ArrayList<>();
        Prediction prediction1 = new Prediction(0.4, new Date(1000));
        Prediction prediction2 = new Prediction(0.8, new Date(2000));

        predictions.add(prediction1);
        predictions.add(prediction2);

        PredictedEvent createdPrediction =
                storage.createPredictedEvent("TestTitle", "TestDescription", dueDate, predictions);

        assertThat("Title should be creation title.",
                createdPrediction.getTitle(), is(equalTo(title)));
        assertThat("Description should be creation description.",
                createdPrediction.getDescription(), is(equalTo(description)));
        assertThat("Due date should be the creation dueDate",
                createdPrediction.getDueDate(), is(equalTo(dueDate)));
        assertThat("New Prediction should be unjudged",
                createdPrediction.getJudgement(), is(nullValue()));
        assertThat("Predictions from creation should be contained in the predictions",
                createdPrediction.getPredictions(), containsInAnyOrder(
                        equalTo(prediction1),
                        equalTo(prediction2)));
        assertThat("There should be no additional predictions",
                createdPrediction.getPredictions().size(), is(2));
    }

    @Test
    public void createdPredictionsShouldBeStored() {
        int numberOfPredictions = 5;
        createTestPredictions(numberOfPredictions);
        assertThat("The number of stored predictions should be the number of created predictions",
                storage.getPredictedEvents().size(), is(numberOfPredictions));
    }

    @Test
    public void createdPredictionShouldBeStoredAsReturned() {
        PredictedEvent prediction = storage.createPredictedEvent("Some title", "A description",
                new Date(1000), new ArrayList<Prediction>());
        assertThat("Stored prediction should be same as the prediction returned by createPredictedEvent",
                storage.getPredictedEvents().get(0), is(equalTo(prediction)));
    }

    @Test
    public void createdPredictionsHaveUniqueIds() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        Set<Long> ids = new HashSet<>();
        for (PredictedEvent predictedEvent : storage.getPredictedEvents()) {
            ids.add(predictedEvent.getId());
        }
        assertThat("Ids of created predictions have to be unique",
                ids.size(), is(numberOfPredictions));
    }

    @Test
    public void updatePredictionSetDoesNotChangePredictionCount() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        long id = storage.getPredictedEvents().get(0).getId();
        storage.updatePredictedEvent(id, new PredictedEvent(id, "Some title",
                "Some description", null, new Date(5000),
                new ArrayList<Prediction>()));
        assertThat("Updating predictions should not change prediction count",
                storage.getPredictedEvents().size(), is(numberOfPredictions));
    }

    @Test
    public void updatePredictedEventChangesSinglePredictionSet() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        List<PredictedEvent> predictions = new ArrayList<>(storage.getPredictedEvents());
        long id = predictions.get(2).getId();
        PredictedEvent newPredictedEvent = new PredictedEvent(id, "Some title",
                "Some description", null, new Date(3500),
                new ArrayList<Prediction>());
        predictions.remove(2);
        storage.updatePredictedEvent(id, newPredictedEvent);

        for (PredictedEvent prediction : predictions) {
            assertThat("Predictions not updated are preserved",
                    storage.getPredictedEvents(), hasItem(equalTo(prediction)));
        }
        assertThat("Updated prediction is stored",
                storage.getPredictedEvents(), hasItem(equalTo(newPredictedEvent)));
    }

    @Test
    public void addPredictionAddsPredictionToSinglePredictionSet() {
        createTestPredictions(2);
        long id1 = storage.getPredictedEvents().get(0).getId();
        long id2 = storage.getPredictedEvents().get(1).getId();

        storage.addPredictionToPredictedEvent(id2, new Prediction(0.5, new Date()));

        assertThat("Specified PredictedEvent has prediction added",
                storage.getPredictedEventById(id2).getPredictions().size(), is(1));
        assertThat("Other PredictedEvents are not affected",
                storage.getPredictedEventById(id1).getPredictions().size(), is(0));
    }

    @Test
    public void addPredictionPreservesPredictionOrderConstraint() {
        // The predictions of a predicted event always have to be in order of creation date
        long id =   storage.createPredictedEvent("Test", null, new Date(100000L),
                new ArrayList<Prediction>()).getId();

        Prediction prediction1 = new Prediction(0.1, new Date(1000L));
        Prediction prediction2 = new Prediction(0.2, new Date(2000L));
        Prediction prediction3 = new Prediction(0.3, new Date(3000L));
        Prediction prediction4 = new Prediction(0.4, new Date(4000L));

        storage.addPredictionToPredictedEvent(id, prediction2);
        storage.addPredictionToPredictedEvent(id, prediction3);
        storage.addPredictionToPredictedEvent(id, prediction1);
        storage.addPredictionToPredictedEvent(id, prediction4);

        List<Prediction> predictions = storage.getPredictedEventById(id).getPredictions();

        assertThat("Predictions of a predicted event have to be in order of creation date",
                predictions, contains(equalTo(prediction1),
                        equalTo(prediction2), equalTo(prediction3),
                        equalTo(prediction4)));
    }

    @Test
    public void deletePredictionSetReducesPredictionSetCount() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        long id = storage.getPredictedEvents().get(5).getId();

        storage.deletePredictedEvent(id);

        assertThat("Deleting a PredictedEvent should decrease PredictedEvent count by 1",
                storage.getPredictedEvents().size(), is(numberOfPredictions - 1));
    }

    @Test
    public void deletePredictionSetShouldInvalidateId() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        long id = storage.getPredictedEvents().get(5).getId();

        storage.deletePredictedEvent(id);

        assertThat("After deleting a PredictedEvent getPredictedEventById for that id should return null",
                storage.getPredictedEventById(id), is(nullValue()));
    }
}
