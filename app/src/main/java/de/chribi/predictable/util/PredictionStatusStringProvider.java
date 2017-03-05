package de.chribi.predictable.util;

import org.joda.time.LocalDateTime;

import de.chribi.predictable.data.PredictionState;

public interface PredictionStatusStringProvider {
    String formatOverDue();
    String formatKnownBy(LocalDateTime dueDateTime);
    String formatJudged(PredictionState judgedState, LocalDateTime judgedDate);
}
