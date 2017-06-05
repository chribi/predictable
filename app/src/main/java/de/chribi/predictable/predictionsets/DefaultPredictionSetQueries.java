package de.chribi.predictable.predictionsets;


import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.queries.PredictionQuery;
import de.chribi.predictable.util.DateTimeProvider;

import static de.chribi.predictable.storage.queries.PredictionField.DUE_DATE;
import static de.chribi.predictable.storage.queries.PredictionField.JUDGEMENT_DATE;
import static de.chribi.predictable.storage.queries.PredictionField.STATE;

public class DefaultPredictionSetQueries implements PredictionSetQueries{
    private DateTimeProvider dateTime;

    public DefaultPredictionSetQueries(DateTimeProvider dateTime) {
        this.dateTime = dateTime;
    }

    @Override public PredictionQuery getPredictionSetQuery(PredictionSet set) {
        switch (set) {
            case OVERDUE_PREDICTIONS:
                return PredictionQuery
                        .where(
                                STATE.equalTo(PredictionState.Open),
                                DUE_DATE.before(dateTime.getCurrentDateTime()))
                        .orderBy(
                                DUE_DATE.Ascending);
            case JUDGED_PREDICTIONS:
                return PredictionQuery
                        .where(
                                STATE.notEqualTo(PredictionState.Open))
                        .orderBy(
                                JUDGEMENT_DATE.Descending);
            case UPCOMING_PREDICTIONS:
                return PredictionQuery
                        .where(
                                STATE.equalTo(PredictionState.Open),
                                DUE_DATE.after(dateTime.getCurrentDateTime()))
                        .orderBy(
                                DUE_DATE.Ascending);
            default:
                throw new IllegalArgumentException();
        }
    }
}
