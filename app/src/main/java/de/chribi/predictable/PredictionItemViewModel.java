package de.chribi.predictable;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.util.ConfidenceFormatProvider;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;
import de.chribi.predictable.util.StringUtil;

public class PredictionItemViewModel extends BaseObservable {
    private PredictedEvent predictedEvent;
    private DateTimeProvider dateTimeProvider;
    private PredictionStatusStringProvider statusStrings;
    private final ConfidenceFormatProvider confidenceFormatter;
    private PredictionListView view;

    @Inject
    public PredictionItemViewModel(DateTimeProvider dateTimeProvider,
                                   PredictionStatusStringProvider statusStrings,
                                   @Named("short") ConfidenceFormatProvider confidenceFormatter) {
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
        return StringUtil.formatCurrentConfidence(predictedEvent.getPredictions(), confidenceFormatter);
    }

    /**
     * The state of the predicted event.
     */
    @Bindable
    public PredictionState getPredictionState() {
        return predictedEvent.getJudgement().getState();
    }

    /**
     * A short string describing the status of the prediction.
     */
    @Bindable
    public String getStatusDescription()
    {
        return StringUtil.formatStatus(predictedEvent.getJudgement(),
                predictedEvent.getDueDate(), statusStrings, dateTimeProvider);
    }

    public void showDetails() {
        view.showPredictedEventDetails(predictedEvent.getId());
    }
}
