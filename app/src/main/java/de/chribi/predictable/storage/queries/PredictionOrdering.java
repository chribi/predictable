package de.chribi.predictable.storage.queries;


public class PredictionOrdering {
    private final PredictionField field;
    private final OrderDirection direction;

    PredictionOrdering(PredictionField field, OrderDirection direction) {
        this.field = field;
        this.direction = direction;
    }

    public PredictionField getField() {
        return field;
    }

    public OrderDirection getDirection() {
        return direction;
    }
}
