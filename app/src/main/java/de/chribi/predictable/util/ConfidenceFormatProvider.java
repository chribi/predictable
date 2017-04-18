package de.chribi.predictable.util;


import android.support.annotation.NonNull;

public interface ConfidenceFormatProvider {
    @NonNull String formatNoConfidence();
    @NonNull String formatConfidence(double confidencePercent);
}
