package de.chribi.predictable;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.util.ConfidenceFormatProvider;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;
import de.chribi.predictable.util.StringUtil;

public class PredictionItemViewModel extends BaseObservable {
    public static class Factory {
        private DateTimeProvider dateTimeProvider;
        private PredictionStatusStringProvider statusStringProvider;
        private ConfidenceFormatProvider confidenceFormatProvider;

        @Inject
        public Factory(DateTimeProvider dateTimeProvider,
                       PredictionStatusStringProvider statusStrings,
                       @Named("short") ConfidenceFormatProvider confidenceFormatter) {
            this.dateTimeProvider = dateTimeProvider;
            this.statusStringProvider = statusStrings;
            this.confidenceFormatProvider = confidenceFormatter;
        }

        public PredictionItemViewModel create(Prediction prediction, PredictionItemView view) {
            return new PredictionItemViewModel(prediction, view, dateTimeProvider,
                    statusStringProvider, confidenceFormatProvider);
        }

        public List<PredictionItemViewModel> createMany(List<Prediction> predictions,
                                                        PredictionItemView view) {
            List<PredictionItemViewModel> result = new ArrayList<>(predictions.size());
            for (Prediction prediction : predictions) {
                result.add(create(prediction, view));
            }
            return result;
        }
    }

    private Prediction prediction;
    private DateTimeProvider dateTimeProvider;
    private PredictionStatusStringProvider statusStrings;
    private final ConfidenceFormatProvider confidenceFormatter;
    private PredictionItemView view;

    private PredictionItemViewModel(Prediction prediction, PredictionItemView view,
                                    DateTimeProvider dateTimeProvider,
                                    PredictionStatusStringProvider statusStrings,
                                    ConfidenceFormatProvider confidenceFormatter) {
        this.prediction = prediction;
        this.view = view;
        this.dateTimeProvider = dateTimeProvider;
        this.statusStrings = statusStrings;
        this.confidenceFormatter = confidenceFormatter;
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
    public String getStatusDescription() {
        return StringUtil.formatStatus(prediction.getJudgement(),
                prediction.getDueDate(), statusStrings, dateTimeProvider);
    }

    public void showDetails() {
        view.showPredictionDetails(prediction.getId());
    }
}
