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
@Module(subcomponents = {
        StartScreenActivitySubcomponent.class,
        PredictionListActivitySubcomponent.class,
        PredictionDetailActivitySubcomponent.class,
        NewPredictionActivitySubcomponent.class,
})
abstract class ActivitySubcomponentBuildersModule {
    @Binds
    @IntoMap
    @ActivityKey(StartScreenActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindStartScreenInjectorFactory(StartScreenActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(PredictionListActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindPredictionListInjectorFactory(PredictionListActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(PredictionDetailActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindPredictionDetailInjectorFactory(PredictionDetailActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(NewPredictionActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindNewPredictionInjectorFactory(NewPredictionActivitySubcomponent.Builder builder);

    @ContributesAndroidInjector()
    abstract StatisticsActivity contributeStatisticsActivityInjector();
}
