package de.chribi.predictable.di;

import de.chribi.predictable.MainActivity;
import de.chribi.predictable.PredictableApp;
import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.predictiondetail.PredictionDetailActivity;
import de.chribi.predictable.storage.PredictionStorage;

public interface PredictableComponent {
    void inject(PredictionDetailActivity activity);
    void inject(MainActivity activity);
    void inject(PredictableApp app);
    PredictionStorage getStorage();
}
