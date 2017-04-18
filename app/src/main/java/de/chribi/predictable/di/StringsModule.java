package de.chribi.predictable.di;


import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.chribi.predictable.util.ConfidenceFormatProvider;
import de.chribi.predictable.util.DefaultStringsProvider;
import de.chribi.predictable.util.LongConfidenceFormatProvider;
import de.chribi.predictable.util.PredictionStatusStringProvider;

@Module
public class StringsModule {
    private DefaultStringsProvider strings;
    private ConfidenceFormatProvider longConfidenceFormatter;

    @Provides
    @Singleton
    PredictionStatusStringProvider provideStatusStrings(Context context) {
        if(strings == null) {
            strings = new DefaultStringsProvider(context);
        }
        return strings;
    }

    @Provides
    @Named("short")
    @Singleton
    ConfidenceFormatProvider provideConfidenceFormatter(Context context) {
        if(strings == null) {
            strings = new DefaultStringsProvider(context);
        }
        return strings;
    }

    @Provides
    @Named("long")
    @Singleton
    ConfidenceFormatProvider provideLongConfidenceFormatter(Context context) {
        if (longConfidenceFormatter == null) {
            longConfidenceFormatter = new LongConfidenceFormatProvider(context);
        }
        return longConfidenceFormatter;
    }
}
