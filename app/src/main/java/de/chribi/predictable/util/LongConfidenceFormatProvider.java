package de.chribi.predictable.util;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Locale;

import javax.inject.Inject;

import de.chribi.predictable.R;

public class LongConfidenceFormatProvider implements ConfidenceFormatProvider {
    private Context context;

    @Inject
    public LongConfidenceFormatProvider(Context context) {
        this.context = context;
    }
    @Override
    public @NonNull String formatNoConfidence() {
        return context.getString(R.string.text_no_confidence_long);
    }

    @Override
    public @NonNull String formatConfidence(double confidencePercent) {
        return String.format(Locale.getDefault(), "%.1f %%", confidencePercent);
    }
}
