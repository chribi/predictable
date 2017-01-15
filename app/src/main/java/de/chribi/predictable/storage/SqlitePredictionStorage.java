package de.chribi.predictable.storage;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

public class SqlitePredictionStorage implements PredictionStorage {
    @NonNull
    private SqlitePredictableHelper dbHelper;

    /**
     * Create a SqlitePredictionStorage.
     * @param dbHelper The {@link SqlitePredictableHelper} handling the database connection.
     */
    public SqlitePredictionStorage(SqlitePredictableHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // table of predicted events joined with predictions
    private static final String joinedPredictedEventTable =
            SqliteSchemas.PredictedEvents.TABLE_NAME +
            " LEFT JOIN " + SqliteSchemas.Predictions.TABLE_NAME +
            " ON " + SqliteSchemas.PredictedEvents.COLUMN_ID +
            " = " + SqliteSchemas.Predictions.COLUMN_EVENT;

    // columns to use in queries for events
    private static final String[] queryEventColumns = new String[]{
            SqliteSchemas.PredictedEvents.COLUMN_ID,
            SqliteSchemas.PredictedEvents.COLUMN_TITLE,
            SqliteSchemas.PredictedEvents.COLUMN_DESCRIPTION,
            SqliteSchemas.PredictedEvents.COLUMN_STATE,
            SqliteSchemas.PredictedEvents.COLUMN_JUDGED_DATE,
            SqliteSchemas.PredictedEvents.COLUMN_DUE_DATE,
            SqliteSchemas.Predictions.COLUMN_CONFIDENCE,
            SqliteSchemas.Predictions.COLUMN_CREATION_DATE
    };

    // indices for the columns in an event query
    private static final int idxEventId = 0;
    private static final int idxEventTitle = 1;
    private static final int idxEventDescription = 2;
    private static final int idxEventState = 3;
    private static final int idxEventJudgementDate = 4;
    private static final int idxEventDueDate = 5;
    private static final int idxConfidence = 6;
    private static final int idxPredictionDate = 7;

    @NonNull
    @Override
    public List<PredictedEvent> getPredictedEvents() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String orderBy = SqliteSchemas.PredictedEvents.COLUMN_ID + " ASC, "
                + SqliteSchemas.Predictions.COLUMN_CREATION_DATE + " ASC";

        Cursor dbCursor = db.query(joinedPredictedEventTable,
                queryEventColumns,
                null, null, null, null,
                orderBy);
        return getPredictedEventsFromCursor(dbCursor);
    }

    @NonNull
    private List<PredictedEvent> getPredictedEventsFromCursor(Cursor cursor) {
        // Most events will probably only have one prediction, so using the cursor
        // count as result size should not be too far off.
        List<PredictedEvent> result = new ArrayList<>(cursor.getCount());

        cursor.moveToNext();
        PredictedEvent event = getCurrentEvent(cursor);
        while (event != null) {
            result.add(event);
            event = getCurrentEvent(cursor);
        }
        cursor.close();
        return result;
    }

    // read a single PredictedEvent from the given cursor.
    // Assumes that the cursor is moved to the first row for the predicted event.
    // Will move the cursor beyond the current event.
    // Will return null when cursor.isAfterLast()
    private PredictedEvent getCurrentEvent(Cursor cursor) {
        if(cursor.isAfterLast()) {
            return null;
        }

        long eventId = cursor.getLong(idxEventId);
        String title = cursor.getString(idxEventTitle);
        String description = cursor.getString(idxEventDescription);
        PredictionState state = PredictionState.fromStoredValue(cursor.getInt(idxEventState));
        Judgement judgement;
        if(state != PredictionState.Open) {
            Date judgementDate = new Date(cursor.getLong(idxEventJudgementDate));
            judgement = new Judgement(state, judgementDate);
        } else {
            judgement = null;
        }
        Date dueDate = new Date(cursor.getLong(idxEventDueDate));
        List<Prediction> predictions = new ArrayList<>();
        long currentId = eventId;
        while(currentId == eventId) {
            Prediction prediction = getCurrentPrediction(cursor);
            if(prediction != null) {
                predictions.add(prediction);
            }
            if(cursor.moveToNext()) {
                currentId = cursor.getLong(idxEventId);
            } else {
                currentId = eventId + 1;
            }
        }
        return new PredictedEvent(eventId, title, description, judgement, dueDate, predictions);
    }

    private Prediction getCurrentPrediction(Cursor cursor) {
        Prediction prediction = null;
        if (!cursor.isNull(idxConfidence) && !cursor.isNull(idxPredictionDate)) {
            double confidence = cursor.getDouble(idxConfidence);
            Date creation = new Date(cursor.getLong(idxPredictionDate));
            prediction = new Prediction(confidence, creation);
        }
        return prediction;
    }


    @Nullable
    @Override
    public PredictedEvent getPredictedEventById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String whereClause = SqliteSchemas.PredictedEvents.COLUMN_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        String orderBy = SqliteSchemas.Predictions.COLUMN_CREATION_DATE + " ASC";


