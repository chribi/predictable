package de.chribi.predictable.di;

import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.storage.PredictionStorage;

public interface PredictableComponent {
    void inject(NewPredictionActivity activity);
    PredictionStorage getStorage();
}
