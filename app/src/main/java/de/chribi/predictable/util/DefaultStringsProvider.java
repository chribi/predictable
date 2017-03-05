package de.chribi.predictable.util;


import android.content.Context;
import android.support.annotation.StringRes;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import javax.inject.Inject;

import de.chribi.predictable.R;
import de.chribi.predictable.data.PredictionState;

public class DefaultStringsProvider
        implements PredictionStatusStringProvider, ConfidenceFormatProvider {
    private Context context;
    @Inject
    public DefaultStringsProvider(Context context) {
        this.context = context;
    }

    @Override
    public String formatNoConfidence() {
        return context.getString(R.string.text_no_confidence);
    }

    @Override
    public String formatConfidence(double confidencePercent) {
        return String.format(Locale.getDefault(), "%.1f", confidencePercent);
    }

    @Override
    public String formatOverDue() {
        return context.getString(R.string.status_overdue);
    }

    @Override
    public String formatKnownBy(LocalDateTime dueDateTime) {
        DateTimeFormatter dateFormat = DateTimeFormat.mediumDate();
        String dateString = dueDateTime.toString(dateFormat);
        return context.getString(R.string.status_known_by, dateString);
    }

    @Override
    public String formatJudged(PredictionState judgedState, LocalDateTime judgedTime) {
        DateTimeFormatter dateFormat = DateTimeFormat.mediumDate();
        String dateString = judgedTime.toString(dateFormat);
        @StringRes int stateStringId = 0;
        switch (judgedState) {
            case Open:
                stateStringId = R.string.string_state_open;
                break;
            case Correct:
                stateStringId = R.string.string_state_correct;
                break;
            case Incorrect:
                stateStringId = R.string.string_state_incorrect;
                break;
            case Invalid:
                stateStringId = R.string.string_state_invalid;
                break;
        }
        String stateString = context.getString(stateStringId);
        return context.getString(R.string.status_judged, stateString, dateString);
    }
}
