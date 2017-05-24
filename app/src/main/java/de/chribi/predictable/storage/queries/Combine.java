package de.chribi.predictable.storage.queries;


public class Combine {
    private Combine() {}

    public static PredictionConstraint and(PredictionConstraint... constraints) {
        return CombinedPredictionConstraint.and(constraints);
    }

    public static PredictionConstraint or(PredictionConstraint... constraints) {
        return CombinedPredictionConstraint.or(constraints);
    }
}
