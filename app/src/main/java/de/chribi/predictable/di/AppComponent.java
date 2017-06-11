package de.chribi.predictable.di;


import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        PredictionStorageModule.class,
        DateTimeModule.class,
        ConfigurationModule.class,
        PredictionSetModule.class,
        StringsModule.class,
        AndroidSupportInjectionModule.class,
        ActivitySubcomponentBuildersModule.class
})
@Singleton
interface AppComponent extends PredictableComponent {
}
