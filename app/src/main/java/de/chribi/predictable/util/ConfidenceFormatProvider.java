package de.chribi.predictable.util;


public interface ConfidenceFormatProvider {
    String formatNoConfidence();
    String formatConfidence(double confidencePercent);
}
