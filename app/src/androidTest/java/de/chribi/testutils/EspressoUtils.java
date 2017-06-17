package de.chribi.testutils;


import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;


import java.util.Collection;

public class EspressoUtils {
    private EspressoUtils() {}

    // from http://qathread.blogspot.de/2014/09/discovering-espresso-for-android-how-to.html
    public static Activity getCurrentActivity() {
        GetCurrentActivityRunnable getActivityRunnable = new GetCurrentActivityRunnable();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(getActivityRunnable);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        return getActivityRunnable.currentActivity;
    }

    private static class GetCurrentActivityRunnable implements Runnable {
        private Activity currentActivity = null;
        @Override
        public void run() {
            ActivityLifecycleMonitor activityMonitor =
                    ActivityLifecycleMonitorRegistry.getInstance();
            Collection resumedActivities = activityMonitor.getActivitiesInStage(Stage.RESUMED);
            if(resumedActivities.iterator().hasNext()) {
                currentActivity = (Activity)resumedActivities.iterator().next();
            }
        }
    }
}
