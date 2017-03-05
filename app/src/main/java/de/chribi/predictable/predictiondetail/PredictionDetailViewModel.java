package de.chribi.predictable.predictiondetail;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.storage.PredictionStorage;

public class PredictionDetailViewModel extends BaseObservable {
    private PredictedEvent.Editor event;
    private PredictionStorage storage;

    @Inject
    public PredictionDetailViewModel(PredictionStorage storage) {
        this.storage = storage;
    }

    public void setPredictedEvent(long eventId) {
        PredictedEvent ev = storage.getPredictedEventById(eventId);
        if(ev != null) {
            this.event = storage.edit(ev);
            notifyChange();
        } else {
            // TODO do some proper error handling when getting an invalid id
        }
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
        return ""; // TODO
    }
}
