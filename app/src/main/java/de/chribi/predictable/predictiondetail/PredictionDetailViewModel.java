package de.chribi.predictable.predictiondetail;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.chribi.predictable.BR;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.ConfidenceFormatProvider;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;
import de.chribi.predictable.util.StringUtil;

public class PredictionDetailViewModel extends BaseObservable {
    private PredictedEvent event;
    private double newConfidencePercentage = Double.NaN;
    private final PredictionStorage storage;
    private final PredictionStatusStringProvider statusStrings;
    private final ConfidenceFormatProvider confidenceFormatter;
    private final DateTimeProvider dateTimeProvider;
    private PredictionDetailView view;

    @Inject
    public PredictionDetailViewModel(PredictionStorage storage,
                                     PredictionStatusStringProvider statusStrings,
                                     @Named("long") ConfidenceFormatProvider confidenceFormatter,
                                     DateTimeProvider dateTimeProvider) {
        this.storage = storage;
        this.statusStrings = statusStrings;
        this.confidenceFormatter = confidenceFormatter;
        this.dateTimeProvider = dateTimeProvider;
    }


    public void setPredictedEvent(long eventId) {
        event = storage.getPredictedEventById(eventId);
        if(event == null) {
            view.onInvalidPrediction();
        } else {
            notifyChange();
        }
    }

    public void setView(PredictionDetailView view) {
        this.view = view;
    }

    /**
     * Whether the view model holds valid data.  If false, all interactions with
     * this view model lead to an exception.
     */
    @Bindable
    public boolean isValid() {
        return event != null;
    }

    @Bindable
    @NonNull
    public String getTitle() {
        return event.getTitle();
    }

    @Bindable
    @Nullable
    public String getDescription() {
        return event.getDescription();
    }

    @Bindable
    @NonNull
    public String getStatus() {
        return StringUtil.formatStatus(event.getJudgement(), event.getDueDate(),
                statusStrings, dateTimeProvider);
    }

    @Bindable
    @NonNull
    public List<Prediction> getPredictions() {
        return event.getPredictions();
    }

    @Bindable
    @NonNull
    public String getCurrentConfidence() {
        return StringUtil.formatCurrentConfidence(event.getPredictions(), confidenceFormatter);
    }

    @Bindable
    public boolean isOpen() {
        return event.getJudgement() == null || event.getJudgement().getState() == PredictionState.Open;
    }

    @Bindable
    public boolean getCanUpdateConfidence() {
        return !Double.isNaN(newConfidencePercentage);
    }

    @Bindable
    public double getNewConfidencePercentage() {
        return newConfidencePercentage;
    }

    public void setNewConfidencePercentage(double percentage) {
        if(percentage < 0) {
            newConfidencePercentage = 0;
        } else if (percentage > 100) {
            newConfidencePercentage = 100;
        } else {
            newConfidencePercentage = percentage;
        }

        notifyPropertyChanged(BR.newConfidencePercentage);
        notifyPropertyChanged(BR.canUpdateConfidence);
    }

    public void judgeCorrect() {
        judge(PredictionState.Correct);
    }

    public void judgeIncorrect() {
        judge(PredictionState.Incorrect);
    }

    public void judgeInvalid() {
        judge(PredictionState.Invalid);
    }

    private void judge(PredictionState state) {
        PredictedEvent.Editor editor = storage.edit(event);
        editor.setJudgement(new Judgement(state, dateTimeProvider.getCurrentDateTime()));
        event = editor.commit();
        notifyChange();
    }

    public void reopen() {
        PredictedEvent.Editor editor = storage.edit(event);
        editor.setJudgement(null);
        event = editor.commit();
        notifyChange();
    }

    public void updateConfidence() {
        double newConfidence = newConfidencePercentage / 100.0;
        Prediction newPrediction = new Prediction(newConfidence,
                dateTimeProvider.getCurrentDateTime());
        PredictedEvent.Editor editor = storage.edit(event);
        editor.addPrediction(newPrediction);
        event = editor.commit();
        notifyChange();
    }

    public void deletePrediction() {
        storage.deletePredictedEvent(event.getId());
        view.closeView();
    }
}
