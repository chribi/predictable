package de.chribi.predictable.predictionsets;


import android.content.Context;

import de.chribi.predictable.R;

public class DefaultPredictionSetTitles implements PredictionSetTitles {
    private final Context context;

    public DefaultPredictionSetTitles(Context context) {
        this.context = context;
    }

    @Override public String getPredictionSetTitle(PredictionSet set) {
        switch (set) {
            case JUDGED_PREDICTIONS:
                return context.getString(R.string.title_judged_predictions);
            case OVERDUE_PREDICTIONS:
                return context.getString(R.string.title_overdue_predictions);
            case UPCOMING_PREDICTIONS:
                return context.getString(R.string.title_upcoming_predictions);
            default:
                throw new IllegalArgumentException();
        }
    }
}
