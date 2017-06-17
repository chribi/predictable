package de.chribi.predictable;


import android.app.Activity;
import android.support.test.InstrumentationRegistry;

import java.util.HashMap;
import java.util.Map;

import dagger.android.AndroidInjector;
import de.chribi.predictable.di.ConfigurationModule;
import de.chribi.predictable.di.DaggerAppComponent;
import de.chribi.predictable.di.PredictableComponent;
import de.chribi.predictable.storage.SqlitePredictionStorage;

public class TestApp extends PredictableApp implements AndroidInjector<Activity> {
    private static final String TEST_DB_NAME = "test_predictable.db";

    private Map<Class<? extends Activity>, AndroidInjector<Activity>> testInjectors;

    public TestApp() {
        testInjectors = new HashMap<>();
    }

    @Override public AndroidInjector<Activity> activityInjector() {
        return this;
    }

    /**
     * Inject an activity.  This will use a custom injector if one is provided and fall back
     * to the injector as resolved by dagger otherwise.
     * @param instance The activity to inject.
     */
    @Override public void inject(Activity instance) {
        Class<? extends Activity> cls = instance.getClass();
        if(testInjectors.containsKey(cls)) {
            testInjectors.get(cls).inject(instance);
        } else {
            super.activityInjector().inject(instance);
        }
    }

    /**
     * Set a custom injector for a given activity.  This will override the default injector for
     * this class that would be resolved from dagger.
     * @param activityClass The activity class for which to provide a custom injector.
     * @param injector The injector for the activity.  If this is null, the default injector will
     *                 be used for the given activity class.
     */
    @SuppressWarnings("unchecked")
    public void setCustomActivityInjector(Class<? extends Activity> activityClass,
                                          AndroidInjector<? extends Activity> injector) {
        if(injector == null) {
            testInjectors.remove(activityClass);
        } else {
            testInjectors.put(activityClass, (AndroidInjector) injector);
        }
    }

    @Override public PredictableComponent createComponent() {
        // Provide a custom ConfigurationModule that uses a different database for testing
        ConfigurationModule configuration =
                new ConfigurationModule(InstrumentationRegistry.getTargetContext(),
                        TEST_DB_NAME);
        return DaggerAppComponent.builder()
                .configurationModule(configuration)
                .build();
    }

    private void resetComponent() {
        this.predictableComponent = createComponent();
        predictableComponent.inject(this);
    }

    public void clearSqliteDatabase() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(TEST_DB_NAME);
        resetComponent();
    }
}
