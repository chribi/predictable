package de.chribi.predictable.storage;


import java.util.Comparator;

import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.storage.queries.OrderDirection;
import de.chribi.predictable.storage.queries.PredictionField;
import de.chribi.predictable.storage.queries.PredictionOrdering;

class PredictionComparator {
    static Comparator<Prediction> fromOrderings(final PredictionOrdering... orderings) {
        return new Comparator<Prediction>() {
            @Override public int compare(Prediction o1, Prediction o2) {
                for (PredictionOrdering ordering : orderings) {
                    PredictionField field = ordering.getField();
                    int fieldResult;
                    if(field == PredictionField.ID) {
                        fieldResult = Long.valueOf(o1.getId()).compareTo(o2.getId());
                    } else if (field == PredictionField.STATE) {
                        fieldResult = o1.getJudgement().getState().compareTo(
                                o2.getJudgement().getState());
                    } else if (field == PredictionField.JUDGEMENT_DATE) {
                        fieldResult = o1.getJudgement().getDate().compareTo(
                                o2.getJudgement().getDate());
                    } else if (field == PredictionField.DUE_DATE) {
                        fieldResult = o1.getDueDate().compareTo(o2.getDueDate());
                    } else {
                        throw new AssertionError("Unknown field: " + field.toString());
                    }

                    if(fieldResult != 0) {
                        if(ordering.getDirection() == OrderDirection.ASCENDING) {
                            return fieldResult;
                        } else {
                            return -fieldResult;
                        }
                    }
                }
                return 0;
            }
        };
    }
}
