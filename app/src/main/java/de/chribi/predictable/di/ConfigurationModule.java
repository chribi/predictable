package de.chribi.predictable.di;


import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ConfigurationModule {
    private Context context;
    private String dbName;

    public ConfigurationModule(Context context, String dbName) {
        this.context = context;
        this.dbName = dbName;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    @Named("DatabaseName")
    String provideDatabaseName() {
        return dbName;
    }
}
