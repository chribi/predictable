package de.chribi.predictable;

import android.app.Application;
import android.content.Context;

import de.chribi.predictable.di.AppComponent;
import de.chribi.predictable.di.DaggerAppComponent;

public class PredictableApp extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = createComponent();
    }

    protected AppComponent createComponent() {
        return DaggerAppComponent.builder()
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static PredictableApp get(Context context) {
        return (PredictableApp)context.getApplicationContext();
    }
}
