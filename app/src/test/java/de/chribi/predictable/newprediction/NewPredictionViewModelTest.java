package de.chribi.predictable.newprediction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import de.chribi.predictable.storage.InMemoryPredictionStorage;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;

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
        viewModel = new NewPredictionViewModel(mockedView, fakeStorage, mockedDateTimeProvider);
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
}