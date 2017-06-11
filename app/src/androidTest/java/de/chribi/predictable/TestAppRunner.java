package de.chribi.predictable;


import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

// From https://stackoverflow.com/a/33959671
public class TestAppRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestApp.class.getName(), context);
    }
}
