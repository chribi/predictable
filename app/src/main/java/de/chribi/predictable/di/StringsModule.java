package de.chribi.predictable.di;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.chribi.predictable.util.ConfidenceFormatProvider;
import de.chribi.predictable.util.DefaultStringsProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;

@Module
public class StringsModule {
    private DefaultStringsProvider strings;

    @Provides
    @Singleton
    PredictionStatusStringProvider provideStatusStrings(Context context) {
        if(strings == null) {
            strings = new DefaultStringsProvider(context);
        }
        return strings;
    }

    @Provides
    @Singleton
    ConfidenceFormatProvider provideConfidenceFormatter(Context context) {
        if(strings == null) {
            strings = new DefaultStringsProvider(context);
        }
        return strings;
    }
}
