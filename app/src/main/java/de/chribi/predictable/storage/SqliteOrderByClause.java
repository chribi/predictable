package de.chribi.predictable.storage;


import android.support.annotation.Nullable;

import de.chribi.predictable.storage.queries.OrderDirection;
import de.chribi.predictable.storage.queries.PredictionField;
import de.chribi.predictable.storage.queries.PredictionOrdering;

class SqliteOrderByClause {
    private SqliteOrderByClause() {}

    public static @Nullable String toSqlite(PredictionOrdering... orderings) {
        if(orderings.length == 0) {
            return null;
        }
        StringBuilder clauseBuilder = new StringBuilder();
        add(orderings[0], clauseBuilder);
        for (int i = 1; i < orderings.length; i++) {
            clauseBuilder.append(", ");
            add(orderings[i], clauseBuilder);
        }
        return clauseBuilder.toString();
    }

    private static void add(PredictionOrdering ordering, StringBuilder clauseBuilder) {
        PredictionField field = ordering.getField();
        if(field == PredictionField.ID) {
            clauseBuilder.append(SqliteSchemas.Predictions.COLUMN_ID);
        } else if (field == PredictionField.JUDGEMENT_DATE) {
            clauseBuilder.append(SqliteSchemas.Predictions.COLUMN_JUDGED_DATE);
        } else if (field == PredictionField.DUE_DATE) {
            clauseBuilder.append(SqliteSchemas.Predictions.COLUMN_DUE_DATE);
        } else if (field == PredictionField.STATE) {
            clauseBuilder.append(SqliteSchemas.Predictions.COLUMN_STATE);
        } else {
            throw new AssertionError("Unknown field: " + field.toString());
        }
        if(ordering.getDirection() == OrderDirection.ASCENDING) {
            clauseBuilder.append(" ASC");
        } else {
            clauseBuilder.append(" DESC");
        }
    }
}
