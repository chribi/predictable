package de.chribi.predictable;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Before;

import java.util.Locale;

import de.chribi.predictable.di.PredictableComponent;

public class BaseUiTest {
    public static final Locale DefaultTestLocale = new Locale("en", "US");
    @Before
    public void setUpTest() {
        setTestLocale(DefaultTestLocale);
    }

    @Before
    public void setUpApplication() {
        PredictableComponent testPredictableComponent = createTestComponent();
        if(testPredictableComponent != null) {
            Log.d("BaseUiTest", "setUpApplication: Custom PredictableComponent is used!");
            getApplication().setPredictableComponent(testPredictableComponent);
        }
    }

    // from http://stackoverflow.com/questions/4985805/set-locale-programatically
    public void setTestLocale(Locale locale) {
        Locale.setDefault(locale);
        Resources res =  InstrumentationRegistry.getTargetContext().getResources();
        Configuration config = res.getConfiguration();
        if(Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public PredictableApp getApplication() {
        return (PredictableApp)InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    /**
     * Method to create a custom {@link PredictableComponent} for use in this test class.
     * @return A custom PredictableComponent or null when you want to use the default one.
     */
    protected PredictableComponent createTestComponent() {
        return null;
    }
}