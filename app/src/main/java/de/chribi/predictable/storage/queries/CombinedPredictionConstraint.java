package de.chribi.predictable.storage.queries;


class CombinedPredictionConstraint implements PredictionConstraint {
    private enum Operator { AND, OR }

    private static final PredictionConstraint constantTrue = new CombinedPredictionConstraint(Operator.AND);
    private static final PredictionConstraint constantFalse = new CombinedPredictionConstraint(Operator.OR);

    public static PredictionConstraint and(PredictionConstraint... constraints) {
        if(constraints.length == 0) {
            return constantTrue;
        } else if(constraints.length == 1) {
            return constraints[0];
        } else {
            return new CombinedPredictionConstraint(Operator.AND, constraints);
        }
    }

    public static PredictionConstraint or(PredictionConstraint... constraints) {
        if(constraints.length == 0) {
            return constantFalse;
        } else if(constraints.length == 1) {
            return constraints[0];
        } else {
            return new CombinedPredictionConstraint(Operator.OR, constraints);
        }
    }

    static PredictionConstraint alwaysTrue() {
        return constantTrue;
    }

    private final Operator operator;
    private final PredictionConstraint[] subConstraints;

    private CombinedPredictionConstraint(Operator operator, PredictionConstraint... subConstraints) {
        this.operator = operator;
        this.subConstraints = subConstraints;
    }

    @Override public <TResult> TResult accept(PredictionConstraintInterpreter<TResult> interpreter) {
        if(operator == Operator.AND) {
            return interpreter.interpretConstraintAll(subConstraints);
        } else {
            return interpreter.interpretConstraintAny(subConstraints);
        }
    }
}
