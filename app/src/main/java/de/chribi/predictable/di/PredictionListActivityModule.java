package de.chribi.predictable.di;


import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.predictionlist.PredictionListActivity;
import de.chribi.predictable.predictionsets.PredictionSet;

@Module
abstract class PredictionListActivityModule {
    @Binds
    abstract PredictionItemView bindItemView(PredictionListActivity activity);

    @Provides
    static PredictionSet predictionSet(PredictionListActivity activity) {
        return activity.getPredictionSet();
    }
}

