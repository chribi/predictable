package de.chribi.predictable.di;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        PredictionStorageModule.class
})
@Singleton
public interface AppComponent {
}
