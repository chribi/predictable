package de.chribi.predictable.util;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.ConfidenceStatement;
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

    public static @NonNull String formatCurrentConfidence(@NonNull List<ConfidenceStatement> confidenceStatements,
                                   @NonNull ConfidenceFormatProvider confidenceFormatter) {
        int numOfConfidences = confidenceStatements.size();
        if(numOfConfidences > 0) {
            ConfidenceStatement lastConfidenceStatement = confidenceStatements.get(numOfConfidences - 1);
            return confidenceFormatter.formatConfidence(lastConfidenceStatement.getConfidence() * 100);
        } else {
            return confidenceFormatter.formatNoConfidence();
        }
    }
}
