package de.chribi.predictable.storage;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SqlitePredictionStorageTest extends PredictionStorageTest {
    private static final String testDbName = "__test__predictable.db";

    @Override
    PredictionStorage createStorage() {
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase(testDbName);
        SqlitePredictableHelper dbHelper = new SqlitePredictableHelper(context, testDbName);
        return new SqlitePredictionStorage(dbHelper);
    }
}
