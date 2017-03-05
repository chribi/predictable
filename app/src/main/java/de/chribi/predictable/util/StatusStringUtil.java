package de.chribi.predictable.util;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Date;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.PredictionState;

public class StatusStringUtil {
   private StatusStringUtil() {}

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
}
