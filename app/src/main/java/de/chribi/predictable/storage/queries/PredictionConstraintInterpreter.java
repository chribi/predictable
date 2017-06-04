package de.chribi.predictable.storage.queries;


import java.util.Date;

import de.chribi.predictable.data.PredictionState;

public interface PredictionConstraintInterpreter<ResultT> {
    ResultT interpretIdConstraint(Constraint<Long> constraint);

    ResultT interpretPredictionStateConstraint(Constraint<PredictionState> constraint);

    ResultT interpretDueDateConstraint(Constraint<Date> constraint);

    ResultT interpretJudgementDateConstraint(Constraint<Date> constraint);

    ResultT interpretConstraintAll(PredictionConstraint... subConstraints);

    ResultT interpretConstraintAny(PredictionConstraint... subConstraints);
}
