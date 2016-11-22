package de.chribi.predictable.newprediction;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.chribi.predictable.BaseUiTest;
import de.chribi.predictable.R;
import de.chribi.predictable.di.PredictableComponent;
import de.chribi.predictable.storage.InMemoryPredictionStorage;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.DefaultDateTimeHandler;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.chribi.testutils.DialogUtils.setDatePickerDialogAndConfirm;
import static de.chribi.testutils.DialogUtils.setTimePickerDialogAndConfirm;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.spy;

@RunWith(AndroidJUnit4.class)
public class NewPredictionActivityTest extends BaseUiTest {

    private NewPredictionViewModel viewModel;
    @Rule
    public ActivityTestRule<NewPredictionActivity> activityTestRule
            = new ActivityTestRule<>(NewPredictionActivity.class, false, false);

    @Override
    protected PredictableComponent createTestComponent() {
        return new PredictableComponent() {
            @Override
            public void inject(NewPredictionActivity activity) {
                activity.viewModel = viewModel;
            }
        };
    }

    @Before
    public void startActivity() {
        PredictionStorage predictionStorage = new InMemoryPredictionStorage();
        DateTimeProvider dateTimeProvider = new DefaultDateTimeHandler();
        viewModel = spy(new NewPredictionViewModel(predictionStorage, dateTimeProvider));
        activityTestRule.launchActivity(null);
    }

    @Test
    public void confidenceGreater100WillBeSetTo100() {
        onView(withId(R.id.text_confidence))
                .perform(typeText("123.45"))
                .check(matches(withText("100.0")));
    }

    @Test
    public void confidenceBetween0And100WillBeLeftUnchanged() {
        onView(withId(R.id.text_confidence))
                .perform(typeText("12.345"))
                .check(matches(withText("12.345")));
    }

    @Test
    public void dateViewShowsDateSelectedInDialog() {
        onView(withId(R.id.text_due_date))
                .perform(click());

        setDatePickerDialogAndConfirm(2000, 5, 10);

        onView(withId(R.id.text_due_date))
                .check(matches(withText("May 10, 2000")));
    }

    @Test
    public void timeViewShowsTimeSelectedInDialog() {
        onView(withId(R.id.text_due_time))
                .perform(click());

        setTimePickerDialogAndConfirm(14, 15);

        // test device has to be set to 24 hour format
        onView(withId(R.id.text_due_time))
                .check(matches(withText("14:15")));
    }
}