package de.chribi.predictable.di;


import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;
import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.predictionlist.PredictionListActivity;

@SuppressWarnings("unused")
@Module(subcomponents = {
        PredictionListActivitySubcomponent.class,
        NewPredictionActivitySubcomponent.class
})
abstract class ActivitySubcomponentBuildersModule {
    @Binds
    @IntoMap
    @ActivityKey(PredictionListActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindPredictionListInjectorFactory(PredictionListActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(NewPredictionActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindNewPredictionInjectorFactory(NewPredictionActivitySubcomponent.Builder builder);
}
