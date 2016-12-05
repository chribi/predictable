package de.chribi.predictable.di;


import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        PredictionStorageModule.class,
        DateTimeModule.class,
        ConfigurationModule.class
})
@Singleton
interface AppComponent extends PredictableComponent {
}
