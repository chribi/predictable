package de.chribi.testutils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;


import java.util.Collection;

public class EspressoUtils {
    private EspressoUtils() {}

    /**
     * Force a configuration change by rotating the screen twice
     * @param activity The current activity
     */
    // from http://blog.sqisland.com/2015/10/espresso-save-and-restore-state.html
    public static void forceConfigurationChange(Activity activity) {
        Context context = InstrumentationRegistry.getTargetContext();
        int currentOrientation = context.getResources().getConfiguration().orientation;

        if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

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
