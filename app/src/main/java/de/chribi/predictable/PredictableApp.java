package de.chribi.predictable;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import de.chribi.predictable.di.ConfigurationModule;
import de.chribi.predictable.di.DaggerAppComponent;
import de.chribi.predictable.di.PredictableComponent;

public class PredictableApp extends Application implements HasActivityInjector {
    private static final String DB_NAME = "predictable.db";

    protected PredictableComponent predictableComponent;
    @Inject DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PredictableApp", "onCreate");
        predictableComponent = createComponent();
        predictableComponent.inject(this);
        JodaTimeAndroid.init(this);
    }

    protected PredictableComponent createComponent() {
        ConfigurationModule configuration = new ConfigurationModule(this, DB_NAME);
        return DaggerAppComponent.builder()
                .configurationModule(configuration)
                .build();
    }

    final public PredictableComponent getPredictableComponent() {
        return predictableComponent;
    }

    public static PredictableApp get(Context context) {
        return (PredictableApp)context.getApplicationContext();
    }
}
