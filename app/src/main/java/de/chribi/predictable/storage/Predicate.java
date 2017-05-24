package de.chribi.predictable.storage;


import android.support.annotation.NonNull;

interface Predicate<T> {
    boolean apply(@NonNull T value);
}
