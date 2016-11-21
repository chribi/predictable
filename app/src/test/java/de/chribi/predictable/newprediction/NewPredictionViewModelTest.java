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

import de.chribi.predictable.storage.InMemoryPredictionStorage;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class NewPredictionViewModelTest {
    private NewPredictionViewModel viewModel;
    @Mock private NewPredictionView mockedView;
    @Mock private DateTimeProvider mockedDateTimeProvider;
    private PredictionStorage fakeStorage;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        fakeStorage = new InMemoryPredictionStorage();
        // Current time is 2000-06-10 10:20:30
        Calendar now = new GregorianCalendar(2000, Calendar.JUNE, 10, 10, 20, 30);
        when(mockedDateTimeProvider.getCurrentDateTime())
                .thenReturn(now.getTime());
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
    public void confidenceIsClampedBetween0And100() {
        viewModel.confidencePercentage.set(120);
        assertThat("Confidence above 100 is reduced to 100",
                viewModel.confidencePercentage.get(), closeTo(100, 1e-8));

        viewModel.confidencePercentage.set(-1.23);
        assertThat("Confidence below 0 is set to 0",
                viewModel.confidencePercentage.get(), closeTo(0, 1e-8));

        viewModel.confidencePercentage.set(23.45);
        assertThat("Confidence between 0 and 100 is unaffected",
                viewModel.confidencePercentage.get(), closeTo(23.45, 1e-8));
    }


}