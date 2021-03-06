package de.chribi.predictable.storage;


@SuppressWarnings("HardCodedStringLiteral")
class SqliteSchemas {
    static final int SCHEMA_VERSION = 1;

    private SqliteSchemas() {}

    public static class Predictions {
        static final String TABLE_NAME = "events";

        static final String COLUMN_ID = "ev_id";
        static final String COLUMN_TITLE = "ev_title";
        static final String COLUMN_DESCRIPTION = "ev_description";
        static final String COLUMN_STATE = "ev_state";
        static final String COLUMN_JUDGED_DATE = "ev_judged_date";
        static final String COLUMN_DUE_DATE = "ev_due_date";

        static final String ID_REF = TABLE_NAME + "(" + COLUMN_ID + ")";

        static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT NOT NULL, " +
                        COLUMN_DESCRIPTION + " TEXT," +
                        COLUMN_STATE + " INTEGER NOT NULL, " +
                        COLUMN_JUDGED_DATE + " INTEGER, " +
                        COLUMN_DUE_DATE + " INTEGER NOT NULL)";
    }

    static class Confidences {
        static final String TABLE_NAME = "predictions";

        static final String COLUMN_ID = "prediction_id";
        static final String COLUMN_CONFIDENCE = "prediction_confidence";
        static final String COLUMN_CREATION_DATE = "prediction_date";
        static final String COLUMN_PREDICTION = "prediction_event_id";

        static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_CONFIDENCE + " REAL NOT NULL, " +
                        COLUMN_CREATION_DATE + " INTEGER NOT NULL," +
                        COLUMN_PREDICTION + " INTEGER " +
                            "REFERENCES " + Predictions.ID_REF +
                            " ON DELETE CASCADE)";
    }
}
