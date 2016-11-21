package de.chribi.predictable.newprediction;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.chribi.predictable.R;

@RunWith(AndroidJUnit4.class)
public class NewPredictionActivityTest {

    @Rule
    public ActivityTestRule<NewPredictionActivity> activityTestRule
            = new ActivityTestRule<>(NewPredictionActivity.class);

    @Test
    public void confidenceGreater100WillBeSetTo100()
    {
        onView(withId(R.id.text_confidence))
                .perform(typeText("123.45"))
                .check(matches(withText("100.0")));
    }

    @Test
    public void confidenceBetween0And100WillBeLeftUnchanged()
    {
        onView(withId(R.id.text_confidence))
                .perform(typeText("12.345"))
                .check(matches(withText("12.345")));
    }
}