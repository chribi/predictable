package de.chribi.predictable.storage;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.queries.Constraint;
import de.chribi.predictable.storage.queries.PredictionConstraint;
import de.chribi.predictable.storage.queries.PredictionConstraintInterpreter;
import de.chribi.predictable.storage.queries.Relation;

class SqliteConstraintInterpreter implements PredictionConstraintInterpreter<SqliteWhereClause> {
    private static final String OP_OR  = " OR ";
    private static final String OP_AND  = " AND ";
    @Override
    public SqliteWhereClause interpretIdConstraint(Constraint<Long> constraint) {
        String clause = clause(SqliteSchemas.Predictions.COLUMN_ID, constraint.getRelation());
        String[] args = new String[] { constraint.getReferenceValue().toString() };
        return new SqliteWhereClause(clause, args);
    }

    @Override
    public SqliteWhereClause interpretPredictionStateConstraint(Constraint<PredictionState> constraint) {
        String clause = clause(SqliteSchemas.Predictions.COLUMN_STATE, constraint.getRelation());
        String[] args = new String[] { String.valueOf(constraint.getReferenceValue().getStoredValue()) };
        return new SqliteWhereClause(clause, args);
    }

    @Override
    public SqliteWhereClause interpretDueDateConstraint(Constraint<Date> constraint) {
        String clause = clause(SqliteSchemas.Predictions.COLUMN_DUE_DATE, constraint.getRelation());
        String[] args = new String[] { String.valueOf(constraint.getReferenceValue().getTime()) };
        return new SqliteWhereClause(clause, args);
    }

    @Override
    public SqliteWhereClause interpretJudgementDateConstraint(Constraint<Date> constraint) {
        String clause = clause(SqliteSchemas.Predictions.COLUMN_JUDGED_DATE, constraint.getRelation());
        String[] args = new String[] { String.valueOf(constraint.getReferenceValue().getTime()) };
        return new SqliteWhereClause(clause, args);
    }

    @Override
    public SqliteWhereClause interpretConstraintAll(PredictionConstraint... subConstraints) {
        if(subConstraints.length == 0) {
            return new SqliteWhereClause("1=1", new String[0]);
        }
        return combinedConstraintQuery(OP_AND, subConstraints);
    }

    @Override
    public SqliteWhereClause interpretConstraintAny(PredictionConstraint... subConstraints) {
        if(subConstraints.length == 0) {
            return new SqliteWhereClause("0=1", new String[0]);
        }
        return combinedConstraintQuery(OP_OR, subConstraints);
    }

    private SqliteWhereClause combinedConstraintQuery(
            String op, PredictionConstraint[] subConstraints) {
        StringBuilder sb = new StringBuilder(subConstraints.length * 40);
        List<String> arguments = new ArrayList<>(subConstraints.length);
        sb.append('(');
        SqliteWhereClause subClause0Sql = subConstraints[0].accept(this);
        sb.append(subClause0Sql.getClause());

        arguments.addAll(Arrays.asList(subClause0Sql.getArgs()));
        for (int i = 1; i < subConstraints.length; i++) {
            sb.append(op);
            SqliteWhereClause subClauseSql = subConstraints[i].accept(this);
            sb.append(subClauseSql.getClause());
            arguments.addAll(Arrays.asList(subClauseSql.getArgs()));
        }
        sb.append(')');
        String[] argsArray = new String[arguments.size()];
        argsArray = arguments.toArray(argsArray);
        return new SqliteWhereClause(sb.toString(), argsArray);
    }

    private String clause(String dbColumn, Relation relation) {
        return dbColumn + interpretRelation(relation) + "?";
    }

    private static String interpretRelation(@NonNull Relation relation) {
        switch (relation) {
            case EQUAL:
                return "=";
            case NOT_EQUAL:
                return "!=";
            case LESS:
                return "<";
            case LESS_OR_EQUAL:
                return "<=";
            case GREATER:
                return ">";
            case GREATER_OR_EQUAL:
                return ">=";
            default:
                throw new AssertionError("Unknown Relation: " + relation.toString());
        }
    }
}
