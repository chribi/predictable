package de.chribi.predictable;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Date;

import javax.inject.Inject;

import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.util.DateTimeProvider;

public class PredictionItemViewModel extends BaseObservable {
    public interface PredictionItemStringProvider {
        String formatNoConfidence();
        String formatConfidence(double confidencePercent);
        String formatOverDue();
        String formatKnownBy(LocalDateTime dueDateTime);
        String formatJudged(PredictionState judgedState);
    }

    private PredictedEvent predictedEvent;
    private DateTimeProvider dateTimeProvider;
    private PredictionItemStringProvider strings;

    @Inject
    public PredictionItemViewModel(DateTimeProvider dateTimeProvider,
                                   PredictionItemStringProvider strings) {
        this.dateTimeProvider = dateTimeProvider;
        this.strings = strings;
    }

    /**
     * Set the underlying predicted event for this PredictionItemViewModel.
     */
    public void setPredictedEvent(@NonNull PredictedEvent predictedEvent) {
        this.predictedEvent = predictedEvent;
        notifyChange();
    }

    /**
     * The event title to show for this event.
     */
    @Bindable
    public String getEventTitle() {
        return predictedEvent.getTitle();
    }

    /**
     * The confidence that is shown for this item.
     */
    @Bindable
    public String getConfidence() {
        // always show the most recent prediction
        int numOfPredictions = predictedEvent.getPredictions().size();
        if(numOfPredictions > 0) {
            Prediction lastPrediction = predictedEvent.getPredictions().get(numOfPredictions - 1);
            return strings.formatConfidence(lastPrediction.getConfidence() * 100);
        } else {
            return strings.formatNoConfidence();
        }
    }

    /**
     * The state of the predicted event.
     */
    @Bindable
    public PredictionState getPredictionState() {
        return predictedEvent.getState();
    }

    /**
     * A short string describing the status of the prediction.
     */
    @Bindable
    public String getStatusDescription()
    {
        if(predictedEvent.getState() == PredictionState.Open) {
            Date dueDate = predictedEvent.getDueDate();
            if(dueDate.after(dateTimeProvider.getCurrentDateTime())) {
                return strings.formatOverDue();
            } else {
                return strings.formatKnownBy(new LocalDateTime(dueDate,
                        dateTimeProvider.getCurrentTimeZone()));
            }
        } else {
            return strings.formatJudged(predictedEvent.getState());
        }
    }

}
