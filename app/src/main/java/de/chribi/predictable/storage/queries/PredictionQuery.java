package de.chribi.predictable.storage.queries;


public class PredictionQuery {
    public static PredictionQuery where(PredictionConstraint whereClause)
    {
        return new PredictionQuery(whereClause);
    }

    public static PredictionQuery allPredictions() {
        return new PredictionQuery(CombinedPredictionConstraint.alwaysTrue());
    }

    private PredictionQuery(PredictionConstraint whereClause)
    {
        this.whereClause = whereClause;
    }

    private PredictionConstraint whereClause;
    public PredictionConstraint getWhereClause()
    {
        return whereClause;
    }
}

