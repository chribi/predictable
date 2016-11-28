package de.chribi.predictable.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class SqlitePredictableHelper extends SQLiteOpenHelper {
    public SqlitePredictableHelper(Context context, String dbName) {
        super(context, dbName, null, SqliteSchemas.SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SqliteSchemas.PredictedEvents.SQL_CREATE);
        db.execSQL(SqliteSchemas.Predictions.SQL_CREATE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException("Database out of date!");
    }
}
