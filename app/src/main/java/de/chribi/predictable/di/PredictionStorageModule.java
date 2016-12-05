package de.chribi.predictable.di;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.chribi.predictable.storage.InMemoryPredictionStorage;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.storage.SqlitePredictableHelper;
import de.chribi.predictable.storage.SqlitePredictionStorage;

@Module
public class PredictionStorageModule {
    @Provides
    @Singleton
    PredictionStorage providePredictionStorage(Context context,
                                               @Named("DatabaseName") String dbName) {
        SqlitePredictableHelper dbHelper = new SqlitePredictableHelper(context, dbName);
        return new SqlitePredictionStorage(dbHelper);
    }
}
