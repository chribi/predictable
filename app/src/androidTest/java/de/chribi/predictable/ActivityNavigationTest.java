package de.chribi.predictable;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.testutils.EspressoUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ActivityNavigationTest extends BaseUiTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void fabNewPredictionNavigatesToNewPredictionActivity()
    {
        onView(withId(R.id.fab_new_prediction))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(NewPredictionActivity.class)));
    }

    @Test
    public void saveActionInNewPredictionActivityNavigatesToMainActivity()
    {
        onView(withId(R.id.fab_new_prediction))
                .perform(click());
        onView(withId(R.id.action_save_prediction))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(MainActivity.class)));
    }

    @Test
    public void homeButtonInNewPredictionActivityNavigatesToMainActivity()
    {
        onView(withId(R.id.fab_new_prediction))
                .perform(click());
        onView(withContentDescription(R.string.abc_action_bar_up_description))
                .perform(click());
        assertThat(EspressoUtils.getCurrentActivity(), is(instanceOf(MainActivity.class)));
    }
}
