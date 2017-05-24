package de.chribi.predictable.storage.queries;


import android.support.annotation.NonNull;

public class Constraint<T> {

    static <T> Constraint<T> equal(T value) {
        return new Constraint<>(Relation.EQUAL, value);
    }

    static <T> Constraint<T> notEqual(T value) {
        return new Constraint<>(Relation.NOT_EQUAL, value);
    }

    static <T> Constraint<T> greater(T value) {
        return new Constraint<>(Relation.GREATER, value);
    }

    static <T> Constraint<T> greaterOrEqual(T value) {
        return new Constraint<>(Relation.GREATER_OR_EQUAL, value);
    }

    static <T> Constraint<T> lesser(T value) {
        return new Constraint<>(Relation.LESS, value);
    }

    static <T> Constraint<T> lesserOrEqual(T value) {
        return new Constraint<>(Relation.LESS_OR_EQUAL, value);
    }

    private final Relation relation;
    private final T referenceValue;

    private Constraint(@NonNull Relation relation, @NonNull T reference)
    {
        this.relation = relation;
        this.referenceValue = reference;
    }

    public @NonNull Relation getRelation() {
        return relation;
    }

    public @NonNull T getReferenceValue() {
        return referenceValue;
    }
}
