package de.chribi.predictable.di;

import de.chribi.predictable.PredictableApp;
import de.chribi.predictable.storage.PredictionStorage;

public interface PredictableComponent {
    void inject(PredictableApp app);
    PredictionStorage getStorage();
}
