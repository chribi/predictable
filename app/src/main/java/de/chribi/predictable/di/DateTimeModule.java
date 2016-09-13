package de.chribi.predictable.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.DefaultDateTimeHandler;

@Module
public class DateTimeModule {
    @Provides
    @Singleton
    DateTimeProvider provideDateTimeProvider() {
        return new DefaultDateTimeHandler();
    }
}
