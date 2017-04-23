package de.chribi.predictable.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.util.DateTimeProvider;

/**
 * An in-memory storage of predictions.
 */
public class InMemoryPredictionStorage implements PredictionStorage {
    private @NonNull HashMap<Long, PredictedEvent> storage;
    private long nextId;

    /**
     * Create a new empty InMemoryPredictionStorage.
     */
    public InMemoryPredictionStorage() {
        storage = new HashMap<>();
        nextId = 0;
    }

    /**
     * Create a new InMemoryPredictionStorage containing some predictions.
     *
     * @param initialPredictions  Some initial predictions.
     */
    public InMemoryPredictionStorage(List<PredictedEvent> initialPredictions) {
        this();
        for (PredictedEvent prediction : initialPredictions) {
            storage.put(prediction.getId(), prediction);
        }
    }

    @NonNull
    @Override
    public List<PredictedEvent> getPredictedEvents() {
        return new ArrayList<>(storage.values());
    }

    @Nullable
    @Override
    public PredictedEvent getPredictedEventById(long id) {
        return storage.get(id);
    }

    @NonNull
    @Override
    public PredictedEvent createPredictedEvent(@NonNull String title, @Nullable String description,
                                               @NonNull Date dueDate,
                                               @NonNull List<Prediction> predictions) {
        PredictedEvent newPredictedEvent = new PredictedEvent(nextId, title, description,
               null, dueDate, predictions);
        storage.put(nextId, newPredictedEvent);
        nextId++;
        return newPredictedEvent;
    }

    @NonNull
    @Override
    public PredictedEvent.Editor edit(@NonNull PredictedEvent event) {
        return new InMemoryEventEditor(event, storage);
    }

    @Override
    public void deletePredictedEvent(long id) {
        storage.remove(id);
    }

    private static class InMemoryEventEditor implements PredictedEvent.Editor {
        private HashMap<Long, PredictedEvent> storage;
        private long id;
        private String newTitle;
        private String newDescription;
        private Judgement newJudgement;
        private Date newDueDate;
        private List<Prediction> currentPredictions;

        public InMemoryEventEditor(PredictedEvent event, HashMap<Long, PredictedEvent> storage) {
            this.storage = storage;
            this.id = event.getId();
            newTitle = event.getTitle();
            newDescription = event.getDescription();
            newJudgement = event.getJudgement();
            newDueDate = event.getDueDate();
            currentPredictions = event.getPredictions();
        }

        @Override
        public PredictedEvent commit() {
            PredictedEvent newEvent = new PredictedEvent(id, newTitle, newDescription, newJudgement,
                    newDueDate, getPredictions());
            storage.put(id, newEvent);
            return newEvent;
        }

        @Override
        public void setTile(@NonNull String newTitle) {
            this.newTitle = newTitle;
        }

        @NonNull
        @Override
        public String getTitle() {
            return newTitle;
        }

        @Override
        public void setDescription(@Nullable String newDescription) {
            this.newDescription = newDescription;
        }

        @Nullable
        @Override
        public String getDescription() {
            return newDescription;
        }

        @Override
        public void setJudgement(@Nullable Judgement newJudgment) {
            this.newJudgement = newJudgment;
        }

        @Nullable
        @Override
        public Judgement getJudgement() {
            return newJudgement;
        }

        @Override
        public void setDueDate(@NonNull Date newDueDate) {
            this.newDueDate = newDueDate;
        }

        @NonNull
        @Override
        public Date getDueDate() {
            return newDueDate;
        }

        @Override
        public void addPrediction(@NonNull Prediction newPrediction) {
            currentPredictions.add(newPrediction);
        }

        @Override
        public void removePrediction(Prediction prediction) {
            currentPredictions.remove(prediction);
        }

        @NonNull
        @Override
        public List<Prediction> getPredictions() {
            Collections.sort(currentPredictions, new Comparator<Prediction>() {
                @Override
                public int compare(Prediction o1, Prediction o2) {
                    return o1.getCreationDate().compareTo(o2.getCreationDate());
                }
            });
            return new ArrayList<>(currentPredictions);
        }
    }
}
