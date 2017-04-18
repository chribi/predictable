package de.chribi.predictable.util;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

public class StringUtil {
    private StringUtil() {
    }

    public static @NonNull String formatStatus(@Nullable Judgement judgement,
                        @NonNull Date dueDate, @NonNull PredictionStatusStringProvider stringProvider,
                        @NonNull DateTimeProvider dateTimeProvider) {
        DateTimeZone timezone = dateTimeProvider.getCurrentTimeZone();
        if (judgement == null || judgement.getState() == PredictionState.Open) {
            if (dueDate.after(dateTimeProvider.getCurrentDateTime())) {
                return stringProvider.formatKnownBy(new LocalDateTime(dueDate, timezone));
            } else {
                return stringProvider.formatOverDue();
            }
        } else {
            LocalDateTime judgedDate = new LocalDateTime(judgement.getDate(), timezone);
            return stringProvider.formatJudged(judgement.getState(), judgedDate);
        }
    }

    public static @NonNull String formatCurrentConfidence(@NonNull List<Prediction> predictions,
                                   @NonNull ConfidenceFormatProvider confidenceFormatter) {
        int numOfPredictions = predictions.size();
        if(numOfPredictions > 0) {
            Prediction lastPrediction = predictions.get(numOfPredictions - 1);
            return confidenceFormatter.formatConfidence(lastPrediction.getConfidence() * 100);
        } else {
            return confidenceFormatter.formatNoConfidence();
        }
    }
}
