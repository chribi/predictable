package de.chribi.predictable.di;


import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;
import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.predictiondetail.PredictionDetailActivity;
import de.chribi.predictable.predictionlist.PredictionListActivity;
import de.chribi.predictable.startscreen.StartScreenActivity;
import de.chribi.predictable.statistics.StatisticsActivity;

@SuppressWarnings("unused")
@Module()
abstract class ActivitySubcomponentBuildersModule {
    @ContributesAndroidInjector(modules = StartScreenActivityModule.class)
    abstract StartScreenActivity contributeStartScreenActivityInjector();

    @ContributesAndroidInjector(modules = PredictionListActivityModule.class)
    abstract PredictionListActivity contributePredictionListActivityInjector();

    @ContributesAndroidInjector(modules = PredictionDetailActivityModule.class)
    abstract PredictionDetailActivity contributePredictionDetailActivityInjector();

    @ContributesAndroidInjector(modules = NewPredictionActivityModule.class)
    abstract NewPredictionActivity contributeNewPredictionActivityInjector();

    @ContributesAndroidInjector
    abstract StatisticsActivity contributeStatisticsActivityInjector();
}
