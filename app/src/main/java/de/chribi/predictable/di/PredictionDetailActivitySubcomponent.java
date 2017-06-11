package de.chribi.predictable.di;


import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import de.chribi.predictable.predictiondetail.PredictionDetailActivity;

@Subcomponent(modules = PredictionDetailActivityModule.class)
interface PredictionDetailActivitySubcomponent extends AndroidInjector<PredictionDetailActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<PredictionDetailActivity> {}
}
