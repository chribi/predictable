package de.chribi.predictable.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            Calendar dueDate = Calendar.getInstance();
            dueDate.set(2000 + i, Calendar.JANUARY, 1, 12, 0);
            storage.createPredictionSet(title, null, dueDate, new ArrayList<Prediction>());
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
        int id = storage.getPredictions().get(0).getId();
        assertThat("getRredictionById for invalid Id should return null",
                storage.getPredictionById(id + 1), is(nullValue()));
    }

    @Test
    public void getPredictionByIdForValidIdReturnsMatchingPredictionSet() {
        createTestPredictions(5);
        int id = storage.getPredictions().get(3).getId();
        assertThat("getPredictionById should return PredictionSet with specified id",
                storage.getPredictionById(id).getId(), is(id));
    }

    @Test
    public void createdPredictionSetHasExpectedValues() {
        String title = "TestTitle";
        String description = "TestDescription";
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(2000, Calendar.APRIL, 20, 14, 30);

        List<Prediction> predictions = new ArrayList<>();
        Calendar predictionDate1 = Calendar.getInstance();
        predictionDate1.set(2000, Calendar.JANUARY, 15, 12, 15);
        Prediction prediction1 = new Prediction(0.4, predictionDate1);

        Calendar predictionDate2 = Calendar.getInstance();
        predictionDate2.set(2000, Calendar.FEBRUARY, 3, 19, 7);
        Prediction prediction2 = new Prediction(0.8, predictionDate2);

        predictions.add(prediction1);
        predictions.add(prediction2);

        PredictionSet createdPrediction =
                storage.createPredictionSet("TestTitle", "TestDescription", dueDate, predictions);

        assertThat("Title should be creation title.",
                createdPrediction.getTitle(), is(equalTo(title)));
        assertThat("Description should be creation description.",
                createdPrediction.getDescription(), is(equalTo(description)));
        assertThat("Due date should be the creation dueDate",
                createdPrediction.getDueDate(), is(equalTo(dueDate)));
        assertThat("State of new prediction should be 'Open'",
                createdPrediction.getState(), is(equalTo(PredictionState.Open)));
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
        assertThat("The number of stored predictions should be the number of created predcitions",
                storage.getPredictions().size(), is(numberOfPredictions));
    }

    @Test
    public void createdPredictionShouldBeStoredAsReturned() {
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(2010, Calendar.JANUARY, 1, 12, 0);

        PredictionSet prediction = storage.createPredictionSet("Some title", "A description",
                dueDate, new ArrayList<Prediction>());
        assertThat("Stored prediction should be same as the prediction returned by createPredictionSet",
                storage.getPredictions().get(0), is(equalTo(prediction)));
    }

    @Test
    public void createdPredictionsHaveUniqueIds() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        Set<Integer> ids = new HashSet<>();
        for (PredictionSet predictionSet : storage.getPredictions()) {
            ids.add(predictionSet.getId());
        }
        assertThat("Ids of created predictions have to be unique",
                ids.size(), is(numberOfPredictions));
    }

    @Test
    public void updatePredictionSetDoesNotChangePredictionCount() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        int id = storage.getPredictions().get(0).getId();
        storage.updatePredictionSet(id, new PredictionSet(id, "Some title",
                "Some description", PredictionState.Incorrect, Calendar.getInstance(),
                new ArrayList<Prediction>()));
        assertThat("Updating predictions should not change prediction count",
                storage.getPredictions().size(), is(numberOfPredictions));
    }

    @Test
    public void updatePredictionSetChangesSinglePredictionSet() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        List<PredictionSet> predictions = new ArrayList<>(storage.getPredictions());
        int id = predictions.get(2).getId();
        PredictionSet newPredictionSet = new PredictionSet(id, "Some title",
                "Some description", PredictionState.Correct, Calendar.getInstance(),
                new ArrayList<Prediction>());
        predictions.remove(2);
        storage.updatePredictionSet(id, newPredictionSet);

        for (PredictionSet prediction : predictions) {
            assertThat("Predictions not updated are preserved",
                    storage.getPredictions(), hasItem(equalTo(prediction)));
        }
        assertThat("Updated prediction is stored",
                storage.getPredictions(), hasItem(equalTo(newPredictionSet)));
    }

    @Test
    public void addPredictionAddsPredictionToSinglePredictionSet() {
        createTestPredictions(2);
        int id1 = storage.getPredictions().get(0).getId();
        int id2 = storage.getPredictions().get(1).getId();

        storage.addPredictionToPredictionSet(id2, new Prediction(0.5, Calendar.getInstance()));

        assertThat("Specified PredictionSet has prediction added",
                storage.getPredictionById(id2).getPredictions().size(), is(1));
        assertThat("Other PredictionSets are not affected",
                storage.getPredictionById(id1).getPredictions().size(), is(0));
    }

    @Test
    public void deletePredictionSetReducesPredictionSetCount() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        int id = storage.getPredictions().get(5).getId();

        storage.deletePredictionSet(id);

        assertThat("Deleting a PredictionSet should decrease PredictionSet count by 1",
                storage.getPredictions().size(), is(numberOfPredictions - 1));
    }

    @Test
    public void deletePredictionSetShouldInvalidateId() {
        int numberOfPredictions = 10;
        createTestPredictions(numberOfPredictions);
        int id = storage.getPredictions().get(5).getId();

        storage.deletePredictionSet(id);

        assertThat("After deleting a PredictionSet getPredictionById for that id should return null",
                storage.getPredictionById(id), is(nullValue()));
    }
}
