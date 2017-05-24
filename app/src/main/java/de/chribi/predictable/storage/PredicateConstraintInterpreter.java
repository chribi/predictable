package de.chribi.predictable.storage;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.queries.Constraint;
import de.chribi.predictable.storage.queries.PredictionConstraint;
import de.chribi.predictable.storage.queries.PredictionConstraintInterpreter;

public class PredicateConstraintInterpreter
        implements PredictionConstraintInterpreter<Predicate<Prediction>> {

    @Override public Predicate<Prediction> interpretIdConstraint(
            final Constraint<Long> constraint) {
        return new Predicate<Prediction>() {
            @Override public boolean apply(@NonNull Prediction value) {
                return fulfillsConstraint(value.getId(), constraint);
            }
        };
    }

    @Override
    public Predicate<Prediction> interpretPredictionStateConstraint(
            final Constraint<PredictionState> constraint) {
        return new Predicate<Prediction>() {
            @Override public boolean apply(@NonNull Prediction value) {
                return fulfillsConstraint(value.getJudgement().getState(),  constraint);
            }
        };
    }

    @Override public Predicate<Prediction> interpretDueDateConstraint(
            final Constraint<Date> constraint) {
        return new Predicate<Prediction>() {
            @Override public boolean apply(@NonNull Prediction value) {
                return fulfillsConstraint(value.getDueDate(), constraint);
            }
        };
    }

    @Override
    public Predicate<Prediction> interpretConstraintAll(PredictionConstraint... subConstraints) {
        final List<Predicate<Prediction>> predicates = new ArrayList<>(subConstraints.length);
        for (PredictionConstraint constraint : subConstraints) {
            predicates.add(constraint.accept(this));
        }

        return new Predicate<Prediction>() {
            @Override public boolean apply(@NonNull Prediction value) {
                return fulfillsAll(value, predicates);
            }
        };
    }

    @Override
    public Predicate<Prediction> interpretConstraintAny(PredictionConstraint... subConstraints) {
        final List<Predicate<Prediction>> predicates = new ArrayList<>(subConstraints.length);
        for (PredictionConstraint constraint : subConstraints) {
            predicates.add(constraint.accept(this));
        }

        return new Predicate<Prediction>() {
            @Override public boolean apply(@NonNull Prediction value) {
                return fulfillsAny(value, predicates);
            }
        };
    }

    private static boolean fulfillsAll(Prediction prediction, List<Predicate<Prediction>> predicates) {
        for (Predicate<Prediction> predicate : predicates) {
            if(!predicate.apply(prediction)) {
                return false;
            }
        }
        return true;
    }

    private static boolean fulfillsAny(Prediction prediction, List<Predicate<Prediction>> predicates) {
        for (Predicate<Prediction> predicate : predicates) {
            if(predicate.apply(prediction)) {
                return true;
            }
        }
        return false;
    }

    private static <T> boolean fulfillsConstraint(Comparable<T> value, Constraint<T> constraint) {
        int comparisonResult = value.compareTo(constraint.getReferenceValue());
        switch (constraint.getRelation()) {
            case EQUAL:
                return comparisonResult == 0;
            case NOT_EQUAL:
                return comparisonResult != 0;
            case GREATER:
                return comparisonResult > 0;
            case GREATER_OR_EQUAL:
                return comparisonResult >= 0;
            case LESS:
                return comparisonResult < 0;
            case LESS_OR_EQUAL:
                return comparisonResult <= 0;
            default:
                throw new AssertionError("Unknown Relation: " + constraint.getRelation().toString());
        }
    }
}
