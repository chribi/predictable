package de.chribi.predictable;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Date;

import javax.inject.Inject;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.util.ConfidenceFormatProvider;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;
import de.chribi.predictable.util.StatusStringUtil;

public class PredictionItemViewModel extends BaseObservable {

    private PredictedEvent predictedEvent;
    private DateTimeProvider dateTimeProvider;
    private PredictionStatusStringProvider statusStrings;
    private final ConfidenceFormatProvider confidenceFormatter;
    private PredictionListView view;

    @Inject
    public PredictionItemViewModel(DateTimeProvider dateTimeProvider,
                                   PredictionStatusStringProvider statusStrings,
                                   ConfidenceFormatProvider confidenceFormatter) {
        this.dateTimeProvider = dateTimeProvider;
        this.statusStrings = statusStrings;
        this.confidenceFormatter = confidenceFormatter;
    }

    /**
     * Set the underlying predicted event for this PredictionItemViewModel.
     */
    public void setPredictedEvent(@NonNull PredictedEvent predictedEvent) {
        this.predictedEvent = predictedEvent;
        notifyChange();
    }

    public void setView(PredictionListView view) {
        this.view = view;
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
            return confidenceFormatter.formatConfidence(lastPrediction.getConfidence() * 100);
        } else {
            return confidenceFormatter.formatNoConfidence();
        }
    }

    /**
     * The state of the predicted event.
     */
    @Bindable
    public PredictionState getPredictionState() {
        Judgement judgement = predictedEvent.getJudgement();
        if(judgement != null) {
            return judgement.getState();
        } else {
            return PredictionState.Open;
        }
    }

    /**
     * A short string describing the status of the prediction.
     */
    @Bindable
    public String getStatusDescription()
    {
        return StatusStringUtil.formatStatus(predictedEvent.getJudgement(),
                predictedEvent.getDueDate(), statusStrings, dateTimeProvider);
    }

    public void showDetails() {
        view.showPredictedEventDetails(predictedEvent.getId());
    }
}
