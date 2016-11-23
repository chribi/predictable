package de.chribi.predictable.newprediction;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.BR;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;

public class NewPredictionViewModel extends BaseObservable {
    private String predictedEventTitle = "";
    private Date dueDate;
    private double confidencePercentage = 50.0;

    @Bindable
    public String getPredictedEventTitle() {
        return predictedEventTitle;
    }

    public void setPredictedEventTitle(String predictedEventTitle) {
        this.predictedEventTitle = predictedEventTitle;
        notifyPropertyChanged(BR.predictedEventTitle);
    }

    @Bindable
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        notifyPropertyChanged(BR.dueDate);
    }

    @Bindable
    public double getConfidencePercentage() {
        return confidencePercentage;
    }

    public void setConfidencePercentage(double confidencePercentage) {
        double newConfidencePercentage = confidencePercentage;
        if(confidencePercentage < 0) {
            newConfidencePercentage = 0;
        } else if (confidencePercentage > 100) {
            newConfidencePercentage = 100;
        }
        this.confidencePercentage = newConfidencePercentage;
        notifyPropertyChanged(BR.confidencePercentage);
    }

    private PredictionStorage storage;
    private NewPredictionView view;
    private DateTimeProvider dateTimeProvider;

    @Inject
    public NewPredictionViewModel(PredictionStorage storage, DateTimeProvider dateTimeProvider) {
        this.storage = storage;
        this.dateTimeProvider = dateTimeProvider;

        // Default due date is tomorrow noon
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dateTimeProvider.getCurrentDateTime());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        setDueDate(cal.getTime());
    }

    public void setView(NewPredictionView view) {
        this.view = view;

    }

    public void onSavePrediction() {
        List<Prediction> predictions = new ArrayList<>(1);
        predictions.add(new Prediction(confidencePercentage / 100,
                dateTimeProvider.getCurrentDateTime()));
        storage.createPredictedEvent(predictedEventTitle, null, dueDate, predictions);
        view.closeView();
    }

    public void onCancel() {
        view.closeView();
    }
}
