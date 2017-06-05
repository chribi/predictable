package de.chribi.predictable.di;

import de.chribi.predictable.MainActivity;
import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.predictiondetail.PredictionDetailActivity;
import de.chribi.predictable.storage.PredictionStorage;

public interface PredictableComponent {
    void inject(NewPredictionActivity activity);
    void inject(PredictionDetailActivity activity);
    void inject(MainActivity activity);
    PredictionStorage getStorage();
}
