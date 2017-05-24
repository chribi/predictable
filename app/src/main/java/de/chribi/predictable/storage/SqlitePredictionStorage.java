package de.chribi.predictable.storage;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.queries.PredictionQuery;

public class SqlitePredictionStorage implements PredictionStorage {
    // table of predictions joined with confidences
    private static final String joinedPredictionsTable =
            SqliteSchemas.Predictions.TABLE_NAME +
                    " LEFT JOIN " + SqliteSchemas.Confidences.TABLE_NAME +
                    " ON " + SqliteSchemas.Predictions.COLUMN_ID +
                    " = " + SqliteSchemas.Confidences.COLUMN_PREDICTION;

    // columns to use in queries for predictions
    private static final String[] queryColumns = new String[]{
            SqliteSchemas.Predictions.COLUMN_ID,
            SqliteSchemas.Predictions.COLUMN_TITLE,
            SqliteSchemas.Predictions.COLUMN_DESCRIPTION,
            SqliteSchemas.Predictions.COLUMN_STATE,
            SqliteSchemas.Predictions.COLUMN_JUDGED_DATE,
            SqliteSchemas.Predictions.COLUMN_DUE_DATE,
            SqliteSchemas.Confidences.COLUMN_CONFIDENCE,
            SqliteSchemas.Confidences.COLUMN_CREATION_DATE
    };

    // indices for the columns in a prediction query
    private static final int idxId = 0;
    private static final int idxTitle = 1;
    private static final int idxDescription = 2;
    private static final int idxState = 3;
    private static final int idxJudgementDate = 4;
    private static final int idxDueDate = 5;
    private static final int idxConfidence = 6;
    private static final int idxConfidenceDate = 7;

    private final SqlitePredictableHelper dbHelper;
    private final SqliteConstraintInterpreter queryInterpreter;

    /**
     * Create a SqlitePredictionStorage.
     *
     * @param dbHelper The {@link SqlitePredictableHelper} handling the database connection.
     */
    public SqlitePredictionStorage(@NonNull SqlitePredictableHelper dbHelper) {
        this.dbHelper = dbHelper;
        queryInterpreter = new SqliteConstraintInterpreter();
    }

    @NonNull
    @Override
    public List<Prediction> getPredictions() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String orderBy = SqliteSchemas.Predictions.COLUMN_ID + " ASC, "
                + SqliteSchemas.Confidences.COLUMN_CREATION_DATE + " ASC";

