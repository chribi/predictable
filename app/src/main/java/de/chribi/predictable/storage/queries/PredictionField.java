package de.chribi.predictable.storage.queries;


import java.util.Date;

import de.chribi.predictable.data.PredictionState;

public class PredictionField<T> {
    public static PredictionField<Long> ID = new PredictionField<>();
    public static PredictionField<PredictionState> STATE = new PredictionField<>();
    public static PredictionField<Date> DUE_DATE = new PredictionField<>();
    public static PredictionField<Date> JUDGEMENT_DATE = new PredictionField<>();


    private PredictionField() { }

    public final PredictionOrdering Ascending = new PredictionOrdering(this, OrderDirection.ASCENDING);

    public PredictionConstraint equalTo(T value) {
        return PredictionFieldConstraint.onField(this, Constraint.equal(value));
    }

    public PredictionConstraint notEqualTo(T value) {
        return PredictionFieldConstraint.onField(this, Constraint.notEqual(value));
    }

    public PredictionConstraint greaterThan(T value) {
        return PredictionFieldConstraint.onField(this, Constraint.greater(value));
    }

    public PredictionConstraint after(T value) {
        return greaterThan(value);
    }

    public PredictionConstraint lesserThan(T value) {
        return PredictionFieldConstraint.onField(this, Constraint.lesser(value));
    }

    public PredictionConstraint before(T value) {
        return lesserThan(value);
    }
}
