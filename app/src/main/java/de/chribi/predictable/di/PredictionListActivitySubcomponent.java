package de.chribi.predictable.di;


import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import de.chribi.predictable.predictionlist.PredictionListActivity;

@Subcomponent(modules = PredictionListActivityModule.class)
interface PredictionListActivitySubcomponent extends AndroidInjector<PredictionListActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<PredictionListActivity> {}
}