        Cursor cursor = db.query(joinedPredictedEventTable,
                queryEventColumns,
                whereClause, whereArgs,
                null, null,
                orderBy);
        cursor.moveToNext();
        PredictedEvent event = getCurrentEvent(cursor);
        cursor.close();
        return event;
    }

    @NonNull
    @Override
    public PredictedEvent createPredictedEvent(@NonNull String title, @Nullable String description,
                                               @NonNull Date dueDate, @NonNull List<Prediction> predictions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long eventId = 0;

        db.beginTransaction();
        try {
            ContentValues values =
                    createContentValuesForPredictedEvent(title, description, dueDate, null);
            eventId = db.insert(SqliteSchemas.PredictedEvents.TABLE_NAME, null, values);

            for (Prediction prediction : predictions) {
                ContentValues predictionValues =
                        createContentValuesForPrediction(eventId, prediction);
                db.insert(SqliteSchemas.Predictions.TABLE_NAME, null, predictionValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return new PredictedEvent(eventId, title, description, null, dueDate, predictions);
    }

    @NonNull
    @Override
    public PredictedEvent.Editor edit(@NonNull PredictedEvent event) {
        return new SqliteEventEditor(event, dbHelper);
    }

    @Override
    public void deletePredictedEvent(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = SqliteSchemas.PredictedEvents.COLUMN_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        db.delete(SqliteSchemas.PredictedEvents.TABLE_NAME, whereClause, whereArgs);
        // XXX Don't need to delete predictions from prediction table, as this is handled by sqlite
    }

    private static ContentValues
    createContentValuesForPredictedEvent(String title, String description, Date dueDate,
                                         Judgement judgement) {
        ContentValues values = new ContentValues();
        values.put(SqliteSchemas.PredictedEvents.COLUMN_TITLE, title);
        values.put(SqliteSchemas.PredictedEvents.COLUMN_DESCRIPTION, description);
        values.put(SqliteSchemas.PredictedEvents.COLUMN_DUE_DATE, dueDate.getTime());
        PredictionState state = judgement != null ? judgement.getState() : PredictionState.Open;
        values.put(SqliteSchemas.PredictedEvents.COLUMN_STATE, state.getStoredValue());
        values.put(SqliteSchemas.PredictedEvents.COLUMN_JUDGED_DATE,
                judgement != null ? judgement.getDate().getTime() : null);
        return values;
    }

    private static ContentValues
    createContentValuesForPrediction(long eventId, Prediction prediction) {
        ContentValues predictionValues = new ContentValues();
        predictionValues.put(SqliteSchemas.Predictions.COLUMN_CONFIDENCE,
                prediction.getConfidence());
        predictionValues.put(SqliteSchemas.Predictions.COLUMN_CREATION_DATE,
                prediction.getCreationDate().getTime());
        predictionValues.put(SqliteSchemas.Predictions.COLUMN_EVENT, eventId);
        return predictionValues;
    }

    private static class SqliteEventEditor implements PredictedEvent.Editor {
        private PredictedEvent editedEvent;

        private @NonNull String currentTitle;
        private String currentDescription;
        private Judgement currentJudgement;
        private Date currentDueDate;
        private List<Prediction> currentPredictions;

        private SQLiteOpenHelper dbHelper;

        public SqliteEventEditor(PredictedEvent event, SQLiteOpenHelper sqliteHelper) {
            editedEvent = event;
            currentTitle = event.getTitle();
            currentDescription = event.getDescription();
            currentJudgement = event.getJudgement();
            currentDueDate = event.getDueDate();
            currentPredictions = new ArrayList<>(event.getPredictions());

            dbHelper = sqliteHelper;
        }

        @Override
        public PredictedEvent commit() {
            long id = editedEvent.getId();
            PredictedEvent newPredictedEvent = new PredictedEvent(
                    id, getTitle(), getDescription(), getJudgement(), getDueDate(),
                    getPredictions());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                String[] argEventId = new String[]{ String.valueOf(id) };

                ContentValues newValues = createContentValuesForPredictedEvent(
                        newPredictedEvent.getTitle(),
                        newPredictedEvent.getDescription(), newPredictedEvent.getDueDate(),
                        newPredictedEvent.getJudgement());
                db.update(SqliteSchemas.PredictedEvents.TABLE_NAME, newValues,
                        SqliteSchemas.PredictedEvents.COLUMN_ID + " = ?", argEventId);

                // update predictions by deleting all predictions currently in the db
                // and then inserting all new ones
                db.delete(SqliteSchemas.Predictions.TABLE_NAME,
                        SqliteSchemas.Predictions.COLUMN_EVENT + " = ?", argEventId);

                for (Prediction prediction : newPredictedEvent.getPredictions()) {
                    ContentValues predictionValues = createContentValuesForPrediction(id, prediction);
                    db.insert(SqliteSchemas.Predictions.TABLE_NAME, null, predictionValues);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            return newPredictedEvent;
        }

        @Override
        public void setTile(@NonNull String newTitle) {
            currentTitle = newTitle;
        }

        @NonNull
        @Override
        public String getTitle() {
            return currentTitle;
        }

        @Override
        public void setDescription(@Nullable String newDescription) {
            currentDescription = newDescription;
        }

        @Nullable
        @Override
        public String getDescription() {
            return currentDescription;
        }

        @Override
        public void setJudgement(@Nullable Judgement newJudgment) {
            currentJudgement = newJudgment;
        }

        @Nullable
        @Override
        public Judgement getJudgement() {
            return currentJudgement;
        }

        @Override
        public void setDueDate(@NonNull Date newDueDate) {
            currentDueDate = newDueDate;
        }

        @NonNull
        @Override
        public Date getDueDate() {
            return currentDueDate;
        }

        @Override
        public void addPrediction(@NonNull Prediction newPrediction) {
            currentPredictions.add(newPrediction);
        }

        @Override
        public void removePrediction(Prediction prediction) {
            currentPredictions.remove(prediction);
        }

        @NonNull
        @Override
        public List<Prediction> getPredictions() {
            Collections.sort(currentPredictions, new Comparator<Prediction>() {
                @Override
                public int compare(Prediction prediction, Prediction t1) {
                    return prediction.getCreationDate().compareTo(t1.getCreationDate());
                }
            });

            return new ArrayList<>(currentPredictions);
        }
    }
}
