package de.chribi.predictable;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.util.ConfidenceFormatProvider;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;
import de.chribi.predictable.util.StringUtil;

public class PredictionItemViewModel extends BaseObservable {
    private Prediction prediction;
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
     * Set the underlying prediction for this PredictionItemViewModel.
     */
    public void setPrediction(@NonNull Prediction prediction) {
        this.prediction = prediction;
        notifyChange();
    }

    public void setView(PredictionListView view) {
        this.view = view;
    }

    /**
     * The title to show for this prediction.
     */
    @Bindable
    public String getPredictionTitle() {
        return prediction.getTitle();
    }

    /**
     * The confidence that is shown for this item.
     */
    @Bindable
    public String getConfidence() {
        return StringUtil.formatCurrentConfidence(prediction.getConfidences(), confidenceFormatter);
    }

    /**
     * The state of the prediction.
     */
    @Bindable
    public PredictionState getPredictionState() {
        return prediction.getJudgement().getState();
    }

    /**
     * A short string describing the status of the prediction.
     */
    @Bindable
    public String getStatusDescription()
    {
        return StringUtil.formatStatus(prediction.getJudgement(),
                prediction.getDueDate(), statusStrings, dateTimeProvider);
    }

    public void showDetails() {
        view.showPredictionDetails(prediction.getId());
    }
}
