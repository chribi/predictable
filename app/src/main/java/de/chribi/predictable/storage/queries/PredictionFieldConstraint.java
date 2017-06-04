package de.chribi.predictable.storage.queries;


import java.util.Date;

import de.chribi.predictable.data.PredictionState;

class PredictionFieldConstraint implements PredictionConstraint {
    static <T> PredictionConstraint onField(PredictionField<T> field, Constraint<T> constraint) {
        return new PredictionFieldConstraint(field, constraint);
    }

    private final PredictionField field;
    private final Constraint constraint;

    private PredictionFieldConstraint(PredictionField field, Constraint constraint)
    {
        this.field = field;
        this.constraint = constraint;
    }

    @SuppressWarnings("unchecked") // types are ensured by the static factory methods
    @Override
    public <ResultT> ResultT accept(PredictionConstraintInterpreter<ResultT> interpreter) {
        if(field == PredictionField.ID) {
            return interpreter.interpretIdConstraint((Constraint<Long>)constraint);
        } else if (field == PredictionField.STATE) {
            return interpreter.interpretPredictionStateConstraint(
                    (Constraint<PredictionState>) constraint);
        } else if (field == PredictionField.DUE_DATE) {
            return interpreter.interpretDueDateConstraint((Constraint<Date>) constraint);
        } else if (field == PredictionField.JUDGEMENT_DATE)  {
            return interpreter.interpretJudgementDateConstraint((Constraint<Date>) constraint);
        } else {
            throw new AssertionError("Can not filter by field: " + field.toString());
        }
    }
}
