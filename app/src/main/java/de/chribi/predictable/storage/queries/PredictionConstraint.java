package de.chribi.predictable.storage.queries;


public interface PredictionConstraint {
    <ResultT> ResultT accept(PredictionConstraintInterpreter<ResultT> interpreter);
}
