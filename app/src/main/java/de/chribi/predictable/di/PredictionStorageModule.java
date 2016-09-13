package de.chribi.predictable.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.chribi.predictable.storage.InMemoryPredictionStorage;
import de.chribi.predictable.storage.PredictionStorage;

@Module
public class PredictionStorageModule {
    @Provides
    @Singleton
    PredictionStorage providePredictionStorage() {
        return new InMemoryPredictionStorage();
    }
}
