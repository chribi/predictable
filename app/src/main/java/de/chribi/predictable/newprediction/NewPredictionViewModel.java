package de.chribi.predictable.newprediction;

import android.databinding.ObservableDouble;
import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;

public class NewPredictionViewModel {
    public final ObservableField<String> predictedEvent = new ObservableField<>();
    public final ObservableField<Date> dueDate = new ObservableField<>();
    public final ObservableDouble confidencePercentage = new ObservableDouble(50);

    private PredictionStorage storage;
    private NewPredictionView view;
    private DateTimeProvider dateTimeProvider;

    @Inject
    public NewPredictionViewModel(PredictionStorage storage, DateTimeProvider dateTimeProvider) {
        this.storage = storage;
        this.dateTimeProvider = dateTimeProvider;

        predictedEvent.set("");
        dueDate.set(dateTimeProvider.getCurrentDateTime()); // TODO set to something more meaningful
    }

    public void setView(NewPredictionView view) {
        this.view = view;
    }

    public void onSavePrediction() {
        // TODO need to ensure confidencePercentage is in [0, 100]
        List<Prediction> predictions = new ArrayList<>(1);
        predictions.add(new Prediction(confidencePercentage.get() / 100,
                dateTimeProvider.getCurrentDateTime()));
        storage.createPredictedEvent(predictedEvent.get(), null, dueDate.get(), predictions);
        view.closeView();
    }

    public void onCancel() {
        view.closeView();
    }
}
