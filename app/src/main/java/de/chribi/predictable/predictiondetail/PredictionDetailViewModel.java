package de.chribi.predictable.predictiondetail;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;
import de.chribi.predictable.util.StatusStringUtil;

public class PredictionDetailViewModel extends BaseObservable {
    private PredictedEvent event;
    private final PredictionStorage storage;
    private final PredictionStatusStringProvider statusStrings;
    private final DateTimeProvider dateTimeProvider;

    @Inject
    public PredictionDetailViewModel(PredictionStorage storage,
                                     PredictionStatusStringProvider statusStrings,
                                     DateTimeProvider dateTimeProvider) {
        this.storage = storage;
        this.statusStrings = statusStrings;
        this.dateTimeProvider = dateTimeProvider;
    }

    public void setPredictedEvent(long eventId) {
        event = storage.getPredictedEventById(eventId);
        notifyChange();
        // TODO handle invalid eventId (i.e. event == null)
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
        return StatusStringUtil.formatStatus(event.getJudgement(), event.getDueDate(),
                statusStrings, dateTimeProvider);
    }

    @Bindable
    public boolean isOpen() {
        return event.getJudgement() == null || event.getJudgement().getState() == PredictionState.Open;
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
}
