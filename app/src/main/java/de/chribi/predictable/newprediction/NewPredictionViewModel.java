package de.chribi.predictable.newprediction;

import android.databinding.Observable;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

        // Default due date is tomorrow noon
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dateTimeProvider.getCurrentDateTime());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        dueDate.set(cal.getTime());

        confidencePercentage.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                double newConfidence = confidencePercentage.get();
                if(newConfidence < 0) {
                    confidencePercentage.set(0);
                } else if (newConfidence > 100) {
                    confidencePercentage.set(100);
                }
            }
        });
    }

    public void setView(NewPredictionView view) {
        this.view = view;

    }

    public void onSavePrediction() {
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
