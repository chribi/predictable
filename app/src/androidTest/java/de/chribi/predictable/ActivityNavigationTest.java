package de.chribi.predictable;


import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.predictiondetail.PredictionDetailActivity;
import de.chribi.predictable.predictionlist.PredictionListActivity;
import de.chribi.predictable.startscreen.StartScreenActivity;
import de.chribi.testutils.EspressoUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ActivityNavigationTest extends BaseUiTest {

    public ActivityNavigationTest() {
        List<Prediction> predictionList = new ArrayList<>();
        for (long id = 0; id < 60; id++) {
            predictionList.add(testPrediction(id));
        }
        Prediction[] predictionArray = new Prediction[predictionList.size()];
        predictionList.toArray(predictionArray);
        setupSqliteDb(predictionArray);
    }

    private static Prediction testPrediction(long index) {
        long futureDate = 6000000000L; // time stamp that should be in the future (around 2160)
        Prediction.Builder builder = Prediction.builder()
                .setId(index)
                .setTitle("Prediction " + String.valueOf(index))
                .setConfidences(ConfidenceStatement.create(index * 0.01, new Date(100)));
        if (index % 3 == 0) {
            // create overdue predction
            // dates around 1970 => should be in the past
            builder.setDueDate(new Date(index * 10000L));
        } else if (index % 3 == 1) {
            // create predictions that is due in the future
            builder.setDueDate(new Date(futureDate + index * 10000L));
        } else { // if (id % 3 == 2)
            // create prediction that is already judged
            builder.setDueDate(new Date(index * 10000L + (index % 2) * futureDate))
                    .setJudgement(Judgement.create(PredictionState.Correct,
                            new Date(index * 20000L)));
        }
        return builder.build();
    }

    @Rule
    public ActivityTestRule<StartScreenActivity> activityTestRule
            = new ActivityTestRule<>(StartScreenActivity.class);

    @Test
    public void fabNewPrediction_navigatesToNewPredictionActivity() {
        onView(withId(R.id.fab_new_prediction))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(NewPredictionActivity.class)));
    }

    @Test
    public void saveAction_inNewPredictionActivity_navigatesToMainActivity() {
        onView(withId(R.id.fab_new_prediction))
                .perform(click());
        onView(withId(R.id.action_save_prediction))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(StartScreenActivity.class)));
    }

    @Test
    public void homeButton_inNewPredictionActivity_navigatesToMainActivity() {
        onView(withId(R.id.fab_new_prediction))
                .perform(click());
        onView(withContentDescription(R.string.abc_action_bar_up_description))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(StartScreenActivity.class)));
    }

    @Test
    public void clickOnPrediction_opensPredictionDetailsForThatPrediction() {
        Prediction somePrediction = testPrediction(0);
        onView(both(withId(R.id.text_prediction_title)).and(withText(somePrediction.getTitle())))
                .perform(click());

        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(PredictionDetailActivity.class)));
        onView(withId(R.id.text_prediction_title))
                .check(matches(withText(somePrediction.getTitle())));
    }

    @Test
    public void homeButton_inPredictionDetailActivity_navigatesBackToStartScreenActivity() {
        final Prediction somePrediction = testPrediction(0);
        onView(both(withId(R.id.text_prediction_title)).and(withText(somePrediction.getTitle())))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(PredictionDetailActivity.class)));

        onView(withContentDescription(R.string.abc_action_bar_up_description))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(StartScreenActivity.class)));
    }

    @Test
    public void clickOnFooter_forOverduePredictions_showsAllOverduePredictions() {
        openOverduePredictions();

        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(PredictionListActivity.class)));
        onView(withId(R.id.included_toolbar))
                .check(matches(hasDescendant(withText(R.string.title_overdue_predictions))));
    }

    @Test
    public void homeButton_inPredictionDetailActivity_navigatesBackToPredictionListActivity() {
        openOverduePredictions();

        final Prediction somePrediction = testPrediction(0);
        onView(both(withId(R.id.text_prediction_title)).and(withText(somePrediction.getTitle())))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(PredictionDetailActivity.class)));

        onView(withContentDescription(R.string.abc_action_bar_up_description))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(PredictionListActivity.class)));
        onView(withId(R.id.included_toolbar))
                .check(matches(hasDescendant(withText(R.string.title_overdue_predictions))));
    }

    private static void openOverduePredictions() {
        onView(withId(R.id.list_predictions))
                .perform(RecyclerViewActions.scrollToPosition(12));
        onView(withId(R.id.button_show_more_footer))
                .perform(click());
    }
}

