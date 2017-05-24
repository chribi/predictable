package de.chribi.predictable.storage;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SqlitePredictionStorageQueryIntegrationTest
        extends PredictionStorageQueryIntegrationTest<SqlitePredictionStorage> {
    private static final String testDbName = "__test__predictable.db";

    @Override SqlitePredictionStorage createStorage() {
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase(testDbName);
        SqlitePredictableHelper dbHelper = new SqlitePredictableHelper(context, testDbName);
        return new SqlitePredictionStorage(dbHelper);
    }
}
