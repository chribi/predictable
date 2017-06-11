package de.chribi.predictable.di;


import dagger.Binds;
import dagger.Module;
import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.newprediction.NewPredictionView;

@Module
abstract class NewPredictionActivityModule {
    @Binds
    abstract NewPredictionView bindView(NewPredictionActivity activity);
}
