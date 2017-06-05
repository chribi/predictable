package de.chribi.predictable.predictionsets;


import de.chribi.predictable.storage.queries.PredictionQuery;

public interface PredictionSetQueries {
    PredictionQuery getPredictionSetQuery(PredictionSet set);
}
