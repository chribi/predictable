package de.chribi.predictable.newprediction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.storage.InMemoryPredictionStorage;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class NewPredictionViewModelTest {
    private NewPredictionViewModel viewModel;
    @Mock private NewPredictionView mockedView;
    @Mock private DateTimeProvider mockedDateTimeProvider;
    private PredictionStorage fakeStorage;
    private Date now;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        fakeStorage = new InMemoryPredictionStorage();
        // Current time is 2000-06-10 10:20:30
        Calendar nowCalendar = new GregorianCalendar(2000, Calendar.JUNE, 10, 10, 20, 30);
        now = nowCalendar.getTime();
        when(mockedDateTimeProvider.getCurrentDateTime())
                .thenReturn(now);
        viewModel = new NewPredictionViewModel(fakeStorage, mockedDateTimeProvider);
        viewModel.setView(mockedView);
    }

    @Test
    public void onSavePredictionClosesWithSavingPrediction() {
        viewModel.onSavePrediction();
        verify(mockedView).closeView();
        assertThat("A prediction was saved",
                fakeStorage.getPredictedEvents().size(), is(1));
    }

    @Test
    public void onCancelClosesWithoutSavingPrediction() {
        viewModel.onCancel();
        verify(mockedView).closeView();
        assertThat("No prediction was saved",
                fakeStorage.getPredictedEvents().size(), is(0));
    }

    private PredictedEvent savePredictionAndReturnSavedEvent() {
        viewModel.onSavePrediction();
        List<PredictedEvent> storedEvents = fakeStorage.getPredictedEvents();
        return storedEvents.get(storedEvents.size() - 1);
    }

    @Test
    public void onSavePredictionSavesPredictionWithCorrectTitle() {
        final String testTitle = "Test event title";
        viewModel.predictedEvent.set(testTitle);

        PredictedEvent storedEvent = savePredictionAndReturnSavedEvent();

        assertThat("Stored predicted event should have title as set in the view model",
                storedEvent.getTitle(), is(equalTo(testTitle)));
    }

    @Test
    public void onSavePredictionSavesPredictionWithCorrectDueDate() {
        final Date testDueDate = new Date(12345678L);
        viewModel.dueDate.set(testDueDate);

        PredictedEvent storedEvent = savePredictionAndReturnSavedEvent();

        assertThat("Stored predicted event should have due date as set in the view model",
                storedEvent.getDueDate(), is(equalTo(testDueDate)));
    }

    @Test
    public void onSavePredictionSavesPredictionWithOnePrediction() {
        PredictedEvent storedEvent = savePredictionAndReturnSavedEvent();

        assertThat("Stored predicted event should have one prediction",
                storedEvent.getPredictions().size(), is(equalTo(1)));
    }

    @Test
    public void onSavePredictionSavesPredictionWithCorrectConfidence() {
        final double testConfidence = 0.4321;
        viewModel.confidencePercentage.set(testConfidence * 100);

        PredictedEvent storedEvent = savePredictionAndReturnSavedEvent();

        assertThat("Stored predicted event should have prediction with confidence as set in the view model",
                storedEvent.getPredictions().get(0).getConfidence(),
                is(closeTo(testConfidence, 1e-8)));
    }

    @Test
    public void onSavePredictionSavesPredictionWithCreationDateNow() {
        PredictedEvent storedEvent = savePredictionAndReturnSavedEvent();

        assertThat("Stored predicted event should have prediction with creation date now",
                storedEvent.getPredictions().get(0).getCreationDate(), is(equalTo(now)));
    }

    @Test
    public void defaultDueDateIsFollowingDayAtNoon() {
        // current time is defined as 2000-06-10 10:20:30 in setUp()
        Date tomorrowNoon = new GregorianCalendar(2000, Calendar.JUNE, 11, 12, 0, 0).getTime();
        assertThat("Due date defaults to 12:00 on the following day",
                viewModel.dueDate.get(), is(tomorrowNoon));
    }

    @Test
    public void defaultConfidenceIs50Percent() {
        assertThat("Default confidence is 50 percent",
                viewModel.confidencePercentage.get(),  closeTo(50, 1e-8));
    }

    @Test
    public void confidenceAbove100IsSetTo100() {
        viewModel.confidencePercentage.set(120);
        assertThat("Confidence above 100 is reduced to 100",
                viewModel.confidencePercentage.get(), closeTo(100, 1e-8));
    }

    @Test
    public void confidenceBelow0IsSetTo0() {
        viewModel.confidencePercentage.set(-1.23);
        assertThat("Confidence below 0 is set to 0",
                viewModel.confidencePercentage.get(), closeTo(0, 1e-8));
    }

    @Test
    public void confidenceBetween0And100IsUnaffected() {
        double[] testValues = new double[] { 23.45, 12.34, 99.999, 0.0001, 0, 100 };
        for (double v : testValues) {
            viewModel.confidencePercentage.set(v);
            assertThat("Confidence between 0 and 100 is unaffected",
                    viewModel.confidencePercentage.get(), closeTo(v, 1e-8));
        }
    }
}