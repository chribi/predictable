package de.chribi.predictable;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;

import java.util.Locale;

import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.di.ConfigurationModule;
import de.chribi.predictable.di.DaggerAppComponent;
import de.chribi.predictable.di.PredictableComponent;
import de.chribi.predictable.storage.PredictionStorage;

public class BaseUiTest {
    public static final Locale DEFAULT_TEST_LOCALE = new Locale("en", "US");

    @Before
    public void setUpTest() {
        setTestLocale(DEFAULT_TEST_LOCALE);
    }

    // from http://stackoverflow.com/questions/4985805/set-locale-programatically
    final protected void setTestLocale(Locale locale) {
        Locale.setDefault(locale);
        Resources res = InstrumentationRegistry.getTargetContext().getResources();
        Configuration config = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    final protected TestApp getApplication() {
        return (TestApp) InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    final protected void setupSqliteDb(Prediction... predictions) {
        getApplication().clearSqliteDatabase();
        PredictionStorage storage = getApplication().getPredictableComponent().getStorage();
        for (Prediction prediction : predictions) {
            long id = storage.createPrediction(prediction.getTitle(), prediction.getDescription(),
                        prediction.getDueDate(), prediction.getConfidences())
                    .getId();
            storage.updatePrediction(id, prediction);
        }
    }
}
