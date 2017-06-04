package de.chribi.predictable.storage.queries;


public class PredictionQuery {
    public static PredictionQuery where(PredictionConstraint whereClause) {
        return new PredictionQuery(whereClause);
    }

    public static PredictionQuery where(PredictionConstraint... whereClauses) {
        return new PredictionQuery(Combine.and(whereClauses));
    }

    public static PredictionQuery allPredictions() {
        return new PredictionQuery(CombinedPredictionConstraint.alwaysTrue());
    }

    private PredictionConstraint whereClause;
    private PredictionOrdering[] orderings;
    private PredictionQuery(PredictionConstraint whereClause)
    {
        this.whereClause = whereClause;
        this.orderings = new PredictionOrdering[0];
    }

    public PredictionConstraint getWhereClause()
    {
        return whereClause;
    }

    public PredictionQuery orderBy(PredictionOrdering... orderings) {
        this.orderings = orderings;
        return this;
    }

    public PredictionOrdering[] getOrderByClauses() {
        return orderings;
    }
}

