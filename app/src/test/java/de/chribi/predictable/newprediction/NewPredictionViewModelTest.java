package de.chribi.predictable.newprediction;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.Prediction;
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
    // private Date now;
    private DateTimeZone timeZone;
    private DateTime now;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        fakeStorage = new InMemoryPredictionStorage();
        // time zone is UTC-2
        timeZone = DateTimeZone.forOffsetHours(-2);
        // Current time is 2000-06-10 10:20:30 in time zone UTC-2
        now = new DateTime(2000, DateTimeConstants.JUNE, 10, 10, 20, 30, timeZone);

        when(mockedDateTimeProvider.getCurrentDateTime())
                .thenReturn(now.toDate());
        when(mockedDateTimeProvider.getCurrentTimeZone())
                .thenReturn(timeZone);

        viewModel = new NewPredictionViewModel(fakeStorage, mockedDateTimeProvider);
        viewModel.setView(mockedView);
    }

    @Test
    public void onSavePredictionClosesWithSavingPrediction() {
        viewModel.onSavePrediction();
        verify(mockedView).closeView();
        assertThat("A prediction was saved",
                fakeStorage.getPredictions().size(), is(1));
    }

    @Test
    public void onCancelClosesWithoutSavingPrediction() {
        viewModel.onCancel();
        verify(mockedView).closeView();
        assertThat("No prediction was saved",
                fakeStorage.getPredictions().size(), is(0));
    }

    private Prediction savePredictionAndReturn() {
        viewModel.onSavePrediction();
        List<Prediction> storedPredictions = fakeStorage.getPredictions();
        return storedPredictions.get(storedPredictions.size() - 1);
    }

    @Test
    public void onSavePredictionSavesPredictionWithCorrectTitle() {
        final String testTitle = "Test prediction title";
        viewModel.setPredictionTitle(testTitle);

        Prediction storedPrediction = savePredictionAndReturn();

        assertThat("Stored prediction should have title as set in the view model",
                storedPrediction.getTitle(), is(equalTo(testTitle)));
    }

    @Test
    public void onSavePredictionSavesPredictionWithCorrectDueDate() {
        LocalDate testDueDate = new LocalDate(1970, DateTimeConstants.JANUARY, 4);
        LocalTime testDueTime = new LocalTime(16, 23);
        viewModel.setLocalDueDate(testDueDate);
        viewModel.setLocalDueTime(testDueTime);
        long dueDateEpochMilliseconds =
                3 * DateTimeConstants.MILLIS_PER_DAY // 3 days after 1970-01-01
                + 16 * DateTimeConstants.MILLIS_PER_HOUR // hour of day = 16
                + 23 * DateTimeConstants.MILLIS_PER_MINUTE // minute of hour = 23
                + 2 * DateTimeConstants.MILLIS_PER_HOUR; // time zone = UTC-2
        Date testDueDateTime = new Date(dueDateEpochMilliseconds);

        Prediction storedPrediction = savePredictionAndReturn();

        assertThat("Stored prediction should have due date as set in the view model",
                storedPrediction.getDueDate(), is(equalTo(testDueDateTime)));
    }

    @Test
    public void onSavePredictionSavesPredictionWithOnePrediction() {
        Prediction storedPrediction = savePredictionAndReturn();

        assertThat("Stored prediction should have one prediction",
                storedPrediction.getConfidences().size(), is(equalTo(1)));
    }

    @Test
    public void onSavePredictionSavesPredictionWithCorrectConfidence() {
        final double testConfidence = 0.4321;
        viewModel.setConfidencePercentage(testConfidence * 100);

        Prediction storedPrediction = savePredictionAndReturn();

        assertThat("Stored prediction should have confidence as set in the view model",
                storedPrediction.getConfidences().get(0).getConfidence(),
                is(closeTo(testConfidence, 1e-8)));
    }

    @Test
    public void onSavePredictionSavesPredictionWithCreationDateNow() {
        Prediction storedPrediction = savePredictionAndReturn();

        assertThat("Stored prediction should have confidence with creation date now",
                storedPrediction.getConfidences().get(0).getCreationDate(), is(equalTo(now.toDate())));
    }

    @Test
    public void defaultDueDateIsFollowingDay() {
        // current time is defined as 2000-06-10 10:20:30 in setUp()
        LocalDate tomorrow = new LocalDate(2000, DateTimeConstants.JUNE, 11);
        assertThat("Due date defaults to the following day",
                viewModel.getLocalDueDate(), is(equalTo(tomorrow)));
    }

    @Test
    public void defaultDueTimeIsAtNoon() {
        LocalTime noon = new LocalTime(12, 0);
        assertThat("Due time defaults to 12:00",
                viewModel.getLocalDueTime(), is(equalTo(noon)));

    }

    @Test
    public void defaultConfidenceIs50Percent() {
        assertThat("Default confidence is 50 percent",
                viewModel.getConfidencePercentage(),  closeTo(50, 1e-8));
    }

    @Test
    public void confidenceAbove100IsSetTo100() {
        viewModel.setConfidencePercentage(120);
        assertThat("Confidence above 100 is reduced to 100",
                viewModel.getConfidencePercentage(), closeTo(100, 1e-8));
    }

    @Test
    public void confidenceBelow0IsSetTo0() {
        viewModel.setConfidencePercentage(-1.23);
        assertThat("Confidence below 0 is set to 0",
                viewModel.getConfidencePercentage(), closeTo(0, 1e-8));
    }

    @Test
    public void confidenceBetween0And100IsUnaffected() {
        double[] testValues = new double[] { 23.45, 12.34, 99.999, 0.0001, 0, 100 };
        for (double v : testValues) {
            viewModel.setConfidencePercentage(v);
            assertThat("Confidence between 0 and 100 is unaffected",
                    viewModel.getConfidencePercentage(), closeTo(v, 1e-8));
        }
    }
}