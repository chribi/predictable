package de.chribi.predictable.newprediction;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.BR;
import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;

public class NewPredictionViewModel extends BaseObservable {
    private final PredictionStorage storage;
    private final NewPredictionView view;
    private final DateTimeProvider dateTimeProvider;

    private String predictionTitle = "";
    private LocalDate localDueDate;
    private LocalTime localDueTime;
    private double confidencePercentage = 50.0;

    @Inject
    public NewPredictionViewModel(PredictionStorage storage, DateTimeProvider dateTimeProvider,
                                  NewPredictionView view) {
        this.storage = storage;
        this.dateTimeProvider = dateTimeProvider;
        this.view = view;

        // Default due date/time is tomorrow noon
        Date now = dateTimeProvider.getCurrentDateTime();
        DateTimeZone timeZone = dateTimeProvider.getCurrentTimeZone();

        this.localDueDate = new LocalDate(now, timeZone).plusDays(1);
        this.localDueTime = new LocalTime(12, 0);
    }

    @Bindable
    public String getPredictionTitle() {
        return predictionTitle;
    }

    public void setPredictionTitle(String predictionTitle) {
        this.predictionTitle = predictionTitle;
        notifyPropertyChanged(BR.predictionTitle);
    }

    @Bindable
    public LocalDate getLocalDueDate() {
        return localDueDate;
    }

    public void setLocalDueDate(LocalDate localDueDate) {
        if(!this.localDueDate.equals(localDueDate)) {
            this.localDueDate = localDueDate;
            notifyPropertyChanged(BR.localDueDate);
        }
    }

    @Bindable
    public LocalTime getLocalDueTime() {
        return localDueTime;
    }

    public void setLocalDueTime(LocalTime localDueTime) {
        if(!this.localDueTime.equals(localDueTime)) {
            this.localDueTime = localDueTime;
            notifyPropertyChanged(BR.localDueTime);
        }
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

    public Date getDueDateTime() {
        DateTime dueDateTime = localDueDate.toDateTime(localDueTime,
                dateTimeProvider.getCurrentTimeZone());
        return dueDateTime.toDate();
    }

    public void setDueDateTime(Date dueDate) {
        LocalDateTime localDateTime = new LocalDateTime(dueDate,
                dateTimeProvider.getCurrentTimeZone());
        localDueDate = localDateTime.toLocalDate();
        localDueTime = localDateTime.toLocalTime();
    }

    public void onSavePrediction() {
        List<ConfidenceStatement> confidenceStatements = new ArrayList<>(1);
        if(!Double.isNaN(confidencePercentage)) {
            confidenceStatements.add(ConfidenceStatement.create(confidencePercentage / 100,
                    dateTimeProvider.getCurrentDateTime()));
        }
        storage.createPrediction(predictionTitle, null, getDueDateTime(), confidenceStatements);
        view.closeView();
    }

    public void onCancel() {
        view.closeView();
    }
}
