package de.chribi.predictable.di;


import dagger.Binds;
import dagger.Module;
import de.chribi.predictable.predictiondetail.PredictionDetailActivity;
import de.chribi.predictable.predictiondetail.PredictionDetailView;

@Module
abstract class PredictionDetailActivityModule {
    @Binds
    abstract PredictionDetailView bindView(PredictionDetailActivity activity);
}
