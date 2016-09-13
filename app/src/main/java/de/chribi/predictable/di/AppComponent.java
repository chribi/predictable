package de.chribi.predictable.di;

import javax.inject.Singleton;

import dagger.Component;
import de.chribi.predictable.newprediction.NewPredictionActivity;

@Component(modules = {
        PredictionStorageModule.class,
        DateTimeModule.class
})
@Singleton
public interface AppComponent {
    void inject(NewPredictionActivity activity);
}
