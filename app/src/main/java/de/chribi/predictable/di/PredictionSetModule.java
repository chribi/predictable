package de.chribi.predictable.di;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.chribi.predictable.predictionsets.DefaultPredictionSetQueries;
import de.chribi.predictable.predictionsets.DefaultPredictionSetTitles;
import de.chribi.predictable.predictionsets.PredictionSetQueries;
import de.chribi.predictable.predictionsets.PredictionSetTitles;
import de.chribi.predictable.util.DateTimeProvider;

@Module
public class PredictionSetModule {

    @Provides
    @Singleton
    PredictionSetQueries providePredictionSetQueries(DateTimeProvider dateTime) {
        return new DefaultPredictionSetQueries(dateTime);
    }

    @Provides
    @Singleton
    PredictionSetTitles providePredictionSetTitles(Context context) {
        return new DefaultPredictionSetTitles(context);
    }
}
