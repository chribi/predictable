package de.chribi.predictable;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.chribi.predictable.di.DaggerAppComponent;
import de.chribi.predictable.di.PredictableComponent;

public class PredictableApp extends Application {
    private PredictableComponent predictableComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        predictableComponent = createComponent();
        Log.d("PredictableApp", "onCreate");
    }

    protected PredictableComponent createComponent() {
        return DaggerAppComponent.builder()
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
