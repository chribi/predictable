package de.chribi.predictable.predictiondetail;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.chribi.predictable.BR;
import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.ConfidenceFormatProvider;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;
import de.chribi.predictable.util.StringUtil;

public class PredictionDetailViewModel extends BaseObservable {
    private Prediction prediction;
    private double newConfidencePercentage = Double.NaN;
    private final PredictionStorage storage;
    private final PredictionStatusStringProvider statusStrings;
    private final ConfidenceFormatProvider confidenceFormatter;
    private final DateTimeProvider dateTimeProvider;
    private final PredictionDetailView view;

    @Inject
    public PredictionDetailViewModel(PredictionStorage storage,
                                     PredictionStatusStringProvider statusStrings,
                                     @Named("long") ConfidenceFormatProvider confidenceFormatter,
                                     DateTimeProvider dateTimeProvider,
                                     PredictionDetailView view) {
        this.storage = storage;
        this.statusStrings = statusStrings;
        this.confidenceFormatter = confidenceFormatter;
        this.dateTimeProvider = dateTimeProvider;
        this.view = view;
    }


    public void setPrediction(long predictionId) {
        prediction = storage.getPredictionById(predictionId);
        if(prediction == null) {
            view.onInvalidPrediction();
        } else {
            notifyChange();
        }
    }

    /**
     * Whether the view model holds valid data.  If false, all interactions with
     * this view model lead to an exception.
     */
    @Bindable
    public boolean isValid() {
        return prediction != null;
    }

    @Bindable
    @NonNull
    public String getTitle() {
        return prediction.getTitle();
    }

    @Bindable
    @Nullable
    public String getDescription() {
        return prediction.getDescription();
    }

    @Bindable
    @NonNull
    public String getStatus() {
        return StringUtil.formatStatus(prediction.getJudgement(), prediction.getDueDate(),
                statusStrings, dateTimeProvider);
    }

    @Bindable
    @NonNull
    public List<ConfidenceStatement> getConfidences() {
        return prediction.getConfidences();
    }

    @Bindable
    @NonNull
    public String getCurrentConfidence() {
        return StringUtil.formatCurrentConfidence(prediction.getConfidences(), confidenceFormatter);
    }

    @Bindable
    public boolean isOpen() {
        return prediction.getJudgement().getState() == PredictionState.Open;
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
        Judgement newJudgement = Judgement.create(state, dateTimeProvider.getCurrentDateTime());
        prediction = prediction.toBuilder().setJudgement(newJudgement).build();
        storage.updatePrediction(prediction.getId(), prediction);
        notifyChange();
    }

    public void reopen() {
        judge(PredictionState.Open);
    }

    public void updateConfidence() {
        double newConfidence = newConfidencePercentage / 100.0;
        ConfidenceStatement newConfidenceStatement = ConfidenceStatement.create(newConfidence,
                dateTimeProvider.getCurrentDateTime());
        prediction = prediction.toBuilder().addConfidence(newConfidenceStatement).build();
        storage.updatePrediction(prediction.getId(), prediction);
        newConfidencePercentage = Double.NaN;
        notifyChange();
    }

    public void deletePrediction() {
        storage.deletePrediction(prediction.getId());
        view.closeView();
    }
}
