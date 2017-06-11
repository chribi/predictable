package de.chribi.predictable.di;


import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import de.chribi.predictable.newprediction.NewPredictionActivity;

@Subcomponent(modules = NewPredictionActivityModule.class)
interface NewPredictionActivitySubcomponent extends AndroidInjector<NewPredictionActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<NewPredictionActivity> {}
}