        Cursor dbCursor = db.query(joinedPredictionsTable,
                queryColumns,
                null, null, null, null,
                orderBy);
        return getPredictionsFromCursor(dbCursor);
    }

    @NonNull
    @Override
    public List<Prediction> getPredictions(PredictionQuery query)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SqliteWhereClause where = query.getWhereClause().accept(queryInterpreter);
        Cursor dbCursor = db.query(joinedPredictionsTable, queryColumns,
                where.getClause(), where.getArgs(),
                null, null, null, null);
        return getPredictionsFromCursor(dbCursor);
    }

    @NonNull
    private List<Prediction> getPredictionsFromCursor(Cursor cursor) {
        // Most predictions will probably only have one or two confidence statements, so using
        // the cursor count as result size should not be too far off.
        List<Prediction> result = new ArrayList<>(cursor.getCount());

        cursor.moveToNext();
        Prediction prediction = getCurrentPrediction(cursor);
        while (prediction != null) {
            result.add(prediction);
            prediction = getCurrentPrediction(cursor);
        }
        cursor.close();
        return result;
    }

    // read a single Prediction from the given cursor.
    // Assumes that the cursor is moved to the first row for the prediction.
    // Will move the cursor beyond the current prediction.
    // Will return null when cursor.isAfterLast()
    private Prediction getCurrentPrediction(Cursor cursor) {
        if (cursor.isAfterLast()) {
            return null;
        }

        long predictionId = cursor.getLong(idxId);
        String title = cursor.getString(idxTitle);
        String description = cursor.getString(idxDescription);
        PredictionState state = PredictionState.fromStoredValue(cursor.getInt(idxState));
        Judgement judgement;
        if (state != PredictionState.Open) {
            Date judgementDate = new Date(cursor.getLong(idxJudgementDate));
            judgement = Judgement.create(state, judgementDate);
        } else {
            judgement = Judgement.Open;
        }
        Date dueDate = new Date(cursor.getLong(idxDueDate));
        List<ConfidenceStatement> confidenceStatements = new ArrayList<>();
        long currentId = predictionId;
        while (currentId == predictionId) {
            ConfidenceStatement confidenceStatement = getCurrentConfidence(cursor);
            if (confidenceStatement != null) {
                confidenceStatements.add(confidenceStatement);
            }
            if (cursor.moveToNext()) {
                currentId = cursor.getLong(idxId);
            } else {
                currentId = predictionId + 1;
            }
        }
        return Prediction.builder()
                .setId(predictionId)
                .setTitle(title)
                .setDescription(description)
                .setJudgement(judgement)
                .setDueDate(dueDate)
                .setConfidences(confidenceStatements)
                .build();
    }

    private ConfidenceStatement getCurrentConfidence(Cursor cursor) {
        ConfidenceStatement confidenceStatement = null;
        if (!cursor.isNull(idxConfidence) && !cursor.isNull(idxConfidenceDate)) {
            double confidence = cursor.getDouble(idxConfidence);
            Date creation = new Date(cursor.getLong(idxConfidenceDate));
            confidenceStatement = ConfidenceStatement.create(confidence, creation);
        }
        return confidenceStatement;
    }


    @Nullable
    @Override
    public Prediction getPredictionById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String whereClause = SqliteSchemas.Predictions.COLUMN_ID + " = ?";
        String[] whereArgs = new String[]{ String.valueOf(id) };

        String orderBy = SqliteSchemas.Confidences.COLUMN_CREATION_DATE + " ASC";


        Cursor cursor = db.query(joinedPredictionsTable,
                queryColumns,
                whereClause, whereArgs,
                null, null,
                orderBy);
        cursor.moveToNext();
        Prediction prediction = getCurrentPrediction(cursor);
        cursor.close();
        return prediction;
    }

    @NonNull
    @Override
    public Prediction createPrediction(@NonNull String title, @Nullable String description,
                                       @NonNull Date dueDate, @NonNull List<ConfidenceStatement> confidenceStatements) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = 0;

        db.beginTransaction();
        try {
            ContentValues values =
                    createContentValuesForPrediction(title, description, dueDate, null);
            id = db.insert(SqliteSchemas.Predictions.TABLE_NAME, null, values);

            for (ConfidenceStatement confidenceStatement : confidenceStatements) {
                ContentValues predictionValues =
                        createContentValuesForPrediction(id, confidenceStatement);
                db.insert(SqliteSchemas.Confidences.TABLE_NAME, null, predictionValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return Prediction.builder()
                .setId(id)
                .setTitle(title)
                .setDescription(description)
                .setDueDate(dueDate)
                .setConfidences(confidenceStatements)
                .build();
    }

    @Override public void updatePrediction(long id, @NonNull Prediction prediction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            String[] argPredictionId = new String[]{ String.valueOf(id) };

            ContentValues newValues = createContentValuesForPrediction(
                    prediction.getTitle(),
                    prediction.getDescription(), prediction.getDueDate(),
                    prediction.getJudgement());
            int rows = db.update(SqliteSchemas.Predictions.TABLE_NAME, newValues,
                    SqliteSchemas.Predictions.COLUMN_ID + " = ?", argPredictionId);

            // only write to the table of confidences if the given id exists
            if (rows > 0) {
                // update confidences by deleting all confidences currently in the db
                // and then inserting all new ones
                db.delete(SqliteSchemas.Confidences.TABLE_NAME,
                        SqliteSchemas.Confidences.COLUMN_PREDICTION + " = ?", argPredictionId);

                for (ConfidenceStatement confidenceStatement : prediction.getConfidences()) {
                    ContentValues predictionValues = createContentValuesForPrediction(id, confidenceStatement);
                    db.insert(SqliteSchemas.Confidences.TABLE_NAME, null, predictionValues);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void deletePrediction(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = SqliteSchemas.Predictions.COLUMN_ID + " = ?";
        String[] whereArgs = new String[]{ String.valueOf(id) };
        db.delete(SqliteSchemas.Predictions.TABLE_NAME, whereClause, whereArgs);
        // XXX Don't need to delete confidences from db, as this is handled by sqlite
    }

    private static ContentValues
    createContentValuesForPrediction(String title, String description, Date dueDate,
                                     Judgement judgement) {
        ContentValues values = new ContentValues();
        values.put(SqliteSchemas.Predictions.COLUMN_TITLE, title);
        values.put(SqliteSchemas.Predictions.COLUMN_DESCRIPTION, description);
        values.put(SqliteSchemas.Predictions.COLUMN_DUE_DATE, dueDate.getTime());
        PredictionState state = judgement != null ? judgement.getState() : PredictionState.Open;
        values.put(SqliteSchemas.Predictions.COLUMN_STATE, state.getStoredValue());
        values.put(SqliteSchemas.Predictions.COLUMN_JUDGED_DATE,
                judgement != null ? judgement.getDate().getTime() : null);
        return values;
    }

    private static ContentValues createContentValuesForPrediction(long id,
                                                                  ConfidenceStatement confidenceStatement) {
        ContentValues predictionValues = new ContentValues();
        predictionValues.put(SqliteSchemas.Confidences.COLUMN_CONFIDENCE,
                confidenceStatement.getConfidence());
        predictionValues.put(SqliteSchemas.Confidences.COLUMN_CREATION_DATE,
                confidenceStatement.getCreationDate().getTime());
        predictionValues.put(SqliteSchemas.Confidences.COLUMN_PREDICTION, id);
        return predictionValues;
    }
}
