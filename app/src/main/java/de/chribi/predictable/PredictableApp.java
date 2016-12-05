package de.chribi.predictable;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

import de.chribi.predictable.di.ConfigurationModule;
import de.chribi.predictable.di.DaggerAppComponent;
import de.chribi.predictable.di.PredictableComponent;

public class PredictableApp extends Application {
    public static final String DB_NAME = "predictable.db";

    private PredictableComponent predictableComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PredictableApp", "onCreate");
        predictableComponent = createComponent();
        JodaTimeAndroid.init(this);
    }

    protected PredictableComponent createComponent() {
        ConfigurationModule configuration = new ConfigurationModule(this, DB_NAME);
        return DaggerAppComponent.builder()
                .configurationModule(configuration)
                .build();
    }

    public PredictableComponent getPredictableComponent() {
        return predictableComponent;
    }

    public void setPredictableComponent(PredictableComponent predictableComponent) {
        this.predictableComponent = predictableComponent;
    }

    public static PredictableApp get(Context context) {
        return (PredictableApp)context.getApplicationContext();
    }
}
